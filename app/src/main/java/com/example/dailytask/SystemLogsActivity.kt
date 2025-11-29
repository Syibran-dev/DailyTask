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

class SystemLogsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DatabaseHelper(this)

        val adminEmail = intent.getStringExtra("EXTRA_EMAIL")

        if (adminEmail.isNullOrEmpty() || db.getUserRole(adminEmail) != "admin") {
            Toast.makeText(this, "Akses Ilegal!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setContentView(R.layout.activity_list)

        val tvTitle = findViewById<TextView>(R.id.tvPageTitle)
        val listView = findViewById<ListView>(R.id.listViewData)

        tvTitle.text = "Log Aktivitas Sistem"

        // Data format: "[timestamp] message"
        val rawLogList = db.getAllLogs()

        val adapter = object : ArrayAdapter<String>(this, 0, rawLogList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_log_admin, parent, false)
                
                val item = getItem(position) ?: return view
                // Parse: "[timestamp] message"
                // Simple hack: Split by "] "
                val parts = item.split("] ")
                val timestamp = parts.getOrElse(0) { "[" }.replace("[", "")
                val message = parts.getOrElse(1) { item } // Fallback if parsing fails

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