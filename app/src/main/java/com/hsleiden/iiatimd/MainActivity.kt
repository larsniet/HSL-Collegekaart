package com.hsleiden.iiatimd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {

    private lateinit var onboardingItemsAdapter: OnboardingItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setOnboardingItems()
    }

    private fun setOnboardingItems() {
        onboardingItemsAdapter = OnboardingItemsAdapter(
            listOf(
                OnboardingItem(
                    onboardingImage = R.drawable.mobile_app,
                    title = "Inloggen met je Hogeschool account",
                    description = "Dit systeem is gekoppeld aan dat van de Hogeschool Leiden, waardoor jij veilig en makkelijk kan inloggen."
                ),
                OnboardingItem(
                    onboardingImage = R.drawable.cloud_sync,
                    title = "Synchroniseer je studentenpas",
                    description = "Door simpelweg je schoolpas tegen je telefoon aan te houden kan je hem gemakkelijk aan je account toevoegen."
                ),
                OnboardingItem(
                    onboardingImage = R.drawable.secure_data,
                    title = "Beveiliging is onze prioriteit",
                    description = "Er worden zo min mogelijk gegevens van jou opgeslagen. De gegevens die we wel opslaan, versleutelen we en verwijderen we bij inactiviteit."
                ),
            )
        )
        val onboardingViewPager = findViewById<ViewPager2>(R.id.onboardingViewPager)
        onboardingViewPager.adapter = onboardingItemsAdapter
    }
}