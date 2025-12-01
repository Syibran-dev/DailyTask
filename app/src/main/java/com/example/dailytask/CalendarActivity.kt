package com.example.dailytask

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CalendarActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var rvCalendarTasks: RecyclerView
    private lateinit var tvEmptyMessage: TextView
    private lateinit var taskAdapter: TaskAdapter
    private var userEmail: String = ""
    private var allTasks: ArrayList<TaskModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        db = DatabaseHelper(this)
        userEmail = intent.getStringExtra("EXTRA_EMAIL") ?: ""

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val tvSelectedDate = findViewById<TextView>(R.id.tvSelectedDate)
        rvCalendarTasks = findViewById(R.id.rvCalendarTasks)
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage)

        setupRecyclerView()
        loadAllTasks()

        tvSelectedDate.text = "Pilih tanggal untuk melihat tugas"

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val displayDate = "$dayOfMonth/${month + 1}/$year"
            tvSelectedDate.text = "Tugas tanggal: $displayDate"
            
            filterTasksByDate(dayOfMonth, month + 1, year)
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
                taskAdapter.notifyDataSetChanged()
            },
            onDelete = { 
                 Toast.makeText(this, "Hapus tugas hanya dari Dashboard utama.", Toast.LENGTH_SHORT).show()
            },
            isEditable = false
        )
        rvCalendarTasks.layoutManager = LinearLayoutManager(this)
        rvCalendarTasks.adapter = taskAdapter
    }

    private fun loadAllTasks() {
        if (userEmail.isNotEmpty()) {
            allTasks = db.getUserTasks(userEmail)
        }
    }

    private fun filterTasksByDate(day: Int, month: Int, year: Int) {
        val filteredList = ArrayList<TaskModel>()

        for (task in allTasks) {
            val dateStr = task.taskDate
            if (isDateMatch(dateStr, day, month, year)) {
                filteredList.add(task)
            }
        }

        taskAdapter.updateData(filteredList)

        if (filteredList.isEmpty()) {
            tvEmptyMessage.visibility = View.VISIBLE
            rvCalendarTasks.visibility = View.GONE
        } else {
            tvEmptyMessage.visibility = View.GONE
            rvCalendarTasks.visibility = View.VISIBLE
        }
    }

    private fun isDateMatch(dateString: String, targetDay: Int, targetMonth: Int, targetYear: Int): Boolean {
        if (dateString.isEmpty()) return false
        
        try {
            val parts = dateString.split("/")
            if (parts.size == 3) {
                val d = parts[0].toIntOrNull() ?: return false
                val m = parts[1].toIntOrNull() ?: return false
                val y = parts[2].toIntOrNull() ?: return false
                
                return d == targetDay && m == targetMonth && y == targetYear
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }
    
    override fun onResume() {
        super.onResume()
        loadAllTasks()
    }
}
