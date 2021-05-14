package com.hsleiden.iiatimd

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import java.util.*


class HomeActivity : AppCompatActivity() {

    private var mIsSignedIn = false
    private var mUserName: String? = null
    private var mUserEmail: String? = null
    private var mUserBirthday: String? = null
    private var mUserEducation: String? = null
    private var mUserValid: String? = null
    private var mUserStNumber: String? = null
    private var mUserTimeZone: String? = null
    private var mAuthHelper: AuthenticationHelper? = null

    private var sharedPrefFile: String = "mUserPreference"
    private lateinit var sharedPreferences: SharedPreferences

    private var onSettingsFragment: Boolean = false
    private var firstLoad: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Restore state
        sharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        if (sharedPreferences.contains("mIsSignedIn")) {
            mIsSignedIn = sharedPreferences.getBoolean("mIsSignedIn", false)
            mUserName = sharedPreferences.getString("mUserName", "null")
            mUserEmail = sharedPreferences.getString("mUserEmail", "null")
            mUserBirthday = sharedPreferences.getString("mUserBirthday", "null")
            mUserEducation = sharedPreferences.getString("mUserEducation", "null")
            mUserValid = sharedPreferences.getString("mUserValid", "null")
            mUserStNumber = sharedPreferences.getString("mUserStNumber", "null")
            mUserTimeZone = sharedPreferences.getString("mUserTimeZone", "null")
        }

        // Set the current signedin state and check if the user has access to HomeActivity
        setSignedInState(mIsSignedIn)

//        setJWTAccessToken()

        // Setup visual data for the activity
        findViewById<TextView>(R.id.userStNumber).text = mUserStNumber

        // Setup for Bottom Navigation
        openHomeFragment(mUserName, mUserBirthday, mUserEducation, mUserValid, mUserStNumber)
        findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_home -> {
                        openHomeFragment(
                            mUserName,
                            mUserBirthday,
                            mUserEducation,
                            mUserValid,
                            mUserStNumber
                        )
                        setContent(false)
                        true
                    }
                    R.id.menu_settings -> {
                        openSettingsFragment(mUserName, mUserEmail, mUserStNumber)
                        setContent(true)
                        true
                    }
                    else -> false
                }
            }

        // Listen to logout click in top navigation on settings page
        findViewById<ImageView>(R.id.logoutButton)
            .setOnClickListener {
                signOut()
            };

        // Get the authentication helper
        AuthenticationHelper.getInstance(applicationContext)
                .thenAccept { authHelper: AuthenticationHelper ->
                    mAuthHelper = authHelper
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
//                setJWTAccessToken()

            } else -> {
                editor.putString("mUserStNumber", "null")
                editor.putString("mUserName", "null")
                editor.putString("mUserEmail", "null")
                editor.putString("mUserBirthday", "null")
                editor.putString("mUserEducation", "null")
                editor.putString("mUserValid", "null")
                editor.putString("mUserTimeZone", "null")
                editor.apply()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
        }
        editor.apply()
    }


    private fun setJWTAccessToken() {
        // Create Volley queue
        val queue = Volley.newRequestQueue(this)

        // Setup URL for requests
        val loginUserURL = "http://192.168.0.159:8000/api/auth/login"
        val createUserURL = "http://192.168.0.159:8000/api/auth/register"

        // Request a string response from the provided URL.
        val loginUserReq : StringRequest =
                object : StringRequest(Method.POST, loginUserURL,
                    Response.Listener { response ->
                        try {
                            val jsonObject = JSONObject(response)
                            Log.e("Data: ", jsonObject.toString())
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.e("Error: ", response)
                        }
                    },
                    Response.ErrorListener { error ->
                        Log.d("API", "error => $error")
                    }
                ){
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["name"] = "Lars"
                        params["email"] = "lvdnbusiness@gmail.com"
                        params["password"] = "password"
                        params["password_confirmation"] = "password"
                        return params
                    }
                }
        queue.add(loginUserReq)

        val createUserReq : StringRequest =
                object : StringRequest(Method.POST, createUserURL,
                    Response.Listener { response ->
                        try {
                            val jsonObject = JSONObject(response)
                            Log.e("Data: ", jsonObject.toString())
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.e("Error: ", response)
                        }
                    },
                    Response.ErrorListener { error ->
                        Log.d("API", "error => $error")
                    }
                ){
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["name"] = "Lars"
                        params["email"] = "lvdnbusiness@gmail.com"
                        params["password"] = "password"
                        params["password_confirmation"] = "password"
                        return params
                    }
                }
        queue.add(createUserReq)
    }

    // Load the "Settings" fragment
    private fun openSettingsFragment(userName: String?, userEmail: String?, userStNumber: String?) {
        val fragment = SettingsFragment.createInstance(userName, userEmail, userStNumber)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Load the "Home" fragment
    private fun openHomeFragment(
        userName: String?,
        userBirthday: String?,
        userEducation: String?,
        userValid: String?,
        userStNumber: String?
    ) {
        val fragment = HomeFragment.createInstance(
            userName,
            userBirthday,
            userEducation,
            userValid,
            userStNumber
        )
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        if (firstLoad) {
            val logoutButtonIcon = findViewById<ImageView>(R.id.logoutButton)
            logoutButtonIcon.animate().translationX(1000F).duration = 0
            firstLoad = false
        }
    }

    // Sign user out
    fun signOut() {
        mAuthHelper!!.signOut()
        setSignedInState(false)
    }

    // Update current page title and icon
    private fun setContent(goToSettingsFragment: Boolean) {

        val currentPageIcon = findViewById<ImageView>(R.id.currentPageIcon)
        val currentPageTitle = findViewById<TextView>(R.id.currentPageTitle)
        val logoHeader = findViewById<ImageView>(R.id.logoHeader)
        val userStNumber = findViewById<TextView>(R.id.userStNumber)
        val stNumberIcon = findViewById<ImageView>(R.id.stNumberIcon)
        val logoutButtonIcon = findViewById<ImageView>(R.id.logoutButton)

        if (goToSettingsFragment) {

            // Set currently on profile fragment to true
            onSettingsFragment = true

            // Execute animations
            currentPageIcon.animate().translationX(-1000F).duration = 500
            currentPageTitle.animate().translationX(-1000F).duration = 500
            userStNumber.animate().translationX(1000F).duration = 500
            stNumberIcon.animate().translationX(1000F).duration = 500
            logoutButtonIcon.animate().translationX(0F).duration = 500
            logoHeader.animate().translationY(-220F).duration = 500
            imageViewAnimatedChange(
                applicationContext, logoHeader, BitmapFactory.decodeResource(
                    applicationContext.resources,
                    R.drawable.logo_transparent
                )
            )
            logoHeader.setBackgroundResource(0)

        } else {

            // Reset to default position if the user came from the profile fragment
            if (onSettingsFragment) {
                logoHeader.animate().translationY(0F).duration = 500
                userStNumber.animate().translationX(0F).duration = 500
                stNumberIcon.animate().translationX(0F).duration = 500
                currentPageIcon.animate().translationX(0F).duration = 500
                logoutButtonIcon.animate().translationX(1000F).duration = 500
                currentPageTitle.animate().translationX(0F).duration = 500
                imageViewAnimatedChange(
                    applicationContext, logoHeader, BitmapFactory.decodeResource(
                        applicationContext.resources,
                        R.drawable.logo_header
                    )
                )
                Handler(Looper.getMainLooper()).postDelayed({
                    logoHeader.setBackgroundResource(R.drawable.header_shadow)
                }, 600)
            }
            onSettingsFragment = false


        }


    }

    private fun imageViewAnimatedChange(c: Context?, v: ImageView, new_image: Bitmap?) {
        val animOut = AnimationUtils.loadAnimation(c, android.R.anim.fade_out)
        val animIn = AnimationUtils.loadAnimation(c, android.R.anim.fade_in)
        animOut.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                v.setImageBitmap(new_image)
                animIn.setAnimationListener(object : AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationRepeat(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {}
                })
                v.startAnimation(animIn)
            }
        })
        v.startAnimation(animOut)
    }

}