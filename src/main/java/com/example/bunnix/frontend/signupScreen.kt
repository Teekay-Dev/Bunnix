package com.example.bunnix.frontend

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bunnix.R

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val txtLogin = findViewById<TextView>(R.id.txtLogin)

        txtLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(
                R.transition.slide_out_left,
                R.transition.slide_in_right
            )
        }
    }
}
