package com.example.harmonic.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonic.MainActivity
import com.example.harmonic.R
import com.example.harmonic.messages.HomeActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

// data layer that handles the logic for login activity/view
class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.setOnClickListener {
           loginUser()
        }

        login_switch.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {
        val email = email_login.text.toString()
        val password = password_login.text.toString()
        val auth = Firebase.auth

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter an email and/or password", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("Login", "Email: $email, Password: $password")

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("login", "user successfully logged in")
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    return@addOnCompleteListener
                }
            }.addOnFailureListener {
                Log.d("login", "user failed to login: ${it.message}")
                Toast.makeText(this, "user failed to login: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
}