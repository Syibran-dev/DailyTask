package com.example.dailytask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView

class SystemLogsActivity : AppCompatActivity() {

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

        tvTitle.text = "Log Aktivitas Sistem"

        // Setup Bottom Navigation
        bottomNavAdmin.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_admin_dashboard -> {
                    val intent = Intent(this, AdminActivity::class.java)
                    intent.putExtra("EXTRA_EMAIL", adminEmail)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_admin_users -> {
                    val intent = Intent(this, ManageUsersActivity::class.java)
                    intent.putExtra("EXTRA_EMAIL", adminEmail)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_admin_logs -> true // Sudah di sini
                else -> false
            }
        }
        bottomNavAdmin.selectedItemId = R.id.nav_admin_logs

        // Load Logs
        val rawLogList = db.getAllLogs()

        val adapter = object : ArrayAdapter<String>(this, 0, rawLogList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_log_admin, parent, false)
                
                val item = getItem(position) ?: return view
                val parts = item.split("] ")
                val timestamp = parts.getOrElse(0) { "[" }.replace("[", "")
                val message = parts.getOrElse(1) { item }

                val tvTime = view.findViewById<TextView>(R.id.tvLogTime)
                val tvMsg = view.findViewById<TextView>(R.id.tvLogMessage)

                tvTime.text = timestamp
                tvMsg.text = message

                return view
            }
        }

        listView.adapter = adapter
        listView.divider = null
    }
}