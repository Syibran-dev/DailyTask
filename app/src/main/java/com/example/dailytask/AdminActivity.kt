package com.example.dailytask

import android.app.AlertDialog
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
    private lateinit var tvGlobalPendingCount: TextView
    private lateinit var tvGlobalDoneCount: TextView
    private var adminEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = DatabaseHelper(this)

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

        setContentView(R.layout.activity_admin)

        val username = intent.getStringExtra("EXTRA_USERNAME") ?: "Admin"
        val tvTitle = findViewById<TextView>(R.id.tvAdminTitle)
        val btnLogout = findViewById<ImageView>(R.id.btnAdminLogout)

        tvTotalUsersCount = findViewById(R.id.tvTotalUsersCount)
        tvTotalLogsCount = findViewById(R.id.tvTotalLogsCount)
        tvGlobalPendingCount = findViewById(R.id.tvGlobalPendingCount)
        tvGlobalDoneCount = findViewById(R.id.tvGlobalDoneCount)

        val cardManageUsers = findViewById<CardView>(R.id.cardManageUsers)
        val cardSettings = findViewById<CardView>(R.id.cardSettings)
        
        val cardStatsPending = findViewById<CardView>(R.id.cardStatsPending)
        val cardStatsDone = findViewById<CardView>(R.id.cardStatsDone)
        
        bottomNavAdmin = findViewById(R.id.bottomNavAdmin)

        tvTitle.text = getString(R.string.home_welcome_greeting, username)

        updateDashboardStats()

        btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }

        cardManageUsers.setOnClickListener {
            startActivity(Intent(this, ManageUsersActivity::class.java).apply {
                putExtra("EXTRA_EMAIL", adminEmail)
            })
        }

        cardSettings.setOnClickListener {
            startActivity(Intent(this, SystemLogsActivity::class.java).apply {
                putExtra("EXTRA_EMAIL", adminEmail)
            })
        }
        
        cardStatsPending.setOnClickListener {
            startActivity(Intent(this, AdminGlobalTasksActivity::class.java).apply {
                putExtra("EXTRA_ADMIN_EMAIL", adminEmail)
                putExtra("EXTRA_FILTER", "PENDING")
            })
        }
        
        cardStatsDone.setOnClickListener {
            startActivity(Intent(this, AdminGlobalTasksActivity::class.java).apply {
                putExtra("EXTRA_ADMIN_EMAIL", adminEmail)
                putExtra("EXTRA_FILTER", "DONE")
            })
        }

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
        val taskCounts = db.getGlobalTaskCounts()

        tvTotalUsersCount.text = userCount.toString()
        tvTotalLogsCount.text = logCount.toString()
        tvGlobalPendingCount.text = taskCounts.first.toString()
        tvGlobalDoneCount.text = taskCounts.second.toString()
    }
    
    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin keluar dari Mode Admin?")
            .setPositiveButton("Logout") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                Toast.makeText(this, "Logout Berhasil", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
