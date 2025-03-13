package com.example.todolist.presentation.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.todolist.R
import com.example.todolist.presentation.model.Task
import com.example.todolist.presentation.view.theme.Typography
import com.example.todolist.presentation.model.TypeDialog

@Composable
fun CustomTextField(
    value: String = "",
    placeholder: String = "",
    backgroundColor: Color = Color.White,
    textColor: Color = Color.Black,
    onValueChange: (String) -> Unit = {},
) {
    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        singleLine = true,
        textStyle = Typography.bodySmall.copy(
            fontSize = 13.sp,
            color = textColor
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor, RoundedCornerShape(10.dp))
                    .height(40.dp)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        placeholder, style = Typography.bodyMedium.copy(
                            color = textColor, fontSize = 13.sp
                        )
                    )
                }
                innerTextField()
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun TaskDialog(
    modifier: Modifier = Modifier,
    onSaveTask: (String, String?) -> Unit = { _, _ -> },
    onDeleteTask: (Int) -> Unit = {},
    onDismiss: () -> Unit = {},
    typeDialog: TypeDialog = TypeDialog.ADD,
    taskUpdate: Task? = null
) {
    val isUpdate = taskUpdate != null && typeDialog == TypeDialog.UPDATE
    var title by remember { mutableStateOf(if (isUpdate) taskUpdate?.title.toString() else "") }
    var description by remember { mutableStateOf(if (isUpdate) taskUpdate?.description else "") }
    Dialog(onDismiss) {
        Column(
            modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Box {
                Text(
                    text = when (typeDialog) {
                        TypeDialog.ADD -> stringResource(R.string.add_task)
                        TypeDialog.UPDATE -> stringResource(R.string.update_task)
                    },
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    style = Typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    textAlign = TextAlign.Center
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable {
                            onDismiss.invoke()
                        }
                        .padding(8.dp)
                )
            }
            CustomTextField(
                title,
                stringResource(R.string.title),
                backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                textColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                title = it
            }
            CustomTextField(
                description ?: "",
                stringResource(R.string.description),
                backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                textColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                description = it
            }
            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isUpdate) {
                    OutlinedButton(
                        onClick = {
                            taskUpdate?.let {
                                onDeleteTask(it.id)
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enabled = title.isNotEmpty()
                    ) {
                        Text(
                            text = stringResource(R.string.delete),
                            style = Typography.titleLarge.copy(
                                fontSize = 16.sp, lineHeight = 20.sp
                            )
                        )
                    }
                }
                Button(
                    onClick = {
                        onSaveTask(title, description?.ifEmpty { null })
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    enabled = title.isNotEmpty()
                ) {
                    Text(
                        text = stringResource(R.string.save),
                        style = Typography.titleLarge.copy(
                            color = Color.White, fontSize = 16.sp, lineHeight = 20.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmDialog(
    modifier: Modifier = Modifier,
    onDeleteTask: (Int) -> Unit = {},
    onDismiss: () -> Unit = {},
    taskDelete: Task,
) {
    Dialog(onDismiss) {
        Column(
            modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = stringResource(R.string.delete_confirm),
                modifier = Modifier.padding(16.dp),
                style = Typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.delete_confirm_detail, taskDelete.title),
                modifier = Modifier.padding(8.dp),
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        onDismiss.invoke()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = Typography.titleLarge.copy(
                            fontSize = 16.sp, lineHeight = 20.sp
                        )
                    )
                }
                Button(
                    onClick = {
                        onDeleteTask.invoke(taskDelete.id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        style = Typography.titleLarge.copy(
                            color = Color.White, fontSize = 16.sp, lineHeight = 20.sp
                        )
                    )
                }
            }
        }
    }
}