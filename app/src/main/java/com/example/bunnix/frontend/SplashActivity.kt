//package com.example.bunnix.frontend
//
//import com.example.bunnix.R
//import android.content.Intent
//import android.os.Bundle
//import android.os.Handler
//
//
//import androidx.appcompat.app.AppCompatActivity
//import kotlin.jvm.java
//
//class SplashActivity : AppCompatActivity() {
//    protected override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_splash.xml)
//
//
//        Handler().postDelayed(Runnable {
//            val intent: Intent = Intent(this@SplashActivity, Onboarding1Activity::class.java)
//            startActivity(intent)
//            finish()
//        }, 2000)
//    }
//}