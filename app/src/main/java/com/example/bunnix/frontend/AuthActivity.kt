//package com.example.bunnix.frontend
//
//import kotlin.jvm.java
//import android.content.Intent
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Toast
//import com.example.bunnix.R
//
//class AuthActivity : AppCompatActivity() {
//
//    private lateinit var emailEditText: EditText
//    private lateinit var passwordEditText: EditText
//    private lateinit var loginButton: Button
//    private lateinit var signupButton: Button
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_auth)
//
//        // Find views
//        emailEditText = findViewById(R.id.editEmail)
//        passwordEditText = findViewById(R.id.editPassword)
//        loginButton = findViewById(R.id.btnLogin)
//        signupButton = findViewById(R.id.btnSignup)
//
//        // Login button click
//        loginButton.setOnClickListener {
//            val email = emailEditText.text.toString().trim()
//            val password = passwordEditText.text.toString().trim()
//
//            if(email.isEmpty() || password.isEmpty()) {
//                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
//            } else {
//                // TODO: Add login logic here
//                Toast.makeText(this, "Login clicked", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Signup button click
//        signupButton.setOnClickListener {
//            val intent = Intent(this@AuthActivity, SignupActivity::class.java)
//            startActivity(intent)
//        }
//    }
//}