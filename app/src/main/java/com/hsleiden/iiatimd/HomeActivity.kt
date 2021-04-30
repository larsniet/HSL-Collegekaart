package com.hsleiden.iiatimd

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import java.nio.charset.Charset
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
                        openHomeFragment(mUserName, mUserBirthday, mUserEducation, mUserValid, mUserStNumber)
                        setContent("Collegekaart", R.drawable.ic_menu_card)
                        true
                    }
                    R.id.menu_profile -> {
                        openProfileFragment(mUserName)
                        setContent("Mijn profiel", R.drawable.ic_menu_user)
                        true
                    }
                    else -> false
                }
            }

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
                                Log.e("Data: ", jsonObject.toString() )
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
        queue.add(loginUserReq )

        val createUserReq : StringRequest =
                object : StringRequest(Method.POST, createUserURL,
                        Response.Listener { response ->
                            try {
                                val jsonObject = JSONObject(response)
                                Log.e("Data: ", jsonObject.toString() )
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

    // Load the "Profile" fragment
    private fun openProfileFragment(userName: String?) {
        val fragment = ProfileFragment.createInstance(userName)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Load the "Home" fragment
    private fun openHomeFragment(userName: String?, userBirthday: String?, userEducation: String?, userValid: String?, userStNumber: String?) {
        val fragment = HomeFragment.createInstance(userName, userBirthday, userEducation, userValid, userStNumber)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Sign user out
    fun signOut() {
        mAuthHelper!!.signOut()
        setSignedInState(false)
    }

    // Update current page title and icon
    private fun setContent(content: String, icon: Int) {

        val currentPageIcon = findViewById<ImageView>(R.id.currentPageIcon)
        val currentPageTitle = findViewById<TextView>(R.id.currentPageTitle)

        var animation = AnimationUtils.loadAnimation(this, R.anim.slide_out)
        currentPageIcon.startAnimation(animation)
        currentPageTitle.startAnimation(animation)
        Handler(Looper.getMainLooper()).postDelayed({
            currentPageTitle.text = content
            currentPageIcon.setImageDrawable(ContextCompat.getDrawable(applicationContext, icon))

            animation = AnimationUtils.loadAnimation(this, R.anim.slide_in)
            currentPageIcon.startAnimation(animation)
            currentPageTitle.startAnimation(animation)

        }, 500)
    }

}