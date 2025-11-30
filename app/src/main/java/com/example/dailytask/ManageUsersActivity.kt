package com.example.dailytask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView

class ManageUsersActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var bottomNavAdmin: BottomNavigationView
    private var adminEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DatabaseHelper(this)

        adminEmail = intent.getStringExtra("EXTRA_EMAIL") ?: ""

        if (adminEmail.isEmpty() || db.getUserRole(adminEmail) != "admin") {
            Toast.makeText(this, "Akses Ilegal!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setContentView(R.layout.activity_list)

        val tvTitle = findViewById<TextView>(R.id.tvPageTitle)
        val listView = findViewById<ListView>(R.id.listViewData)
        bottomNavAdmin = findViewById(R.id.bottomNavAdmin)

        tvTitle.text = "Kelola Pengguna"

        // Setup Bottom Navigation agar konsisten
        bottomNavAdmin.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_admin_dashboard -> {
                    // Kembali ke Dashboard, clear top agar tidak menumpuk
                    val intent = Intent(this, AdminActivity::class.java)
                    intent.putExtra("EXTRA_EMAIL", adminEmail)
                    // Kita tidak perlu EXTRA_USERNAME karena AdminActivity akan load ulang atau simpan state
                    // Tapi lebih baik jika AdminActivity mengambil username dari DB jika null
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish() // Tutup activity ini
                    true
                }
                R.id.nav_admin_users -> true // Sudah di sini
                R.id.nav_admin_logs -> {
                    val intent = Intent(this, SystemLogsActivity::class.java)
                    intent.putExtra("EXTRA_EMAIL", adminEmail)
                    startActivity(intent)
                    finish() // Pindah activity, tutup yang ini
                    true
                }
                else -> false
            }
        }
        bottomNavAdmin.selectedItemId = R.id.nav_admin_users

        // Load Data Users
        val rawUserList = db.getAllUsers()
        
        val adapter = object : ArrayAdapter<String>(this, 0, rawUserList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_user_admin, parent, false)
                
                val item = getItem(position) ?: return view
                val parts = item.split("\n")
                val line1 = parts.getOrElse(0) { "Unknown (User)" }
                val email = parts.getOrElse(1) { "-" }
                
                val nameParts = line1.split(" (")
                val username = nameParts.getOrElse(0) { "Unknown" }
                val role = nameParts.getOrElse(1) { "user)" }.replace(")", "")

                val tvName = view.findViewById<TextView>(R.id.tvUserName)
                val tvEmail = view.findViewById<TextView>(R.id.tvUserEmail)
                val tvRole = view.findViewById<TextView>(R.id.tvUserRole)
                val imgIcon = view.findViewById<ImageView>(R.id.imgUserIcon)

                tvName.text = username
                tvEmail.text = email
                tvRole.text = role.uppercase()

                if (role.lowercase() == "admin") {
                    tvRole.setBackgroundColor(context.getColor(android.R.color.holo_red_light))
                    tvRole.setTextColor(context.getColor(android.R.color.white))
                    imgIcon.setColorFilter(context.getColor(android.R.color.holo_red_dark))
                } else {
                    tvRole.setBackgroundColor(context.getColor(android.R.color.darker_gray))
                    tvRole.setTextColor(context.getColor(android.R.color.white))
                    imgIcon.setColorFilter(context.getColor(android.R.color.holo_purple))
                }

                return view
            }
        }

        listView.adapter = adapter
        listView.divider = null

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedString = rawUserList[position]
            val parts = selectedString.split("\n")
            if (parts.size > 1) {
                val targetEmail = parts[1].trim()
                val intent = Intent(this, AdminUserTasksActivity::class.java)
                intent.putExtra("EXTRA_ADMIN_EMAIL", adminEmail)
                intent.putExtra("EXTRA_TARGET_EMAIL", targetEmail)
                startActivity(intent)
            }
        }
    }
}