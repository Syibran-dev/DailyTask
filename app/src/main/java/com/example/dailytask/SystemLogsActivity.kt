package com.example.dailytask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

class SystemLogsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = DatabaseHelper(this)

        // 1. KEAMANAN WAJIB: Tangkap Email Admin dan Cek Role
        val adminEmail = intent.getStringExtra("EXTRA_EMAIL")

        if (adminEmail.isNullOrEmpty() || db.getUserRole(adminEmail) != "admin") {
            Toast.makeText(this, "Akses Ilegal! Silakan Login sebagai Admin.", Toast.LENGTH_LONG).show()

            // Redirect ke Login
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        // Jika lolos cek keamanan:
        setContentView(R.layout.activity_list)

        // 2. Inisialisasi View & Data
        val tvTitle = findViewById<TextView>(R.id.tvPageTitle)
        val listView = findViewById<ListView>(R.id.listViewData)

        tvTitle.text = "Log Aktivitas Sistem"

        // Ambil data logs dari database
        val logList = db.getAllLogs()

        // Tampilkan ke ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, logList)
        listView.adapter = adapter
    }
}