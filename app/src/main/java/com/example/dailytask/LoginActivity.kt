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

        // 1. Inisialisasi View (Sesuai ID di XML Modern)
        // Perhatikan tipe datanya adalah TextInputEditText
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)

        // 2. Logic Tombol Register (Belum punya akun)
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 3. Logic Tombol Lupa Password
        tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Fitur reset password sedang dalam pengembangan", Toast.LENGTH_SHORT).show()
        }

        // 4. Logic Tombol Login (Inti Proses)
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validasi Input Kosong
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cek Login ke Database
            if (db.login(email, password)) {

                // --- LOGIN SUKSES ---

                // Ambil data Role dan Username dari database
                val role = db.getUserRole(email)
                val username = db.getUsername(email)

                // Tentukan tujuan berdasarkan Role
                val intent: Intent
                if (role == "admin") {
                    Toast.makeText(this, "Login Admin Berhasil!", Toast.LENGTH_SHORT).show()
                    intent = Intent(this, AdminActivity::class.java)
                } else {
                    Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                    intent = Intent(this, HomeActivity::class.java)
                }

                // Kirim Username ke Activity selanjutnya agar bisa ditampilkan
                intent.putExtra("EXTRA_USERNAME", username)

                startActivity(intent)
                finish() // Tutup LoginActivity agar user tidak bisa back ke sini

            } else {
                // --- LOGIN GAGAL ---
                Toast.makeText(this, "Email atau Password salah!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}