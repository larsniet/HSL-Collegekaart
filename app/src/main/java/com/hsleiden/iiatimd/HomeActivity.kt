package com.hsleiden.iiatimd

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.microsoft.graph.models.User
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalServiceException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;
import java.util.function.Consumer


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var mDrawer: DrawerLayout? = null
    private var mNavigationView: NavigationView? = null
    private var mHeaderView: View? = null
    private var mIsSignedIn = false
    private var mUserName: String? = null
    private var mUserEmail: String? = null
    private var mUserTimeZone: String? = null
    private var mAuthHelper: AuthenticationHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Set the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        mDrawer = findViewById(R.id.drawer_layout)

        // Add the hamburger menu icon
        val toggle = ActionBarDrawerToggle(
            this, mDrawer, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        mDrawer?.addDrawerListener(toggle)
        toggle.syncState()
        mNavigationView = findViewById(R.id.nav_view)

        // Set user name and email
        mHeaderView = mNavigationView?.getHeaderView(0)
        setSignedInState(mIsSignedIn)

        // Listen for item select events on menu
        mNavigationView?.setNavigationItemSelectedListener(this)
        if (savedInstanceState == null) {
            // Load the home fragment by default on startup
            openHomeFragment(mUserName)
        } else {
            // Restore state
            mIsSignedIn = savedInstanceState.getBoolean(SAVED_IS_SIGNED_IN)
            mUserName = savedInstanceState.getString(SAVED_USER_NAME)
            mUserEmail = savedInstanceState.getString(SAVED_USER_EMAIL)
            mUserTimeZone = savedInstanceState.getString(SAVED_USER_TIMEZONE)
            setSignedInState(mIsSignedIn)
        }

        showProgressBar()
        // Get the authentication helper
        AuthenticationHelper.getInstance(applicationContext)
            .thenAccept { authHelper: AuthenticationHelper ->
                mAuthHelper = authHelper
                if (!mIsSignedIn) {
                    doSilentSignIn(false)
                } else {
                    hideProgressBar()
                }
            }
            .exceptionally { exception: Throwable? ->
                Log.e("AUTH", "Error creating auth helper", exception)
                null
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_IS_SIGNED_IN, mIsSignedIn)
        outState.putString(SAVED_USER_NAME, mUserName)
        outState.putString(SAVED_USER_EMAIL, mUserEmail)
        outState.putString(SAVED_USER_TIMEZONE, mUserTimeZone)
    }

    override fun onNavigationItemSelected(@NonNull menuItem: MenuItem): Boolean {
        // Load the fragment that corresponds to the selected item
        when (menuItem.itemId) {
            R.id.nav_home -> openHomeFragment(mUserName)
            R.id.nav_calendar -> openCalendarFragment(mUserTimeZone!!)
            R.id.nav_create_event -> openNewEventFragment(mUserTimeZone!!)
            R.id.nav_signin -> signIn()
            R.id.nav_signout -> signOut()
        }
        mDrawer!!.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (mDrawer!!.isDrawerOpen(GravityCompat.START)) {
            mDrawer!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun showProgressBar() {
        val container = findViewById<FrameLayout>(R.id.fragment_container)
        val progressBar = findViewById<ProgressBar>(R.id.progressbar)
        container.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        val container = findViewById<FrameLayout>(R.id.fragment_container)
        val progressBar = findViewById<ProgressBar>(R.id.progressbar)
        progressBar.visibility = View.GONE
        container.visibility = View.VISIBLE
    }

    // Update the menu and get the user's name and email
    @SuppressLint("SetTextI18n")
    private fun setSignedInState(isSignedIn: Boolean) {
        mIsSignedIn = isSignedIn
        mNavigationView!!.menu.clear()
        mNavigationView!!.inflateMenu(R.menu.drawer_menu)
        val menu = mNavigationView!!.menu

        // Hide/show the Sign in, Calendar, and Sign Out buttons
        when {
            isSignedIn -> {
                menu.removeItem(R.id.nav_signin);
            }
            else -> {
                menu.removeItem(R.id.nav_home);
                menu.removeItem(R.id.nav_calendar);
                menu.removeItem(R.id.nav_create_event);
                menu.removeItem(R.id.nav_signout);
            }
        }

        // Set the user name and email in the nav drawer
        val userName = mHeaderView!!.findViewById<TextView>(R.id.user_name)
        val userEmail = mHeaderView!!.findViewById<TextView>(R.id.user_email)
        when {
            isSignedIn -> {
                // For testing
                userName.text = mUserName
                userEmail.text = mUserEmail
            }
            else -> {
                mUserName = null
                mUserEmail = null
                mUserTimeZone = null
                userName.text = "Please sign in"
                userEmail.text = ""
            }
        }
    }

    // Load the "Home" fragment
    private fun openHomeFragment(userName: String?) {
        val fragment = HomeFragment.createInstance(userName)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        mNavigationView?.setCheckedItem(R.id.nav_home)
    }

    // Load the "Calendar" fragment
    private fun openCalendarFragment(timeZone: String) {
        val fragment = CalendarFragment.createInstance(timeZone)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        mNavigationView?.setCheckedItem(R.id.nav_calendar)
    }

    // Load the "New Event" fragment
    private fun openNewEventFragment(timeZone: String) {
        val fragment = NewEventFragment.createInstance(timeZone)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        mNavigationView?.setCheckedItem(R.id.nav_create_event)
    }

    private fun signIn() {
        showProgressBar()
        // Attempt silent sign in first
        // if this fails, the callback will handle doing
        // interactive sign in
        doSilentSignIn(true)
    }

    private fun signOut() {
        mAuthHelper!!.signOut()
        setSignedInState(false)
        openHomeFragment(mUserName)
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
                    val clientException: MsalClientException? = cause as MsalClientException?
                    if (clientException != null) {
                        if (clientException.errorCode === "no_current_account" ||
                            clientException.errorCode === "no_account_found"
                        ) {
                            Log.d("AUTH", "No current account, interactive login required")
                            if (shouldAttemptInteractive) doInteractiveSignIn()
                        }
                    }
                } else {
                    handleSignInFailure(cause)
                }
                hideProgressBar()
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
                hideProgressBar()
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
            .thenAccept(Consumer { user: User ->
                mUserName = user.displayName
                mUserEmail = if (user.mail == null) user.userPrincipalName else user.mail
                mUserTimeZone = user.mailboxSettings?.timeZone
                runOnUiThread(fun() {
                    hideProgressBar()
                    setSignedInState(true)
                    openHomeFragment(mUserName)
                })
            })
            .exceptionally { exception: Throwable? ->
                Log.e("AUTH", "Error getting /me", exception)
                runOnUiThread {
                    hideProgressBar()
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
                // Exception inside MSAL, more info inside MsalError.java
                Log.e("AUTH", "Client error authenticating", exception)
            }
            else -> {
                Log.e("AUTH", "Unhandled exception authenticating", exception)
            }
        }
    }

    companion object {
        private const val SAVED_IS_SIGNED_IN = "isSignedIn"
        private const val SAVED_USER_NAME = "userName"
        private const val SAVED_USER_EMAIL = "userEmail"
        private const val SAVED_USER_TIMEZONE = "userTimeZone"
    }
}