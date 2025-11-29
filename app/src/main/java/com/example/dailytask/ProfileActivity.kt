package com.example.dailytask

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        db = DatabaseHelper(this)

        val username = intent.getStringExtra("EXTRA_USERNAME") ?: "User"
        val email = intent.getStringExtra("EXTRA_EMAIL") ?: "email@example.com"

        val tvUsername = findViewById<TextView>(R.id.tvProfileName)
        val tvEmail = findViewById<TextView>(R.id.tvProfileEmail)
        val tvPending = findViewById<TextView>(R.id.tvStatPending)
        val tvDone = findViewById<TextView>(R.id.tvStatDone)
        val btnLogout = findViewById<Button>(R.id.btnProfileLogout)

        tvUsername.text = username
        tvEmail.text = email

        // Load stats
        val stats = db.getTaskCounts(email)
        tvPending.text = stats.first.toString()
        tvDone.text = stats.second.toString()

        btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin keluar dari akun ini?")
            .setPositiveButton("Ya, Logout") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}