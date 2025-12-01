package com.example.dailytask

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        db = DatabaseHelper(this)

        val etEmail = findViewById<TextInputEditText>(R.id.etFPEmail)
        val etNewPassword = findViewById<TextInputEditText>(R.id.etFPNewPassword)
        val etConfirmPassword = findViewById<TextInputEditText>(R.id.etFPConfirmPassword)
        val btnReset = findViewById<Button>(R.id.btnResetPassword)

        btnReset.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val newPass = etNewPassword.text.toString().trim()
            val confirmPass = etConfirmPassword.text.toString().trim()

            if (email.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Mohon isi semua kolom!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPass != confirmPass) {
                Toast.makeText(this, "Password baru tidak cocok!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPass.length < 6) {
                Toast.makeText(this, "Password minimal 6 karakter!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usernameExists = db.getUsername(email).isNotEmpty()

            if (!usernameExists) {
                Toast.makeText(this, "Email tidak terdaftar.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val success = db.updatePassword(email, newPass)

            if (success) {
                Toast.makeText(this, "Password berhasil direset. Silakan login.", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Gagal mereset password.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
