package com.example.dailytask

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminGlobalTasksActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var rvTasks: RecyclerView
    private lateinit var tvWelcome: TextView
    private lateinit var layoutStats: LinearLayout

    private var filterType: String = "ALL" // ALL, PENDING, DONE
    private var adminEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = DatabaseHelper(this)

        adminEmail = intent.getStringExtra("EXTRA_ADMIN_EMAIL") ?: ""
        filterType = intent.getStringExtra("EXTRA_FILTER") ?: "ALL"

        tvWelcome = findViewById(R.id.tvWelcome)
        rvTasks = findViewById(R.id.rvTasks)
        layoutStats = findViewById(R.id.layoutStats)

        // Sembunyikan elemen User & Stats yang tidak perlu
        findViewById<View>(R.id.btnLogout).visibility = View.GONE
        findViewById<View>(R.id.bottomNavUser).visibility = View.GONE
        findViewById<View>(R.id.fabAdd).visibility = View.GONE
        layoutStats.visibility = View.GONE // Hide user stats layout

        // Custom Title
        val filterName = when(filterType) {
            "PENDING" -> "Pending Tasks"
            "DONE" -> "Completed Tasks"
            else -> "All Tasks"
        }
        tvWelcome.text = filterName
        
        val subtitle = findViewById<TextView>(R.id.tvSubtitleHome)
        subtitle.text = "Menampilkan semua tugas dari seluruh user"

        setupRecyclerView()
        loadTasks()
    }

    private fun setupRecyclerView() {
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
                showStatusChangeConfirmation(task, isDone)
            },
            onDelete = { task ->
                showDeleteConfirmation(task)
            },
            isEditable = true
        )
        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = taskAdapter
    }

    private fun loadTasks() {
        val tasks = db.getAllTasksWithUsernames(filterType)
        taskAdapter.updateData(tasks)
    }

    private fun showStatusChangeConfirmation(task: TaskModel, isDone: Boolean) {
        val statusText = if (isDone) "SELESAI (Done)" else "PENDING"
        val message = "Tindakan untuk tugas '${task.taskName}' milik ${task.ownerName}?"

        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Status")
            .setMessage(message)
            .setPositiveButton("Ubah ke $statusText") { _, _ ->
                db.updateTaskStatus(task.id, isDone)
                loadTasks()
                Toast.makeText(this, "Status diperbarui", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Hapus Tugas") { _, _ ->
                db.deleteTask(task.id)
                loadTasks()
                Toast.makeText(this, "Tugas dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
                loadTasks() // Revert
            }
            .setCancelable(false)
            .show()
    }
    
    private fun showDeleteConfirmation(task: TaskModel) {
        val message = "Hapus tugas '${task.taskName}' milik ${task.ownerName}?"

        AlertDialog.Builder(this)
            .setTitle("Hapus Tugas")
            .setMessage(message)
            .setPositiveButton("Hapus") { _, _ ->
                db.deleteTask(task.id)
                loadTasks()
                Toast.makeText(this, "Tugas dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
