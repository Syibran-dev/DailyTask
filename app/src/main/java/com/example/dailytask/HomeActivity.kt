package com.example.dailytask

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var rvTasks: RecyclerView
    private lateinit var tvPending: TextView
    private lateinit var tvDone: TextView
    private lateinit var bottomNavUser: BottomNavigationView

    private var userEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = DatabaseHelper(this)

        val username = intent.getStringExtra("EXTRA_USERNAME") ?: "User"
        userEmail = intent.getStringExtra("EXTRA_EMAIL") ?: ""

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        tvPending = findViewById(R.id.tvPendingCount)
        tvDone = findViewById(R.id.tvDoneCount)
        val btnLogout = findViewById<ImageView>(R.id.btnLogout)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        rvTasks = findViewById(R.id.rvTasks)
        bottomNavUser = findViewById(R.id.bottomNavUser)

        tvWelcome.text = getString(R.string.home_welcome_greeting, username)

        setupRecyclerView()
        loadTasks()

        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(this, getString(R.string.toast_logout_success), Toast.LENGTH_SHORT).show()
        }
        fabAdd.setOnClickListener { showAddTaskDialog() }

        bottomNavUser.setOnItemSelectedListener { item ->
            val intentToStart: Intent? = when (item.itemId) {
                R.id.nav_tasks -> null
                R.id.nav_calendar -> Intent(this, CalendarActivity::class.java)
                R.id.nav_profile -> Intent(this, ProfileActivity::class.java)
                else -> null
            }

            if (intentToStart != null) {
                intentToStart.putExtra("EXTRA_USERNAME", intent.getStringExtra("EXTRA_USERNAME"))
                intentToStart.putExtra("EXTRA_EMAIL", userEmail)
                startActivity(intentToStart)
            }
            return@setOnItemSelectedListener item.itemId == R.id.nav_tasks
        }

        bottomNavUser.selectedItemId = R.id.nav_tasks
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            ArrayList(),
            onTaskClick = { task -> // LISTENER DETAIL (Diperlukan oleh TaskAdapter yang baru)
                val intent = Intent(this, TaskDetailActivity::class.java).apply {
                    putExtra("EXTRA_TASK_ID", task.id)
                    putExtra("EXTRA_TASK_NAME", task.name) // Kirim nama juga untuk kemudahan
                    putExtra("EXTRA_EMAIL", userEmail)
                }
                startActivity(intent)
            },
            onStatusChange = { task, isDone ->
                db.updateTaskStatus(task.id, isDone)
                loadTasks()
            },
            onDelete = { task ->
                showDeleteConfirmation(task)
            }
        )
        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = taskAdapter
    }

    private fun loadTasks() {
        if (userEmail.isNotEmpty()) {
            val tasks = db.getUserTasks(userEmail)
            taskAdapter.updateData(tasks)
            loadDashboardStats()
        }
    }

    private fun loadDashboardStats() {
        if (userEmail.isNotEmpty()) {
            val stats = db.getTaskCounts(userEmail)
            tvPending.text = stats.first.toString()
            tvDone.text = stats.second.toString()
        }
    }

    private fun showAddTaskDialog() {
        val input = EditText(this)
        input.hint = "Masukkan nama tugas..."

        AlertDialog.Builder(this)
            .setTitle("Tambah Tugas Baru")
            .setView(input)
            .setPositiveButton("Simpan") { _, _ ->
                val taskName = input.text.toString().trim()
                if (taskName.isNotEmpty()) {
                    db.addTask(userEmail, taskName)
                    loadTasks()
                    Toast.makeText(this, getString(R.string.toast_task_added), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showDeleteConfirmation(task: TaskModel) {
        // FIX: Menggunakan properti model yang benar: task.name
        val message = getString(R.string.dialog_delete_message, task.name)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_delete_title))
            .setMessage(message)
            .setPositiveButton("Hapus") { _, _ ->
                db.deleteTask(task.id)
                loadTasks()
                Toast.makeText(this, getString(R.string.toast_task_deleted), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}