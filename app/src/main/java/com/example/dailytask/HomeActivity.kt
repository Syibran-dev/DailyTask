package com.example.dailytask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 1. Tangkap Username dari Intent (dikirim dari LoginActivity)
        val username = intent.getStringExtra("EXTRA_USERNAME") ?: "User"

        // 2. Inisialisasi View
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val btnLogout = findViewById<ImageView>(R.id.btnLogout)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        val rvTasks = findViewById<RecyclerView>(R.id.rvTasks)

        // 3. Set Nama User ke Teks Sapaan
        tvWelcome.text = "Halo, $username!"

        // 4. Logic Tombol Logout
        btnLogout.setOnClickListener {
            // Kembali ke halaman Login
            val intent = Intent(this, LoginActivity::class.java)
            // Hapus stack activity agar user tidak bisa back ke Home setelah logout
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
        }

        // 5. Logic Tombol Tambah Tugas (FAB)
        fabAdd.setOnClickListener {
            Toast.makeText(this, "Buka form tambah tugas...", Toast.LENGTH_SHORT).show()
            // Nanti di sini arahkan ke Activity tambah tugas
            // startActivity(Intent(this, AddTaskActivity::class.java))
        }

        // TODO: Setup RecyclerView untuk menampilkan data dari database
    }
}