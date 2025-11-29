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
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var rvTasks: RecyclerView
    private lateinit var tvPending: TextView
    private lateinit var tvDone: TextView
    private var userEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = DatabaseHelper(this)

        // 1. Tangkap Data User
        val username = intent.getStringExtra("EXTRA_USERNAME") ?: "User"
        // PASTIKAN LOGIN ACTIVITY MENGIRIM "EXTRA_EMAIL"
        userEmail = intent.getStringExtra("EXTRA_EMAIL") ?: ""

        // 2. Inisialisasi View
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        tvPending = findViewById(R.id.tvPendingCount) // Tambahkan ID ini di XML (lihat langkah 5)
        tvDone = findViewById(R.id.tvDoneCount)       // Tambahkan ID ini di XML (lihat langkah 5)
        val btnLogout = findViewById<ImageView>(R.id.btnLogout)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        rvTasks = findViewById(R.id.rvTasks)

        tvWelcome.text = "Halo, $username!"

        // 3. Setup RecyclerView
        setupRecyclerView()

        // 4. Load Data Awal
        loadTasks()

        // 5. Tombol Logout
        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
        }

        // 6. Tombol Tambah Tugas (Munculkan Dialog)
        fabAdd.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun setupRecyclerView() {
        // Adapter menangani logika Ceklis (Update) dan Sampah (Hapus)
        taskAdapter = TaskAdapter(ArrayList(),
            onStatusChange = { task, isDone ->
                db.updateTaskStatus(task.id, isDone)
                loadDashboardStats() // Update angka di dashboard
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
            tvPending.text = stats.first.toString() // Pending
            tvDone.text = stats.second.toString()   // Done
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
                    loadTasks() // Refresh list
                    Toast.makeText(this, "Tugas ditambahkan!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showDeleteConfirmation(task: TaskModel) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Tugas")
            .setMessage("Yakin ingin menghapus '${task.name}'?")
            .setPositiveButton("Hapus") { _, _ ->
                db.deleteTask(task.id)
                loadTasks() // Refresh list
                Toast.makeText(this, "Tugas dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}