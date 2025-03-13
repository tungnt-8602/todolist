package com.example.todolist.presentation.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.common.extention.toDomain
import com.example.todolist.common.extention.toEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import com.example.todolist.data.entity.TaskEntity
import com.example.todolist.presentation.intent.TaskIntent
import com.example.todolist.presentation.view.state.Resource
import com.example.todolist.presentation.view.state.TaskState
import vcc.ntt.todoapp.domain.TaskUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val taskDomain: TaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<TaskState>>(Resource.Empty)
    val uiState: StateFlow<Resource<TaskState>> = _uiState.asStateFlow()

    init {
        initDatabase()
    }

    fun processIntent(intent: TaskIntent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (intent) {
                is TaskIntent.AddTask -> {
                    _uiState.value = Resource.Loading
                    taskDomain.addTask(
                        TaskEntity(
                            title = intent.title,
                            description = intent.description
                        )
                    )
                    loadTasks()
                }

                is TaskIntent.UpdateTask -> {
                    _uiState.value = Resource.Loading
                    taskDomain.updateTask(
                        intent.task.toEntity()
                    )
                    loadTasks()
                }

                is TaskIntent.DeleteTask -> {
                    taskDomain.deleteTask(intent.id)
                }

                is TaskIntent.SearchTask -> {
                    taskDomain.searchTask(intent.query)?.catch {
                        _uiState.value = Resource.Error(
                            it.message ?: "Unknown error"
                        )
                    }?.collect { tasks ->
                        val finishedTasks = tasks.filter { it.isCompleted }.map { it.toDomain() }
                        val pendingTasks = tasks.filter { !it.isCompleted }.map { it.toDomain() }
                        _uiState.value = Resource.Success(
                            TaskState(
                                finishedTasks = finishedTasks,
                                pendingTasks = pendingTasks,
                                searchQuery = intent.query
                            )
                        )
                    }
                }

                is TaskIntent.CheckTask -> {
                    taskDomain.checkTask(intent.taskId)
                }

                is TaskIntent.ToggleFinishedTasks -> {
                    val currentState = (_uiState.value as Resource.Success).data
                    _uiState.value = Resource.Success(
                        currentState.copy(
                            showFinishedTasks = !currentState.showFinishedTasks
                        )
                    )
                }

                is TaskIntent.TogglePendingTasks -> {
                    val currentState = (_uiState.value as Resource.Success).data
                    _uiState.value = Resource.Success(
                        currentState.copy(
                            showPendingTasks = !currentState.showPendingTasks
                        )
                    )
                }
            }
        }
    }

    private fun initDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            taskDomain.initDb()
        }
    }

    private fun loadTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            taskDomain.getAllTasks()?.flowOn(Dispatchers.IO)?.catch {
                _uiState.value = Resource.Error(
                    it.message ?: "Unknown error"
                )
            }?.collect { tasks ->
                val finishedTasks = tasks.filter { it.isCompleted }.map { it.toDomain() }
                val pendingTasks = tasks.filter { !it.isCompleted }.map { it.toDomain() }
                _uiState.value = Resource.Success(
                    TaskState(
                        finishedTasks = finishedTasks,
                        pendingTasks = pendingTasks
                    )
                )
            }
        }
    }
}