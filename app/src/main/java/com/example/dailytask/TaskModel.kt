package com.example.dailytask

data class TaskModel(
    val id: Int,
    val taskName: String,
    var isDone: Boolean,
    val taskDate: String = "",
    val taskDesc: String = "",
    val ownerName: String = ""
)
