package com.example.notes_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var createAccountBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var loginBtnTextView: TextView
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize views
        initializeViews()

        // Set up click listeners
        setupClickListeners()
    }

    private fun initializeViews() {
        emailEditText = findViewById(R.id.email_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text)
        createAccountBtn = findViewById(R.id.create_account_button)
        progressBar = findViewById(R.id.progress_bar)
        loginBtnTextView = findViewById(R.id.login_text_view_button)
    }

    private fun setupClickListeners() {
        createAccountBtn.setOnClickListener {
            createAccount()
        }

        loginBtnTextView.setOnClickListener {
            finish()
        }
    }

    private fun createAccount() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        // Input validation
        if (!validateData(email, password, confirmPassword)) {
            return
        }

        // Show progress bar
        changeInProgress(true)

        // Create account in Firebase
        createAccountInFirebase(email, password)
    }

    private fun createAccountInFirebase(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Account created successfully
                    Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()

                    // Send email verification
                    sendEmailVerification()
                } else {
                    // If account creation fails
                    val exception = task.exception
                    Toast.makeText(this, "Failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
                    changeInProgress(false)
                }
            }
            .addOnFailureListener { e ->
                // Handle specific Firebase errors
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                changeInProgress(false)
            }
    }

    private fun sendEmailVerification() {
        val firebaseUser = firebaseAuth.currentUser
        firebaseUser?.sendEmailVerification()
            ?.addOnCompleteListener { verificationTask ->
                if (verificationTask.isSuccessful) {
                    // Email sent successfully
                    Toast.makeText(
                        this,
                        "Verification email sent. Please verify your email before logging in.",
                        Toast.LENGTH_LONG
                    ).show()

                    // Sign out the user
                    firebaseAuth.signOut()

                    // Navigate to Login Activity
                    navigateToLogin()
                } else {
                    // Failed to send verification email
                    Toast.makeText(
                        this,
                        "Failed to send verification email: ${verificationTask.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    changeInProgress(false)
                }
            }
            ?.addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error sending verification email: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                changeInProgress(false)
            }
    }

    private fun navigateToLogin() {
        // You can replace LoginActivity::class.java with your actual login activity
        val intent = Intent(this, LoginActivity::class.java)
        // Clear the back stack so user can't go back to create account screen
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun validateData(email: String, password: String, confirmPassword: String): Boolean {
        // Validate email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Invalid email"
            return false
        }

        // Validate password
        if (password.length < 6) {
            passwordEditText.error = "Password must be at least 6 characters"
            return false
        }

        // Validate password confirmation
        if (password != confirmPassword) {
            confirmPasswordEditText.error = "Passwords don't match"
            return false
        }

        return true
    }

    private fun changeInProgress(inProgress: Boolean) {
        if (inProgress) {
            progressBar.visibility = View.VISIBLE
            createAccountBtn.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            createAccountBtn.visibility = View.VISIBLE
        }
    }
}