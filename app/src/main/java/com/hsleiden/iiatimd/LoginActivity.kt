package com.hsleiden.iiatimd

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.microsoft.graph.models.User
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalServiceException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;


class LoginActivity : AppCompatActivity() {

    private var mIsSignedIn = false
    private var mUserName: String? = null
    private var mUserEmail: String? = null
    private var mUserStNumber: String? = null
    private var mUserTimeZone: String? = null
    private var mAuthHelper: AuthenticationHelper? = null

    private var sharedPrefFile: String = "mUserPreference"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.buttonLogin).setOnClickListener {
            signIn()
        }

        // Restore state
        sharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        if (sharedPreferences.contains("mIsSignedIn")) {
            mIsSignedIn = sharedPreferences.getBoolean("mIsSignedIn", false)
            mUserName = sharedPreferences.getString("mUserName", "null")
            mUserEmail = sharedPreferences.getString("mUserEmail", "null")
            mUserStNumber = sharedPreferences.getString("mUserStNumber", "null")
            mUserTimeZone = sharedPreferences.getString("mUserTimeZone", "null")
        }
        setSignedInState(mIsSignedIn)


        // Get the authentication helper
        AuthenticationHelper.getInstance(applicationContext)
            .thenAccept { authHelper: AuthenticationHelper ->
                mAuthHelper = authHelper
                if (!mIsSignedIn) {
                    doSilentSignIn(false)
                }
            }
            .exceptionally { exception: Throwable? ->
                Log.e("AUTH", "Error creating auth helper", exception)
                null
            }
    }

    // Update the menu and get the user's name and email
    @SuppressLint("SetTextI18n")
    private fun setSignedInState(isSignedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("mIsSignedIn", isSignedIn)

        // Set the user details
        when {
            isSignedIn -> {
                startHomeActivity()
            }
            else -> {
                editor.putString("mUserStNumber", "null")
                editor.putString("mUserName", "null")
                editor.putString("mUserEmail", "null")
                editor.putString("mUserTimeZone", "null")
            }
        }
        editor.apply()
    }

    private fun startHomeActivity() {
        startActivity(Intent(applicationContext, HomeActivity::class.java))
        finish()
    }

    private fun signIn() {
        // Attempt silent sign in first
        // if this fails, the callback will handle doing
        // interactive sign in
        doSilentSignIn(true)
    }

    // Silently sign in - used if there is already a
    // user account in the MSAL cache
    private fun doSilentSignIn(shouldAttemptInteractive: Boolean) {
        mAuthHelper!!.acquireTokenSilently()
            .thenAccept { authenticationResult: IAuthenticationResult ->
                handleSignInSuccess(
                    authenticationResult
                )
            }
            .exceptionally { exception: Throwable ->
                // Check the type of exception and handle appropriately
                val cause = exception.cause
                if (cause is MsalUiRequiredException) {
                    Log.d("AUTH", "Interactive login required")
                    if (shouldAttemptInteractive) doInteractiveSignIn()
                } else if (cause is MsalClientException) {
                    val clientException: MsalClientException = cause
                    if (clientException.errorCode === "no_current_account" ||
                        clientException.errorCode === "no_account_found"
                    ) {
                        Log.d("AUTH", "No current account, interactive login required")
                        if (shouldAttemptInteractive) doInteractiveSignIn()
                        // GoToHome
//                        startHomeActivity()
                    }
                } else {
                    handleSignInFailure(cause)
                }
                null
            }
    }

    // Prompt the user to sign in
    private fun doInteractiveSignIn() {
        mAuthHelper!!.acquireTokenInteractively(this)
            .thenAccept { authenticationResult: IAuthenticationResult ->
                handleSignInSuccess(
                    authenticationResult
                )
            }
            .exceptionally { exception: Throwable? ->
                handleSignInFailure(exception)
                null
            }
    }

    // Handles the authentication result
    private fun handleSignInSuccess(authenticationResult: IAuthenticationResult) {
        // Log the token for debug purposes
        val accessToken = authenticationResult.accessToken
        Log.d("AUTH", String.format("Access token: %s", accessToken))

        // Get Graph client and get user
        val graphHelper = GraphHelper.getInstance()
        graphHelper.user
            .thenAccept { user: User ->
                val editor = sharedPreferences.edit()
                editor.putString("mUserName", user.displayName)
                editor.putString("mUserEmail",  if (user.mail == null) user.userPrincipalName else user.mail)
                editor.putString("mUserStNumber", user.employeeId)
                editor.putString("mUserTimeZone", user.mailboxSettings?.timeZone)
                editor.apply()
                runOnUiThread(fun() {
                    setSignedInState(true)
                    startHomeActivity()
                })
            }
            .exceptionally { exception: Throwable? ->
                Log.e("AUTH", "Error getting /me", exception)
                runOnUiThread {
                    setSignedInState(false)
                }
                null
            }
    }

    private fun handleSignInFailure(exception: Throwable?) {
        when (exception) {
            is MsalServiceException -> {
                // Exception when communicating with the auth server, likely config issue
                Log.e("AUTH", "Service error authenticating", exception)
            }
            is MsalClientException -> {
                // Exception inside MSAL
                Log.e("AUTH", "Client error authenticating", exception)
            }
            else -> {
                Log.e("AUTH", "Unhandled exception authenticating", exception)
            }
        }
    }

}