package com.example.todolist.presentation.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todolist.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.todolist.presentation.view.component.DeleteConfirmDialog
import com.example.todolist.presentation.view.component.TaskDialog
import com.example.todolist.presentation.view.state.Resource
import com.example.todolist.presentation.intent.TaskIntent
import com.example.todolist.presentation.model.Task
import com.example.todolist.presentation.model.TypeDialog
import com.example.todolist.presentation.view.component.SearchTopAppBar
import com.example.todolist.presentation.view.component.TaskCategory
import com.example.todolist.presentation.view.component.TaskItem
import com.example.todolist.presentation.view.theme.ToDoListTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                TaskScreen()
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val tasksState by viewModel.uiState.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    var dialogState by remember { mutableStateOf(TypeDialog.ADD) }
    var taskUpdate by remember { mutableStateOf<Task?>(null) }
    var isSearching by remember { mutableStateOf(false) }
    var querySearch by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = tasksState is Resource.Loading,
        onRefresh = {
            viewModel.processIntent(TaskIntent.SearchTask(querySearch))
        }
    )

    if (showDialog) {
        TaskDialog(
            onDismiss = {
                showDialog = false
            },
            onDeleteTask = {
                showDialog = false
                showConfirm = true
            },
            onSaveTask = { title, description ->
                when (dialogState) {
                    TypeDialog.ADD -> {
                        viewModel.processIntent(TaskIntent.AddTask(title, description))
                    }

                    TypeDialog.UPDATE -> {
                        taskUpdate?.let {
                            viewModel.processIntent(
                                TaskIntent.UpdateTask(
                                    Task(
                                        it.id,
                                        title,
                                        description,
                                        it.isCompleted
                                    )
                                )
                            )
                        }
                    }
                }
                showDialog = false
            }, typeDialog = dialogState, taskUpdate = taskUpdate
        )
    }

    if (showConfirm) {
        taskUpdate?.let {
            DeleteConfirmDialog(
                onDismiss = {
                    showConfirm = false
                },
                onDeleteTask = { taskId ->
                    viewModel.processIntent(TaskIntent.DeleteTask(taskId))
                    showConfirm = false
                },
                taskDelete = it
            )
        }
    }

    LaunchedEffect(querySearch) {
        viewModel.processIntent(TaskIntent.SearchTask(querySearch))
    }

    Scaffold(
        topBar = {
            SearchTopAppBar(
                isSearching = isSearching,
                searchQuery = querySearch,
                onSearchToggle = {
                    isSearching = it
                },
                onSearchQueryChange = {
                    querySearch = it
                },
                title = stringResource(R.string.app_name)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .pullRefresh(pullRefreshState),
        ) {
            when (tasksState) {
                is Resource.Success -> {
                    val taskState = (tasksState as Resource.Success).data
                    Box(modifier = Modifier.fillMaxSize()) {
                        taskState.takeIf { it.pendingTasks.size + it.finishedTasks.size != 0 }
                            ?.let { taskState ->
                                Column {
                                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                                        val finishedTasks = taskState.finishedTasks
                                        val pendingTasks = taskState.pendingTasks
                                        pendingTasks.takeIf { it.isNotEmpty() }?.let {
                                            item {
                                                TaskCategory(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    title = stringResource(
                                                        R.string.tasks_count,
                                                        stringResource(R.string.pending),
                                                        pendingTasks.size
                                                    ),
                                                    isShowTasks = taskState.showPendingTasks,
                                                    onClick = {
                                                        viewModel.processIntent(TaskIntent.TogglePendingTasks)
                                                    }
                                                )
                                            }
                                            if (taskState.showPendingTasks) {
                                                items(items = pendingTasks) { task ->
                                                    TaskItem(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(horizontal = 8.dp),
                                                        data = task, onCheckedChange = {
                                                            viewModel.processIntent(
                                                                TaskIntent.CheckTask(
                                                                    task.id
                                                                )
                                                            )
                                                        }) {
                                                        dialogState = TypeDialog.UPDATE
                                                        showDialog = true
                                                        taskUpdate = task
                                                    }
                                                }
                                                if (finishedTasks.isEmpty()) {
                                                    item {
                                                        Spacer(modifier = Modifier.height(16.dp))
                                                    }
                                                }
                                            }
                                        }
                                        finishedTasks.takeIf { it.isNotEmpty() }?.let {
                                            item {
                                                TaskCategory(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    title = stringResource(
                                                        R.string.tasks_count,
                                                        stringResource(R.string.finished),
                                                        finishedTasks.size
                                                    ),
                                                    isShowTasks = taskState.showFinishedTasks,
                                                    onClick = {
                                                        viewModel.processIntent(TaskIntent.ToggleFinishedTasks)
                                                    }
                                                )
                                            }
                                            if (taskState.showFinishedTasks) {
                                                items(items = finishedTasks) { task ->
                                                    TaskItem(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(horizontal = 8.dp),
                                                        data = task,
                                                        onCheckedChange = {
                                                            viewModel.processIntent(
                                                                TaskIntent.CheckTask(
                                                                    task.id
                                                                )
                                                            )
                                                        }) {
                                                        dialogState = TypeDialog.UPDATE
                                                        showDialog = true
                                                        taskUpdate = task
                                                    }
                                                }
                                                item {
                                                    Spacer(modifier = Modifier.height(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            } ?: run {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_empty_task),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(100.dp)
                                )
                            }
                        }
                        FloatingActionButton(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(bottom = 16.dp, end = 16.dp),
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.medium,
                            onClick = {
                                dialogState = TypeDialog.ADD
                                showDialog = true
                                taskUpdate = null
                            }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    }
                }

                is Resource.Error -> {
                    val error = (tasksState as Resource.Error)
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                    scope.launch {
                        delay(1000)
                        viewModel.processIntent(TaskIntent.SearchTask(querySearch))
                    }
                }

                is Resource.Loading -> {}

                is Resource.Empty -> {}
            }

            PullRefreshIndicator(
                refreshing = tasksState is Resource.Loading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}