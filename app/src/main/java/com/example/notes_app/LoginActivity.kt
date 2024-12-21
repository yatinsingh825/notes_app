package com.example.notes_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var createAccountBtnTextView: TextView
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize views
        initializeViews()

        // Set up window insets
        setupWindowInsets()

        // Set up click listeners
        setupClickListeners()
    }

    private fun initializeViews() {
        emailEditText = findViewById(R.id.email_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        loginBtn = findViewById(R.id.create_login_button)
        progressBar = findViewById(R.id.progress_bar)
        createAccountBtnTextView = findViewById(R.id.create_account_text_view_button)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupClickListeners() {
        loginBtn.setOnClickListener {
            loginUser()
        }

        createAccountBtnTextView.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
            finish()
        }
    }

    private fun loginUser() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE
        loginBtn.visibility = View.GONE

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progressBar.visibility = View.GONE
                loginBtn.visibility = View.VISIBLE

                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        if (user.isEmailVerified) {
                            // User is verified, proceed to main activity
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            // User needs to verify email
                            Toast.makeText(
                                this,
                                "Please verify your email first. Check your inbox.",
                                Toast.LENGTH_LONG
                            ).show()
                            firebaseAuth.signOut()
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
    override fun onStart() {
        super.onStart()
        // Check if user is already logged in
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}