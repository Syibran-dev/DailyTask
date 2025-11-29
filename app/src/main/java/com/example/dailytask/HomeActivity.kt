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

// Tambahkan anotasi untuk menghilangkan warning terkait fungsi yang sudah usang
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

        // 1. Tangkap Data User
        val username = intent.getStringExtra("EXTRA_USERNAME") ?: "User"
        userEmail = intent.getStringExtra("EXTRA_EMAIL") ?: ""

        // 2. Inisialisasi View
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        tvPending = findViewById(R.id.tvPendingCount)
        tvDone = findViewById(R.id.tvDoneCount)
        val btnLogout = findViewById<ImageView>(R.id.btnLogout)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        rvTasks = findViewById(R.id.rvTasks)
        bottomNavUser = findViewById(R.id.bottomNavUser)

        // FIX WARNING (Line 46): Menggunakan resource string dengan placeholder
        tvWelcome.text = getString(R.string.home_welcome_greeting, username)

        // 3. Setup RecyclerView & Load Data
        setupRecyclerView()
        loadTasks()

        // 4. Setup Listeners
        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(this, getString(R.string.toast_logout_success), Toast.LENGTH_SHORT).show()
        }
        fabAdd.setOnClickListener { showAddTaskDialog() }

        // 5. Setup Bottom Navigation Listener
        bottomNavUser.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_tasks -> {
                    true
                }
                R.id.nav_calendar -> {
                    Toast.makeText(this, "Fitur Calendar sedang disiapkan!", Toast.LENGTH_SHORT).show()
                    false
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Membuka Profil Pengguna!", Toast.LENGTH_SHORT).show()
                    false
                }
                else -> false
            }
        }

        bottomNavUser.selectedItemId = R.id.nav_tasks
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(ArrayList(),
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
        // FIX ERROR 'name': Menggunakan getString dengan placeholder dan task.name
        // Use task.taskName instead of task.name to match TaskModel definition
        val message = getString(R.string.dialog_delete_message, task.taskName)

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
