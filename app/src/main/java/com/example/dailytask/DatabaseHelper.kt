package com.example.dailytask

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*

// UBAH VERSION KE 5
class DatabaseHelper(context: Context)
    : SQLiteOpenHelper(context, "DailyTask.db", null, 5) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, username TEXT, password TEXT, role TEXT)")
        db?.execSQL("CREATE TABLE logs(id INTEGER PRIMARY KEY AUTOINCREMENT, message TEXT, created_at TEXT)")

        // TABEL BARU: TASKS
        // is_done: 0 = Belum, 1 = Selesai
        db?.execSQL("CREATE TABLE tasks(id INTEGER PRIMARY KEY AUTOINCREMENT, email_user TEXT, task_name TEXT, is_done INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS users")
        db?.execSQL("DROP TABLE IF EXISTS logs")
        db?.execSQL("DROP TABLE IF EXISTS tasks") // Drop tabel tasks
        onCreate(db)
    }

    // --- FUNGSI AUTH & LOGS (Sama seperti sebelumnya, tidak perlu diubah) ---
    fun register(email: String, username: String, password: String, role: String = "user"): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("email", email)
        values.put("username", username)
        values.put("password", password)
        values.put("role", role)
        return db.insert("users", null, values) != -1L
    }

    fun login(email: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE email=? AND password=?", arrayOf(email, password))
        val success = cursor.count > 0
        cursor.close()
        return success
    }

    fun getUserRole(email: String): String {
        val db = readableDatabase
        var role = "user"
        val cursor = db.rawQuery("SELECT role FROM users WHERE email=?", arrayOf(email))
        if (cursor.moveToFirst()) role = cursor.getString(0)
        cursor.close()
        return role
    }

    fun getUsername(email: String): String {
        val db = readableDatabase
        var name = ""
        val cursor = db.rawQuery("SELECT username FROM users WHERE email=?", arrayOf(email))
        if (cursor.moveToFirst()) name = cursor.getString(0)
        cursor.close()
        return name
    }

    // --- FUNGSI TASK (BARU) ---

    // 1. Tambah Tugas
    fun addTask(emailUser: String, taskName: String): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("email_user", emailUser)
        values.put("task_name", taskName)
        values.put("is_done", 0) // Default belum selesai
        return db.insert("tasks", null, values) != -1L
    }

    // 2. Ambil Semua Tugas User Tertentu
    // Kita buat class Model sederhana di dalam return type
    fun getUserTasks(emailUser: String): ArrayList<TaskModel> {
        val list = ArrayList<TaskModel>()
        val db = readableDatabase
        // Urutkan: Yang belum selesai diatas
        val cursor = db.rawQuery("SELECT id, task_name, is_done FROM tasks WHERE email_user=? ORDER BY is_done ASC, id DESC", arrayOf(emailUser))

        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            val isDone = cursor.getInt(2) == 1
            list.add(TaskModel(id, name, isDone))
        }
        cursor.close()
        return list
    }

    // 3. Update Status (Selesai/Belum)
    fun updateTaskStatus(id: Int, isDone: Boolean) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("is_done", if (isDone) 1 else 0)
        db.update("tasks", values, "id=?", arrayOf(id.toString()))
    }

    // 4. Hapus Tugas
    fun deleteTask(id: Int) {
        val db = writableDatabase
        db.delete("tasks", "id=?", arrayOf(id.toString()))
    }

    // 5. Hitung Statistik (Untuk Dashboard)
    fun getTaskCounts(emailUser: String): Pair<Int, Int> {
        val db = readableDatabase
        val cursorPending = db.rawQuery("SELECT COUNT(*) FROM tasks WHERE email_user=? AND is_done=0", arrayOf(emailUser))
        val cursorDone = db.rawQuery("SELECT COUNT(*) FROM tasks WHERE email_user=? AND is_done=1", arrayOf(emailUser))

        var pending = 0
        var done = 0

        if (cursorPending.moveToFirst()) pending = cursorPending.getInt(0)
        if (cursorDone.moveToFirst()) done = cursorDone.getInt(0)

        cursorPending.close()
        cursorDone.close()

        return Pair(pending, done)
    }
}

// Model Data Sederhana (Taruh di file ini atau file terpisah boleh)
data class TaskModel(val id: Int, val name: String, val isDone: Boolean)