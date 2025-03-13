package com.example.todolist.presentation.intent

import com.example.todolist.presentation.model.Task

sealed class TaskIntent {
    data class AddTask(val title: String, val description: String?) : TaskIntent()
    data class UpdateTask(val task: Task) : TaskIntent()
    data class CheckTask(val taskId: Int) : TaskIntent()
    data class DeleteTask(val id: Int) : TaskIntent()
    data class SearchTask(val query: String) : TaskIntent()
    data object ToggleFinishedTasks : TaskIntent()
    data object TogglePendingTasks : TaskIntent()
}