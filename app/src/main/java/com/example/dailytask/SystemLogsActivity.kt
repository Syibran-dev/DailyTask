package com.example.dailytask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView

class SystemLogsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list) // Pakai layout yang sama

        val tvTitle = findViewById<TextView>(R.id.tvPageTitle)
        val listView = findViewById<ListView>(R.id.listViewData)
        val db = DatabaseHelper(this)

        tvTitle.text = "Log Aktivitas User"

        // Ambil data logs dari database
        val logList = db.getAllLogs()

        // Tampilkan ke ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, logList)
        listView.adapter = adapter
    }
}