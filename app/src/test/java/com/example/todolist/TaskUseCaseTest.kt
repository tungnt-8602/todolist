package com.example.todolist

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.todolist.data.AppDatabase
import com.example.todolist.data.dao.TaskDao
import com.example.todolist.data.entity.TaskEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
@Config(manifest = Config.NONE)
class TaskUseCaseTest {
    private lateinit var taskDao: TaskDao
    private lateinit var database: AppDatabase

    private val testTask1 =
        TaskEntity(id = 1, title = "Task 1", description = "Description 1", isCompleted = false)
    private val testTask2 =
        TaskEntity(id = 2, title = "Task 2", description = null, isCompleted = false)
    private val testTask3 =
        TaskEntity(id = 3, title = "Test", description = "Important Task", isCompleted = false)

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        taskDao = database.taskDao()

        runBlocking {
            taskDao.addTask(testTask1)
            taskDao.addTask(testTask2)
            taskDao.addTask(testTask3)
        }
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun addTask_insertsTaskSuccessfully() = runBlocking {
        logTestStart("Adding a new task to the list")

        val newTask =
            TaskEntity(title = "New Task", description = "New Description", isCompleted = false)

        logTasks("Before adding new task")
        taskDao.addTask(newTask)
        logTasks("After adding new task")

        val tasks = taskDao.getTasks().first()
        assertThat(tasks.any { it.title == "New Task" && it.description == "New Description" }).isTrue()

        logTestEnd()
    }

    @Test
    fun addTask_withNullDescription_insertsSuccessfully() = runBlocking {
        logTestStart("Adding a task with null description to the list")

        val newTask = TaskEntity(
            title = "Task With Null Description",
            description = null,
            isCompleted = false
        )

        logTasks("Before adding new task")

        taskDao.addTask(newTask)
        logTasks("After adding new task")
        val tasks = taskDao.getTasks().first()
        assertThat(tasks.any { it.title == "Task With Null Description" && it.description == null }).isTrue()

        logTestEnd()
    }

    @Test
    fun deleteTask_removesTaskSuccessfully() = runBlocking {
        logTestStart("Deleting a task from the list")

        logTasks("Before deleting task")
        taskDao.deleteTask(1)
        logTasks("After deleting task")

        val tasks = taskDao.getTasks().first()
        assertThat(tasks.any { it.id == 1 }).isFalse()

        logTestEnd()
    }

    @Test
    fun deleteTask_withNonExistentId_doesNothing(): Unit = runBlocking {
        logTestStart("Attempting to delete a non-existent task")

        logTasks("Before attempting to delete non-existent task")
        taskDao.deleteTask(999) // ID does not exist
        logTasks("After attempting to delete non-existent task")

        val tasksAfterDeletion = taskDao.getTasks().first()
        assertThat(tasksAfterDeletion).containsExactly(testTask1, testTask2, testTask3)

        logTestEnd()
    }

    @Test
    fun searchTask_returnsCorrectResults(): Unit = runBlocking {
        logTestStart("Searching for tasks with a matching keyword")

        logTasks("Available tasks before searching")
        println("Search keyword: Ta")
        val result = taskDao.searchTasks("Ta")
        logTasks("Search results", result)

        assertThat(result.first()).containsExactly(testTask1, testTask2)

        logTestEnd()
    }

    @Test
    fun searchTask_withNoMatchingResults_returnsEmptyList(): Unit = runBlocking {
        logTestStart("Searching for a non-existent task")

        logTasks("Available tasks before searching")
        println("Search keyword: NonExistentTask")

        val result = taskDao.searchTasks("NonExistentTask")
        logTasks("Search results", result)

        assertThat(result.first()).isEmpty()

        logTestEnd()
    }

    @Test
    fun checkTask_togglesCompletionStatus() = runBlocking {
        logTestStart("Toggling task completion status")

        logTasks("Before toggling task completion")
        taskDao.checkTask(1)
        logTasks("After toggling task completion")

        val updatedTask = taskDao.getTasks().first().find { it.id == 1 }
        assertThat(updatedTask?.isCompleted).isTrue()

        logTestEnd()
    }

    @Test
    fun updateTask_changesTaskDetails() = runBlocking {
        logTestStart("Updating task details")

        val updatedTask =
            testTask1.copy(title = "Updated Task", description = "Updated Description")

        logTasks("Before updating task")
        taskDao.updateTask(updatedTask)
        logTasks("After updating task")

        val tasks = taskDao.getTasks().first()
        assertThat(tasks.any { it.id == 1 && it.title == "Updated Task" && it.description == "Updated Description" }).isTrue()

        logTestEnd()
    }

    /**
     * Logs the start of a test
     */
    private fun logTestStart(testName: String) {
        println("\n[START] Running test: $testName")
    }

    /**
     * Logs the current list of tasks
     */
    private suspend fun logTasks(
        prefix: String,
        dataLog: Flow<List<TaskEntity>> = taskDao.getTasks()
    ) {
        println("\n$prefix:")
        if (dataLog.first().isEmpty()) {
            println("Tasks: ${dataLog.first()}")
            return
        }
        dataLog.first().forEach { println("Task: $it") }
    }

    /**
     * Logs the end of a test
     */
    private fun logTestEnd() {
        println("\n--------------------------------------")
    }
}