package com.example.notes_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Check auth state after delay
        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = FirebaseAuth.getInstance().currentUser
            val intent = when {
                currentUser != null && currentUser.isEmailVerified -> {
                    // User is logged in and verified
                    Intent(this, MainActivity::class.java)
                }
                currentUser != null && !currentUser.isEmailVerified -> {
                    // User is logged in but not verified
                    FirebaseAuth.getInstance().signOut()
                    Intent(this, LoginActivity::class.java)
                }
                else -> {
                    // No user logged in
                    Intent(this, LoginActivity::class.java)
                }
            }
            startActivity(intent)
            finish()
        }, 1000)
    }
}