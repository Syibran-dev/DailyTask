package com.example.dailytask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    private val ADMIN_SECRET_KEY = "DAILY_ADMIN_2025"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        db = DatabaseHelper(this)

        val etUsername = findViewById<TextInputEditText>(R.id.etRegUsername)
        val etEmail = findViewById<TextInputEditText>(R.id.etRegEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etRegPassword)
        val etAdminKey = findViewById<TextInputEditText>(R.id.etAdminKey)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        tvLoginLink.setOnClickListener {
            finish()
        }

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val adminKey = etAdminKey.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_fill_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var roleToRegister = "user"

            if (adminKey.isNotEmpty()) {
                if (adminKey == ADMIN_SECRET_KEY) {
                    roleToRegister = "admin"
                    Toast.makeText(this, getString(R.string.toast_admin_key_confirmed), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, getString(R.string.toast_admin_key_invalid), Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }

            val isSuccess = db.register(email, username, password, roleToRegister)

            if (isSuccess) {
                Toast.makeText(this, getString(R.string.toast_registration_success), Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, getString(R.string.toast_registration_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
