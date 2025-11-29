package com.example.dailytask

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// UBAH VERSION KE 3 (Penting agar tabel di-reset ulang dengan struktur baru)
class DatabaseHelper(context: Context)
    : SQLiteOpenHelper(context, "DailyTask.db", null, 3) {

    override fun onCreate(db: SQLiteDatabase?) {
        // Tambahkan kolom 'username'
        db?.execSQL(
            "CREATE TABLE users(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "email TEXT UNIQUE, " +
                    "username TEXT, " +  // Kolom baru
                    "password TEXT, " +
                    "role TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    // Update fungsi register: Terima parameter 'username'
    fun register(email: String, username: String, password: String, role: String = "user"): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("email", email)
        values.put("username", username) // Simpan username
        values.put("password", password)
        values.put("role", role)

        val result = db.insert("users", null, values)
        return result != -1L
    }

    fun login(email: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE email=? AND password=?",
            arrayOf(email, password)
        )
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

    // FUNGSI BARU: Ambil Username berdasarkan email
    fun getUsername(email: String): String {
        val db = readableDatabase
        var username = ""
        val cursor = db.rawQuery("SELECT username FROM users WHERE email=?", arrayOf(email))
        if (cursor.moveToFirst()) {
            username = cursor.getString(0)
        }
        cursor.close()
        return username
    }
}