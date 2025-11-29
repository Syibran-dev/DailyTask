package com.example.dailytask

import android.content.Context
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

class ManageUsersActivity : AppCompatActivity() {

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

        tvTitle.text = "Kelola Pengguna"

        // Data format: "Username (Role)\nEmail"
        val rawUserList = db.getAllUsers()
        
        // Custom Adapter
        val adapter = object : ArrayAdapter<String>(this, 0, rawUserList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_user_admin, parent, false)
                
                val item = getItem(position) ?: return view
                // Parse: "Username (Role)\nEmail"
                val parts = item.split("\n")
                val line1 = parts.getOrElse(0) { "Unknown (User)" }
                val email = parts.getOrElse(1) { "-" }
                
                // Parse line1: "Username (Role)"
                // Simple hack: Split by " ("
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
                    imgIcon.setColorFilter(context.getColor(R.color.purple_500))
                }

                return view
            }
        }

        listView.adapter = adapter
        listView.divider = null // Remove default separator

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