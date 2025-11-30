package com.example.dailytask

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var rvTasks: RecyclerView
    private lateinit var tvPending: TextView
    private lateinit var tvDone: TextView
    private lateinit var bottomNavUser: BottomNavigationView
    private lateinit var layoutStats: LinearLayout 

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
        layoutStats = findViewById(R.id.layoutStats) 
        val btnLogout = findViewById<ImageView>(R.id.btnLogout)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        rvTasks = findViewById(R.id.rvTasks)
        bottomNavUser = findViewById(R.id.bottomNavUser)

        tvWelcome.text = getString(R.string.home_welcome_greeting, username)

        layoutStats.visibility = View.GONE

        setupRecyclerView()
        loadTasks()
        checkAdminUpdates()

        btnLogout.setOnClickListener {
            showLogoutConfirmation()
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
    
    private fun checkAdminUpdates() {
        if (userEmail.isNotEmpty()) {
            val updates = db.getUnseenAdminUpdates(userEmail)
            if (updates.isNotEmpty()) {
                val sb = StringBuilder("Tugas berikut telah diselesaikan oleh Admin:\n")
                for (taskName in updates) {
                    sb.append("- $taskName\n")
                }
                
                AlertDialog.Builder(this)
                    .setTitle("Update Status")
                    .setMessage(sb.toString())
                    .setPositiveButton("OK") { _, _ ->
                        db.markUpdatesAsSeen(userEmail)
                    }
                    .show()
            }
        }
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
            onStatusChange = { _, _ ->
                Toast.makeText(this, "Hanya Admin yang bisa mengubah status!", Toast.LENGTH_SHORT).show()
                loadTasks() 
            },
            onDelete = { task ->
                showDeleteConfirmation(task)
            },
            isEditable = false 
        )
        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = taskAdapter
    }

    private fun loadTasks() {
        if (userEmail.isNotEmpty()) {
            val tasks = db.getUserTasks(userEmail)
            taskAdapter.updateData(tasks)
        }
    }

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val etTaskName = dialogView.findViewById<TextInputEditText>(R.id.etTaskName)
        val etDay = dialogView.findViewById<TextInputEditText>(R.id.etDay)
        val etMonth = dialogView.findViewById<TextInputEditText>(R.id.etMonth)
        val etYear = dialogView.findViewById<TextInputEditText>(R.id.etYear)
        val etTaskDesc = dialogView.findViewById<TextInputEditText>(R.id.etTaskDesc)

        AlertDialog.Builder(this)
            .setTitle("Tambah Tugas Baru")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val taskName = etTaskName.text.toString().trim()
                val day = etDay.text.toString().trim()
                val month = etMonth.text.toString().trim()
                val year = etYear.text.toString().trim()
                val taskDesc = etTaskDesc.text.toString().trim()

                val taskDate = if (day.isNotEmpty() && month.isNotEmpty() && year.isNotEmpty()) {
                    "$day/$month/$year"
                } else {
                    ""
                }

                if (taskName.isNotEmpty()) {
                    if (taskDate.isEmpty() && (day.isNotEmpty() || month.isNotEmpty() || year.isNotEmpty())) {
                         Toast.makeText(this, "Format tanggal tidak lengkap!", Toast.LENGTH_SHORT).show()
                         return@setPositiveButton
                    }
                    
                    db.addTask(userEmail, taskName, taskDate, taskDesc)
                    loadTasks()
                    Toast.makeText(this, getString(R.string.toast_task_added), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Nama tugas wajib diisi!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showDeleteConfirmation(task: TaskModel) {
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

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Logout") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                Toast.makeText(this, getString(R.string.toast_logout_success), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
