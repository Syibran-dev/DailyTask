package com.example.dailytask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // 1. Tangkap Username
        val username = intent.getStringExtra("EXTRA_USERNAME") ?: "Admin"
        val tvTitle = findViewById<TextView>(R.id.tvAdminTitle)
        tvTitle.text = "Hello, $username"

        // 2. Inisialisasi Tombol
        val btnLogout = findViewById<ImageView>(R.id.btnAdminLogout)
        val cardManageUsers = findViewById<CardView>(R.id.cardManageUsers)
        val cardSettings = findViewById<CardView>(R.id.cardSettings) // Kita ubah ini jadi Log Activity

        // 3. Logic Logout
        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(this, "Admin Logout Berhasil", Toast.LENGTH_SHORT).show()
        }

        // 4. Logic Manage Users (Melihat Daftar User)
        cardManageUsers.setOnClickListener {
            startActivity(Intent(this, ManageUsersActivity::class.java))
        }

        // 5. Logic Settings / Log Activity (Melihat Notifikasi Perubahan)
        cardSettings.setOnClickListener {
            // Kita arahkan tombol Settings ini ke halaman LOGS agar admin bisa liat notifikasi
            startActivity(Intent(this, SystemLogsActivity::class.java))
        }
    }
}