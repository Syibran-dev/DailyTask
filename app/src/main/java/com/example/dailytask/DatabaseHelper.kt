package com.example.dailytask

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context)
    : SQLiteOpenHelper(context, "DailyTask.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, password TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    fun register(email: String, password: String): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("email", email)
        values.put("password", password)

        return db.insert("users", null, values) != -1L
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
}
