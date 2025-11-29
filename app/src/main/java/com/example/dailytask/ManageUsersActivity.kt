package com.example.dailytask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView

class ManageUsersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val db = DatabaseHelper(this)
        val tvTitle = findViewById<TextView>(R.id.tvPageTitle)
        val listView = findViewById<ListView>(R.id.listViewData)

        tvTitle.text = "Daftar Pengguna"

        val users = db.getAllUsers()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, users)
        listView.adapter = adapter
    }
}