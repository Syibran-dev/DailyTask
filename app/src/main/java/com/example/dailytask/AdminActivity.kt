package com.example.dailytask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminActivity : AppCompatActivity() {

    private lateinit var bottomNavAdmin: BottomNavigationView
    private lateinit var db: DatabaseHelper
    private lateinit var tvTotalUsersCount: TextView
    private lateinit var tvTotalLogsCount: TextView
    private var adminEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = DatabaseHelper(this)

        // 1. TANGKAP EMAIL & CEK SECURITY WAJIB
        val passedEmail = intent.getStringExtra("EXTRA_EMAIL")
        adminEmail = passedEmail ?: ""

        if (adminEmail.isNullOrEmpty() || db.getUserRole(adminEmail) != "admin") {
            Toast.makeText(this, "AKSES DITOLAK! Anda bukan administrator.", Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        // LANJUTKAN JIKA LOLOS KEAMANAN
        setContentView(R.layout.activity_admin)

        val username = intent.getStringExtra("EXTRA_USERNAME") ?: "Admin"
        val tvTitle = findViewById<TextView>(R.id.tvAdminTitle)
        val btnLogout = findViewById<ImageView>(R.id.btnAdminLogout)

        tvTotalUsersCount = findViewById(R.id.tvTotalUsersCount)
        tvTotalLogsCount = findViewById(R.id.tvTotalLogsCount)
        val cardManageUsers = findViewById<CardView>(R.id.cardManageUsers)
        val cardSettings = findViewById<CardView>(R.id.cardSettings)
        bottomNavAdmin = findViewById(R.id.bottomNavAdmin)

        tvTitle.text = getString(R.string.home_welcome_greeting, username)

        updateDashboardStats()

        // 2. Logic Logout
        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(this, "Logout Berhasil", Toast.LENGTH_SHORT).show()
        }

        // 3. Logic Tombol Kartu (Menu Tengah)
        cardManageUsers.setOnClickListener {
            startActivity(Intent(this, ManageUsersActivity::class.java).apply {
                putExtra("EXTRA_EMAIL", adminEmail)
            })
        }

        // FIX: Kirimkan EXTRA_EMAIL saat klik card Settings
        cardSettings.setOnClickListener {
            startActivity(Intent(this, SystemLogsActivity::class.java).apply {
                putExtra("EXTRA_EMAIL", adminEmail)
            })
        }

        // 4. Logic Bottom Navigation (Navigasi Utama)
        bottomNavAdmin.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_admin_dashboard -> true
                R.id.nav_admin_users -> {
                    startActivity(Intent(this, ManageUsersActivity::class.java).apply {
                        putExtra("EXTRA_EMAIL", adminEmail)
                    })
                    true
                }
                R.id.nav_admin_logs -> {
                    // FIX: Kirimkan EXTRA_EMAIL saat klik Bottom Nav Logs
                    startActivity(Intent(this, SystemLogsActivity::class.java).apply {
                        putExtra("EXTRA_EMAIL", adminEmail)
                    })
                    true
                }
                else -> false
            }
        }

        bottomNavAdmin.selectedItemId = R.id.nav_admin_dashboard
    }

    override fun onResume() {
        super.onResume()
        if (adminEmail.isNotEmpty()) {
            updateDashboardStats()
        }
    }

    private fun updateDashboardStats() {
        val userCount = db.getUserCount()
        val logCount = db.getAllLogs().size
        tvTotalUsersCount.text = userCount.toString()
        tvTotalLogsCount.text = logCount.toString()
    }
}