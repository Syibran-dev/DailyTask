package com.example.dailytask

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*

// FINAL VERSION 5
class DatabaseHelper(context: Context)
    : SQLiteOpenHelper(context, "DailyTask.db", null, 5) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, username TEXT, password TEXT, role TEXT)")
        db?.execSQL("CREATE TABLE logs(id INTEGER PRIMARY KEY AUTOINCREMENT, message TEXT, created_at TEXT)")
        db?.execSQL("CREATE TABLE tasks(id INTEGER PRIMARY KEY AUTOINCREMENT, email_user TEXT, task_name TEXT, is_done INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS users")
        db?.execSQL("DROP TABLE IF EXISTS logs")
        db?.execSQL("DROP TABLE IF EXISTS tasks")
        onCreate(db)
    }

    // --- FUNGSI UTAMA AUTH & LOGS ---
    fun register(email: String, username: String, password: String, role: String = "user"): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("email", email)
        values.put("username", username)
        values.put("password", password)
        values.put("role", role)

        val res = db.insert("users", null, values)
        if (res != -1L) {
            addLog("User Baru: $username bergabung sebagai $role")
            return true
        }
        return false
    }

    fun login(email: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE email=? AND password=?", arrayOf(email, password))
        val success = cursor.count > 0
        if (success) {
            cursor.moveToFirst()
            val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
            addLog("Login: $username masuk ke aplikasi")
        }
        cursor.close()
        return success
    }

    fun getUsername(email: String): String {
        val db = readableDatabase
        var name = ""
        val cursor = db.rawQuery("SELECT username FROM users WHERE email=?", arrayOf(email))
        if (cursor.moveToFirst()) name = cursor.getString(0)
        cursor.close()
        return name
    }

    fun getUserRole(email: String): String { /* ... (Logika sama) ... */ return "user" }
    fun addLog(message: String) { /* ... (Logika sama) ... */ }
    fun getAllLogs(): ArrayList<String> { /* ... (Logika sama) ... */ return ArrayList() }
    fun getAllUsers(): ArrayList<String> { /* ... (Logika sama) ... */ return ArrayList() }

    // --- FUNGSI TASK (CRUD & STATS) ---
    fun addTask(emailUser: String, taskName: String): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("email_user", emailUser)
        values.put("task_name", taskName)
        values.put("is_done", 0)
        return db.insert("tasks", null, values) != -1L
    }

    fun getUserTasks(emailUser: String): ArrayList<TaskModel> {
        val list = ArrayList<TaskModel>()
        val db = readableDatabase
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

    fun updateTaskStatus(id: Int, isDone: Boolean) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("is_done", if (isDone) 1 else 0)
        db.update("tasks", values, "id=?", arrayOf(id.toString()))
    }

    fun deleteTask(id: Int) {
        val db = writableDatabase
        db.delete("tasks", "id=?", arrayOf(id.toString()))
    }

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