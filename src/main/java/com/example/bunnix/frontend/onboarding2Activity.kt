package com.example.bunnix.frontend


import kotlin.jvm.java
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.example.bunnix.R

class Onboarding2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding1)

        // Find the button by its ID
        val nextButton: Button = findViewById(R.id.btnNext)

        // Set click listener
        nextButton.setOnClickListener {
            // Make sure Onboarding3Activity exists
            val intent = Intent(this@Onboarding2Activity, Onboarding3Activity::class.java)
            startActivity(intent)
        }
    }
}