package com.example.dailytask

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText

class AdminUserTasksActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var rvTasks: RecyclerView
    private lateinit var tvPending: TextView
    private lateinit var tvDone: TextView
    private lateinit var layoutStats: LinearLayout

    private var targetEmail: String = ""
    private var adminEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Kita gunakan layout yang sama dengan HomeActivity karena strukturnya mirip
        setContentView(R.layout.activity_home)

        db = DatabaseHelper(this)

        adminEmail = intent.getStringExtra("EXTRA_ADMIN_EMAIL") ?: ""
        targetEmail = intent.getStringExtra("EXTRA_TARGET_EMAIL") ?: ""

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        tvPending = findViewById(R.id.tvPendingCount)
        tvDone = findViewById(R.id.tvDoneCount)
        layoutStats = findViewById(R.id.layoutStats)
        
        // Sembunyikan elemen User (Logout, BottomNav, FAB)
        findViewById<android.view.View>(R.id.btnLogout).visibility = android.view.View.GONE
        findViewById<android.view.View>(R.id.bottomNavUser).visibility = android.view.View.GONE
        findViewById<android.view.View>(R.id.fabAdd).visibility = android.view.View.GONE

        rvTasks = findViewById(R.id.rvTasks)

        val username = db.getUsername(targetEmail)
        tvWelcome.text = "Managing User: $username"

        // ADMIN VIEW: Stats terlihat
        layoutStats.visibility = android.view.View.VISIBLE

        setupRecyclerView()
        loadTasks()
    }

    private fun setupRecyclerView() {
        // ADMIN MODE: isEditable = true (Admin bisa ubah status)
        taskAdapter = TaskAdapter(
            ArrayList(),
            onTaskClick = { task -> 
                val intent = Intent(this, TaskDetailActivity::class.java).apply {
                    putExtra("EXTRA_TASK_ID", task.id)
                    putExtra("EXTRA_TASK_NAME", task.taskName)
                    putExtra("EXTRA_TASK_DATE", task.taskDate)
                    putExtra("EXTRA_TASK_DESC", task.taskDesc)
                    putExtra("EXTRA_TASK_STATUS", task.isDone)
                }
                startActivity(intent)
            },
            onStatusChange = { task, isDone ->
                // Admin mengubah status -> Update DB
                db.updateTaskStatus(task.id, isDone)
                loadTasks() // Refresh stats
                Toast.makeText(this, "Status diperbarui oleh Admin", Toast.LENGTH_SHORT).show()
            },
            onDelete = { task ->
                showDeleteConfirmation(task)
            },
            isEditable = true // FITUR UTAMA: Checkbox Enabled untuk Admin
        )
        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = taskAdapter
    }

    private fun loadTasks() {
        if (targetEmail.isNotEmpty()) {
            val tasks = db.getUserTasks(targetEmail)
            taskAdapter.updateData(tasks)
            loadDashboardStats()
        }
    }

    private fun loadDashboardStats() {
        if (targetEmail.isNotEmpty()) {
            val stats = db.getTaskCounts(targetEmail)
            tvPending.text = stats.first.toString()
            tvDone.text = stats.second.toString()
        }
    }

    private fun showDeleteConfirmation(task: TaskModel) {
        val message = "Hapus tugas '${task.taskName}' milik user ini?"

        AlertDialog.Builder(this)
            .setTitle("Hapus Tugas (Admin)")
            .setMessage(message)
            .setPositiveButton("Hapus") { _, _ ->
                db.deleteTask(task.id)
                loadTasks()
                Toast.makeText(this, "Tugas dihapus oleh Admin", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
