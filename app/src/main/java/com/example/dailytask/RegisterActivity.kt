package com.example.dailytask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    // DEKLARASI SECRET KEY (Kunci Rahasia Admin)
    private val ADMIN_SECRET_KEY = "DAILY_ADMIN_2025"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        db = DatabaseHelper(this)

        // 1. Inisialisasi Komponen View
        val etUsername = findViewById<TextInputEditText>(R.id.etRegUsername)
        val etEmail = findViewById<TextInputEditText>(R.id.etRegEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etRegPassword)
        val etAdminKey = findViewById<TextInputEditText>(R.id.etAdminKey)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        // 2. Logic Tombol "Login"
        tvLoginLink.setOnClickListener {
            finish()
        }

        // 3. Logic Tombol Register
        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val adminKey = etAdminKey.text.toString().trim()

            // --- VALIDASI INPUT WAJIB ---
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_fill_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --- TENTUKAN ROLE BERDASARKAN KEY ---
            var roleToRegister = "user"

            if (adminKey.isNotEmpty()) {
                // Key diisi, lakukan konfirmasi
                if (adminKey == ADMIN_SECRET_KEY) {
                    roleToRegister = "admin" // Konfirmasi BERHASIL
                    // MENGGANTI STRING LITERAL DENGAN RESOURCE STRING
                    Toast.makeText(this, getString(R.string.toast_admin_key_confirmed), Toast.LENGTH_SHORT).show()
                } else {
                    // Key salah, batalkan proses pendaftaran
                    // MENGGANTI STRING LITERAL DENGAN RESOURCE STRING
                    Toast.makeText(this, getString(R.string.toast_admin_key_invalid), Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }
            // Jika adminKey kosong, role otomatis tetap "user"

            // --- PROSES SIMPAN KE DATABASE ---
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