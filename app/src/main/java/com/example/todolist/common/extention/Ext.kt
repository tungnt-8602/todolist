package com.example.todolist.common.extention

import com.example.todolist.data.entity.TaskEntity
import com.example.todolist.presentation.model.Task

fun TaskEntity.toDomain(): Task = Task(id, title, description, isCompleted)
fun Task.toEntity(): TaskEntity = TaskEntity(id, title, description, isCompleted)