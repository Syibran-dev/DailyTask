// File: TaskDetailActivity.kt
package com.example.dailytask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast

class TaskDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        val db = DatabaseHelper(this)
        val taskId = intent.getIntExtra("EXTRA_TASK_ID", -1)

        val tvDetailName = findViewById<TextView>(R.id.tvDetailName)
        val tvDetailStatus = findViewById<TextView>(R.id.tvDetailStatus)

        if (taskId != -1) {
            // Logika: Ambil detail tugas dari database berdasarkan ID
            // NOTE: Anda perlu membuat fungsi di DatabaseHelper.kt untuk mengambil satu tugas berdasarkan ID
            // Karena kita belum punya fungsi itu, kita tampilkan ID-nya saja dulu.

            tvDetailName.text = "Detail Tugas ID: $taskId"
            tvDetailStatus.text = "Status: (Belum Diambil dari DB)"
        } else {
            Toast.makeText(this, "Tugas tidak ditemukan.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}