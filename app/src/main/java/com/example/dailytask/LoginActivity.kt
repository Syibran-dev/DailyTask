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

        // Inisialisasi Database
        db = DatabaseHelper(this)

        // 1. Inisialisasi View
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)

        // 2. Logic Tombol Register
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 3. Logic Tombol Lupa Password
        tvForgotPassword.setOnClickListener {
            // Menggunakan resource string
            Toast.makeText(this, getString(R.string.toast_password_reset_unavailable), Toast.LENGTH_SHORT).show()
        }

        // 4. Logic Tombol Login (Inti Proses)
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validasi Input Kosong
            if (email.isEmpty() || password.isEmpty()) {
                // Menggunakan resource string
                Toast.makeText(this, getString(R.string.toast_email_password_required), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cek Login ke Database
            if (db.login(email, password)) {

                // --- LOGIN SUKSES ---

                // Ambil data Role dan Username dari database
                val role = db.getUserRole(email)
                val username = db.getUsername(email)

                // Tentukan tujuan berdasarkan Role
                val intent = if (role == "admin") {
                    // Menggunakan resource string
                    Toast.makeText(this, getString(R.string.toast_login_admin_success), Toast.LENGTH_SHORT).show()
                    Intent(this, AdminActivity::class.java)
                } else {
                    // Menggunakan resource string
                    Toast.makeText(this, getString(R.string.toast_login_user_success), Toast.LENGTH_SHORT).show()
                    Intent(this, HomeActivity::class.java)
                }

                // --- PENTING: KIRIM DATA KE ACTIVITY SELANJUTNYA ---
                intent.putExtra("EXTRA_USERNAME", username)
                intent.putExtra("EXTRA_EMAIL", email)

                startActivity(intent)
                finish()

            } else {
                // --- LOGIN GAGAL ---
                // Menggunakan resource string
                Toast.makeText(this, getString(R.string.toast_login_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }
}