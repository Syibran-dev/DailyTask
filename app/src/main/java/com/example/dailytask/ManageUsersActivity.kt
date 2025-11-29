package com.example.dailytask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

class ManageUsersActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DatabaseHelper(this)

        // 1. KEAMANAN WAJIB: Cek Role Admin
        val adminEmail = intent.getStringExtra("EXTRA_EMAIL")

        if (adminEmail.isNullOrEmpty() || db.getUserRole(adminEmail) != "admin") {
            Toast.makeText(this, "Akses Ilegal! Silakan Login sebagai Admin.", Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_list)

        val tvTitle = findViewById<TextView>(R.id.tvPageTitle)
        val listView = findViewById<ListView>(R.id.listViewData)

        tvTitle.text = "Kelola Tugas Pengguna" // Ubah judul agar relevan

        // Ambil semua user
        val rawUserList = db.getAllUsers()
        // Filter atau format ulang jika perlu. Format saat ini: "Username (Role)\nEmail"
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, rawUserList)
        listView.adapter = adapter

        // CLICK LISTENER: Buka halaman AdminUserTasksActivity untuk user yang dipilih
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedString = rawUserList[position]
            // Parse Email dari string: "Username (Role)\nEmail"
            val parts = selectedString.split("\n")
            if (parts.size > 1) {
                val targetEmail = parts[1].trim()
                
                // Buka Activity khusus Admin untuk mengelola tugas user tersebut
                val intent = Intent(this, AdminUserTasksActivity::class.java)
                intent.putExtra("EXTRA_ADMIN_EMAIL", adminEmail)
                intent.putExtra("EXTRA_TARGET_EMAIL", targetEmail)
                startActivity(intent)
            }
        }
    }
}