package vcc.ntt.todoapp.domain

import android.content.Context
import com.example.todolist.data.AppDatabase
import com.example.todolist.data.dao.TaskDao
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.todolist.data.entity.TaskEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private var taskDao: TaskDao? = null

    /* **********************************************************************
     * Init
     ********************************************************************** */
    fun initDb() {
        if (taskDao != null) return
        val database = AppDatabase.getDatabase(context)
        taskDao = database.taskDao()
    }

    /* **********************************************************************
     * Task Use Cases
     ********************************************************************** */
    fun addTask(task: TaskEntity) {
        taskDao?.addTask(task)
    }

    fun updateTask(task: TaskEntity) {
        taskDao?.updateTask(task)
    }

    fun deleteTask(taskId: Int) {
        taskDao?.deleteTask(taskId)
    }

    fun searchTask(query: String) = taskDao?.searchTasks(query)

    fun checkTask(taskId: Int) = taskDao?.checkTask(taskId)

    fun getAllTasks() = taskDao?.getTasks()
}