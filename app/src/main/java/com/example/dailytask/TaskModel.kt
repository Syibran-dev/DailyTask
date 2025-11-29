package com.example.dailytask

data class TaskModel(
    val id: Int,
    val taskName: String,
    var isDone: Boolean,
    val taskDate: String = "", // Format: DD/MM/YYYY or similar
    val taskDesc: String = ""
)
