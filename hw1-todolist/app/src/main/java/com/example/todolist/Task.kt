package com.example.todolist

data class Task(
    val name: String,
    val deadline: Long? = null,
    val isDone: Boolean = false
)