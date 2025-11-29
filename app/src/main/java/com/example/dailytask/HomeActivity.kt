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
    private lateinit var layoutStats: LinearLayout // Container statistik

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
        layoutStats = findViewById(R.id.layoutStats) // Pastikan ID ini ada di XML
        val btnLogout = findViewById<ImageView>(R.id.btnLogout)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        rvTasks = findViewById(R.id.rvTasks)
        bottomNavUser = findViewById(R.id.bottomNavUser)

        tvWelcome.text = getString(R.string.home_welcome_greeting, username)

        // SEMBUNYIKAN STATISTIK UNTUK USER (Hanya Admin yang bisa lihat)
        // Sesuai permintaan: "pending sama sukses hanya admin saja yang bisa liat"
        layoutStats.visibility = View.GONE

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
        // USER TIDAK BISA EDIT STATUS (isEditable = false)
        // "ketika user tambah tugas nanti admin yang menentukan apaka tugas udah selesai atau blom"
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
                // User tidak bisa ubah status, jadi callback ini mungkin tidak dipanggil jika checkbox disabled.
                // Tapi jika dipanggil, kita abaikan atau show message.
                Toast.makeText(this, "Hanya Admin yang bisa mengubah status!", Toast.LENGTH_SHORT).show()
                loadTasks() // Revert UI change
            },
            onDelete = { task ->
                showDeleteConfirmation(task)
            },
            isEditable = false // FITUR BARU: User View -> Disabled Checkbox
        )
        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = taskAdapter
    }

    private fun loadTasks() {
        if (userEmail.isNotEmpty()) {
            val tasks = db.getUserTasks(userEmail)
            taskAdapter.updateData(tasks)
            // Stats tidak perlu di-load karena hidden
        }
    }

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val etTaskName = dialogView.findViewById<TextInputEditText>(R.id.etTaskName)
        val etTaskDate = dialogView.findViewById<TextInputEditText>(R.id.etTaskDate)
        val etTaskDesc = dialogView.findViewById<TextInputEditText>(R.id.etTaskDesc)

        AlertDialog.Builder(this)
            .setTitle("Tambah Tugas Baru")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val taskName = etTaskName.text.toString().trim()
                val taskDate = etTaskDate.text.toString().trim()
                val taskDesc = etTaskDesc.text.toString().trim()

                if (taskName.isNotEmpty()) {
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
}
