package com.example.bunnix.frontend

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.example.bunnix.R
import kotlin.jvm.java

class Onboarding3Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding3) // Make sure this layout exists

        // Find the button by its ID
        val nextButton: Button = findViewById(R.id.btnNext)

        // Set click listener
        nextButton.setOnClickListener {
            val intent = Intent(this@Onboarding3Activity, AuthActivity::class.java)
            startActivity(intent)
            finish() // Optional: closes the current activity
        }
    }
}