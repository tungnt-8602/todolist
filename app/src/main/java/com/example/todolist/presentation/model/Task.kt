package com.example.todolist.presentation.model

data class Task(
    val id: Int = 0,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false
)

enum class TypeDialog {
    ADD, UPDATE
}