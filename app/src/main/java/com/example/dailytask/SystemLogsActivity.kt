package com.example.dailytask

import android.app.AlertDialog
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
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SystemLogsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var bottomNavAdmin: BottomNavigationView
    private lateinit var listView: ListView
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
        listView = findViewById(R.id.listViewData)
        bottomNavAdmin = findViewById(R.id.bottomNavAdmin)
        val fabAction = findViewById<FloatingActionButton>(R.id.fabAction)

        tvTitle.text = "Log Aktivitas Sistem"
        
        // Show FAB for clearing logs
        fabAction.visibility = View.VISIBLE
        fabAction.setOnClickListener {
             showClearLogsConfirmation()
        }

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
                R.id.nav_admin_logs -> true 
                else -> false
            }
        }
        bottomNavAdmin.selectedItemId = R.id.nav_admin_logs

        loadLogs()
    }
    
    private fun loadLogs() {
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
    
    private fun showClearLogsConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Semua Log")
            .setMessage("Apakah Anda yakin ingin menghapus semua data log aktivitas?")
            .setPositiveButton("Hapus") { _, _ ->
                db.clearAllLogs()
                loadLogs()
                Toast.makeText(this, "Semua log berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
