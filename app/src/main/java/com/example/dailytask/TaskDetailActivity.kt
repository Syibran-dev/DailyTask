package com.example.dailytask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class TaskDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        val taskName = intent.getStringExtra("EXTRA_TASK_NAME") ?: "-"
        val taskDate = intent.getStringExtra("EXTRA_TASK_DATE") ?: "-"
        val taskDesc = intent.getStringExtra("EXTRA_TASK_DESC") ?: "-"
        val isDone = intent.getBooleanExtra("EXTRA_TASK_STATUS", false)

        val tvDetailName = findViewById<TextView>(R.id.tvDetailName)
        val tvDetailDate = findViewById<TextView>(R.id.tvDetailDate)
        val tvDetailDesc = findViewById<TextView>(R.id.tvDetailDesc)
        val tvDetailStatus = findViewById<TextView>(R.id.tvDetailStatus)

        tvDetailName.text = taskName
        tvDetailDate.text = if (taskDate.isNotEmpty()) "Tenggat: $taskDate" else "Tenggat: -"
        tvDetailDesc.text = if (taskDesc.isNotEmpty()) taskDesc else "Tidak ada deskripsi."
        
        val statusText = if (isDone) "Selesai" else "Belum Selesai"
        tvDetailStatus.text = "Status: $statusText"
        tvDetailStatus.setTextColor(if (isDone) getColor(android.R.color.holo_green_dark) else getColor(android.R.color.holo_red_dark))
    }
}
