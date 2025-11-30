package com.example.dailytask

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// FINAL VERSION 7 - Added avatar_id to users
class DatabaseHelper(context: Context)
    : SQLiteOpenHelper(context, "DailyTask.db", null, 7) {

    override fun onCreate(db: SQLiteDatabase?) {
        // users table now has avatar_id (0-3 represents different icons)
        db?.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, username TEXT, password TEXT, role TEXT, avatar_id INTEGER DEFAULT 0)")
        db?.execSQL("CREATE TABLE logs(id INTEGER PRIMARY KEY AUTOINCREMENT, message TEXT, created_at TEXT)")
        db?.execSQL("CREATE TABLE tasks(id INTEGER PRIMARY KEY AUTOINCREMENT, email_user TEXT, task_name TEXT, is_done INTEGER, task_date TEXT, task_desc TEXT)")

        seedAdminAccount(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS users")
        db?.execSQL("DROP TABLE IF EXISTS logs")
        db?.execSQL("DROP TABLE IF EXISTS tasks")
        onCreate(db)
    }

    private fun seedAdminAccount(db: SQLiteDatabase?) {
        val values = ContentValues()
        values.put("email", "admin@daily.com")
        values.put("username", "System Admin")
        values.put("password", "123456")
        values.put("role", "admin")
        values.put("avatar_id", 0)
        db?.insert("users", null, values)
    }

    fun register(email: String, username: String, password: String, role: String = "user"): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("email", email)
        values.put("username", username)
        values.put("password", password)
        values.put("role", role)
        values.put("avatar_id", 0) // Default avatar

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
        var username = ""
        val cursor = db.rawQuery("SELECT username FROM users WHERE email=?", arrayOf(email))
        if (cursor.moveToFirst()) {
            username = cursor.getString(0)
        }
        cursor.close()
        return username
    }

    fun getUserRole(email: String): String {
        val db = readableDatabase
        var role = "user"
        val cursor = db.rawQuery("SELECT role FROM users WHERE email=?", arrayOf(email))
        if (cursor.moveToFirst()) {
            role = cursor.getString(0)
        }
        cursor.close()
        return role
    }

    fun getUserAvatar(email: String): Int {
        val db = readableDatabase
        var avatarId = 0
        val cursor = db.rawQuery("SELECT avatar_id FROM users WHERE email=?", arrayOf(email))
        if (cursor.moveToFirst()) {
            avatarId = cursor.getInt(0)
        }
        cursor.close()
        return avatarId
    }

    fun updateProfile(email: String, newUsername: String, newAvatarId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("username", newUsername)
        values.put("avatar_id", newAvatarId)
        val rows = db.update("users", values, "email=?", arrayOf(email))
        return rows > 0
    }

    fun addLog(message: String) {
        val db = writableDatabase
        val values = ContentValues()
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        values.put("message", message)
        values.put("created_at", timestamp)
        db.insert("logs", null, values)
    }

    fun getAllLogs(): ArrayList<String> {
        val list = ArrayList<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM logs ORDER BY id DESC", null)
        if (cursor.moveToFirst()) {
            do {
                val msg = cursor.getString(cursor.getColumnIndexOrThrow("message"))
                val time = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
                list.add("[$time] $msg")
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun getAllUsers(): ArrayList<String> {
        val list = ArrayList<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT username, email, role FROM users", null)
        if (cursor.moveToFirst()) {
            do {
                val user = cursor.getString(0)
                val email = cursor.getString(1)
                val role = cursor.getString(2)
                list.add("$user ($role)\n$email")
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun getUserCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM users", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    fun addTask(emailUser: String, taskName: String, taskDate: String, taskDesc: String): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("email_user", emailUser)
        values.put("task_name", taskName)
        values.put("is_done", 0)
        values.put("task_date", taskDate)
        values.put("task_desc", taskDesc)
        val res = db.insert("tasks", null, values)
        return res != -1L
    }

    fun getUserTasks(emailUser: String): ArrayList<TaskModel> {
        val list = ArrayList<TaskModel>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM tasks WHERE email_user=? ORDER BY is_done ASC, id DESC", arrayOf(emailUser))
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("task_name"))
                val isDoneInt = cursor.getInt(cursor.getColumnIndexOrThrow("is_done"))
                val date = try { cursor.getString(cursor.getColumnIndexOrThrow("task_date")) } catch (e: Exception) { "" }
                val desc = try { cursor.getString(cursor.getColumnIndexOrThrow("task_desc")) } catch (e: Exception) { "" }
                
                // ownerName default empty for normal user view
                list.add(TaskModel(id, name, isDoneInt == 1, date ?: "", desc ?: "", ""))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
    
    // New Method: Get ALL tasks from ALL users for Admin Global View
    fun getAllTasksWithUsernames(filter: String = "ALL"): ArrayList<TaskModel> {
        val list = ArrayList<TaskModel>()
        val db = readableDatabase
        
        // Simple join to get username from users table
        var query = "SELECT tasks.*, users.username FROM tasks LEFT JOIN users ON tasks.email_user = users.email"
        
        if (filter == "PENDING") {
            query += " WHERE tasks.is_done = 0"
        } else if (filter == "DONE") {
            query += " WHERE tasks.is_done = 1"
        }
        query += " ORDER BY tasks.id DESC"

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("task_name"))
                val isDoneInt = cursor.getInt(cursor.getColumnIndexOrThrow("is_done"))
                val date = try { cursor.getString(cursor.getColumnIndexOrThrow("task_date")) } catch (e: Exception) { "" }
                val desc = try { cursor.getString(cursor.getColumnIndexOrThrow("task_desc")) } catch (e: Exception) { "" }
                val owner = try { cursor.getString(cursor.getColumnIndexOrThrow("username")) } catch (e: Exception) { "Unknown" }
                
                list.add(TaskModel(id, name, isDoneInt == 1, date ?: "", desc ?: "", owner ?: "Unknown"))
            } while (cursor.moveToNext())
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
        var pending = 0
        var done = 0

        val curPending = db.rawQuery("SELECT COUNT(*) FROM tasks WHERE email_user=? AND is_done=0", arrayOf(emailUser))
        if (curPending.moveToFirst()) pending = curPending.getInt(0)
        curPending.close()

        val curDone = db.rawQuery("SELECT COUNT(*) FROM tasks WHERE email_user=? AND is_done=1", arrayOf(emailUser))
        if (curDone.moveToFirst()) done = curDone.getInt(0)
        curDone.close()

        return Pair(pending, done)
    }
    
    fun getGlobalTaskCounts(): Pair<Int, Int> {
        val db = readableDatabase
        var pending = 0
        var done = 0

        val curPending = db.rawQuery("SELECT COUNT(*) FROM tasks WHERE is_done=0", null)
        if (curPending.moveToFirst()) pending = curPending.getInt(0)
        curPending.close()

        val curDone = db.rawQuery("SELECT COUNT(*) FROM tasks WHERE is_done=1", null)
        if (curDone.moveToFirst()) done = curDone.getInt(0)
        curDone.close()

        return Pair(pending, done)
    }

    fun updatePassword(email: String, newPass: String): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("password", newPass)
        val rowsAffected = db.update("users", values, "email=?", arrayOf(email))
        return rowsAffected > 0
    }
}