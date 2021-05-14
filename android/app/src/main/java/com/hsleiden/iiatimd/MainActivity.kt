package com.hsleiden.iiatimd

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.nfc.NfcAdapter
import android.nfc.NfcAdapter.ACTION_TECH_DISCOVERED
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var onboardingItemsAdapter: OnboardingItemsAdapter
    private lateinit var indicatorsContainer: LinearLayout

    private var sharedPrefFile: String = "mUserPreference"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("mIsSignedIn", false)) {
            navigateToLoginActivity(false)
        }

        setContentView(R.layout.activity_main)
        setOnboardingItems()
        setupIndicators()
        setCurrentIndicator(0)
    }

    private fun setOnboardingItems() {
        onboardingItemsAdapter = OnboardingItemsAdapter(
                listOf(
                        OnboardingItem(
                                onboardingImage = R.drawable.card,
                                title = "Collegekaart synchroniseren",
                                description = "Door simpelweg je collegekaart tegen je telefoon aan te houden kan je hem gemakkelijk aan je account toevoegen."
                        ),
                        OnboardingItem(
                                onboardingImage = R.drawable.mobile_login,
                                title = "Hogeschool account koppelen",
                                description = "Dit systeem is gekoppeld aan dat van de Hogeschool Leiden, waardoor je slechts met een paar knoppen kan inloggen."
                        ),
                        OnboardingItem(
                                onboardingImage = R.drawable.secure_server,
                                title = "Veiligheid is onze prioriteit",
                                description = "Er worden zo min mogelijk gegevens van jou opgeslagen. Wat we wel moeten opslaan, wordt goed versleuteld."
                        ),
                )
        )
        val onboardingViewPager = findViewById<ViewPager2>(R.id.onboardingViewPager)
        onboardingViewPager.adapter = onboardingItemsAdapter
        onboardingViewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        (onboardingViewPager.getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        findViewById<ImageView>(R.id.imageNext).setOnClickListener {
            if (onboardingViewPager.currentItem + 1 < onboardingItemsAdapter.itemCount) {
                onboardingViewPager.currentItem += 1
            } else {
                navigateToLoginActivity(false)
            }
        }
        findViewById<TextView>(R.id.textSkip).setOnClickListener {
            navigateToLoginActivity(false)
        }
        findViewById<MaterialButton>(R.id.loginWithMicrosoft).setOnClickListener {
            navigateToLoginActivity(true)
        }
    }

    private fun navigateToLoginActivity(startSignInProcess: Boolean) {
        val i = Intent(applicationContext, LoginActivity::class.java)
        i.putExtra("startSignInProcess", startSignInProcess)
        startActivity(i)
        overridePendingTransition(R.anim.page_slide_out, R.anim.page_slide_in)
        finish()
    }

    private fun setupIndicators() {
        indicatorsContainer = findViewById(R.id.indicatorsContainer)
        val indicators = arrayOfNulls<ImageView>(onboardingItemsAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.let {
                it.setImageDrawable(
                        ContextCompat.getDrawable(
                                applicationContext,
                                R.drawable.indicator_inactive_background
                        )
                )
                it.layoutParams = layoutParams
                indicatorsContainer.addView(it)
            }
        }
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount = indicatorsContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorsContainer.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                                applicationContext,
                                R.drawable.indicator_active_background
                        )
                )
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                                applicationContext,
                                R.drawable.indicator_inactive_background
                        )
                )
            }
        }
    }

}