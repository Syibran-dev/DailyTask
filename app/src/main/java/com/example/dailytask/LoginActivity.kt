package com.example.dailytask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = DatabaseHelper(this)

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_email_password_required), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (db.login(email, password)) {

                val role = db.getUserRole(email)
                val username = db.getUsername(email)

                val intent = if (role == "admin") {
                    Toast.makeText(this, getString(R.string.toast_login_admin_success), Toast.LENGTH_SHORT).show()
                    Intent(this, AdminActivity::class.java)
                } else {
                    Toast.makeText(this, getString(R.string.toast_login_user_success), Toast.LENGTH_SHORT).show()
                    Intent(this, HomeActivity::class.java)
                }

                intent.putExtra("EXTRA_USERNAME", username)
                intent.putExtra("EXTRA_EMAIL", email)

                startActivity(intent)
                finish()

            } else {
                Toast.makeText(this, getString(R.string.toast_login_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }
}