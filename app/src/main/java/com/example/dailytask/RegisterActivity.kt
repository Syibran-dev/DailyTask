package com.example.dailytask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    // Deklarasi variabel database
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inisialisasi Database
        db = DatabaseHelper(this)

        // 1. Inisialisasi Komponen View (Sesuai ID di XML baru)
        val etUsername = findViewById<TextInputEditText>(R.id.etRegUsername)
        val etEmail = findViewById<TextInputEditText>(R.id.etRegEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etRegPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        // 2. Logic Tombol "Login" (Jika user sudah punya akun)
        tvLoginLink.setOnClickListener {
            // Menutup activity ini akan otomatis kembali ke activity sebelumnya (Login)
            finish()
        }

        // 3. Logic Tombol Register
        btnRegister.setOnClickListener {
            // Ambil data dari inputan & hilangkan spasi depan/belakang (trim)
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // --- VALIDASI INPUT ---
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Mohon isi semua data!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --- PROSES SIMPAN KE DATABASE ---

            // CATATAN PENTING UNTUK MEMBUAT ADMIN:
            // Jika Anda ingin membuat akun ini sebagai ADMIN, ubah baris di bawah ini menjadi:
            // val isSuccess = db.register(email, username, password, "admin")

            // Default (User Biasa):
            val isSuccess = db.register(email, username, password) // Role otomatis "user"

            if (isSuccess) {
                Toast.makeText(this, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_SHORT).show()

                // Tutup halaman register agar user kembali ke halaman Login
                finish()
            } else {
                Toast.makeText(this, "Registrasi Gagal! Email mungkin sudah terdaftar.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}