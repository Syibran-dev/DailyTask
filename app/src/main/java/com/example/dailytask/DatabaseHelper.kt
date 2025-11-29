package com.example.dailytask

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*

// UBAH VERSION KE 4
class DatabaseHelper(context: Context)
    : SQLiteOpenHelper(context, "DailyTask.db", null, 4) {

    override fun onCreate(db: SQLiteDatabase?) {
        // Tabel Users
        db?.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, username TEXT, password TEXT, role TEXT)")

        // Tabel Logs (BARU: Untuk mencatat aktivitas)
        db?.execSQL("CREATE TABLE logs(id INTEGER PRIMARY KEY AUTOINCREMENT, message TEXT, created_at TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS users")
        db?.execSQL("DROP TABLE IF EXISTS logs")
        onCreate(db)
    }

    // --- FUNGSI USER ---
    fun register(email: String, username: String, password: String, role: String = "user"): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("email", email)
        values.put("username", username)
        values.put("password", password)
        values.put("role", role)

        val res = db.insert("users", null, values)
        if (res != -1L) {
            // Otomatis catat log saat register berhasil
            addLog("User baru mendaftar: $username ($email)")
            return true
        }
        return false
    }

    fun login(email: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE email=? AND password=?", arrayOf(email, password))
        val success = cursor.count > 0

        if (success) {
            // Ambil username untuk log
            cursor.moveToFirst()
            val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
            addLog("User login: $username")
        }
        cursor.close()
        return success
    }

    // --- FUNGSI ADMIN: MANAGE USERS ---
    fun getAllUsers(): ArrayList<String> {
        val list = ArrayList<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT username, email, role FROM users", null)

        while (cursor.moveToNext()) {
            val user = cursor.getString(0)
            val email = cursor.getString(1)
            val role = cursor.getString(2)
            list.add("$user ($role)\n$email")
        }
        cursor.close()
        return list
    }

    // --- FUNGSI SISTEM: LOGS / NOTIFIKASI ---

    // Fungsi untuk mencatat aktivitas (Dipanggil di backend)
    fun addLog(message: String) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("message", message)

        // Ambil waktu sekarang
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = dateFormat.format(Date())
        values.put("created_at", date)

        db.insert("logs", null, values)
    }

    // Fungsi untuk Admin melihat semua log
    fun getAllLogs(): ArrayList<String> {
        val list = ArrayList<String>()
        val db = readableDatabase
        // Urutkan dari yang terbaru (DESC)
        val cursor = db.rawQuery("SELECT message, created_at FROM logs ORDER BY id DESC", null)

        while (cursor.moveToNext()) {
            val msg = cursor.getString(0)
            val time = cursor.getString(1)
            list.add("[$time]\n$msg")
        }
        cursor.close()
        return list
    }

    // Fungsi pendukung lainnya
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
        var username = ""
        val cursor = db.rawQuery("SELECT username FROM users WHERE email=?", arrayOf(email))
        if (cursor.moveToFirst()) username = cursor.getString(0)
        cursor.close()
        return username
    }
}