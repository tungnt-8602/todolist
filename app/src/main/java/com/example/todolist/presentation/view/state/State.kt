package com.example.todolist.presentation.view.state

import com.example.todolist.presentation.model.Task

data class TaskState(
    val pendingTasks: List<Task> = emptyList(),
    val showPendingTasks: Boolean = true,
    val finishedTasks: List<Task> = emptyList(),
    val showFinishedTasks: Boolean = true,
    val searchQuery: String = ""
)

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
    data object Empty : Resource<Nothing>()
}