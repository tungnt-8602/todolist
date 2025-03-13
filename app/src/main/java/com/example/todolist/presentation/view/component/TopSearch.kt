package com.example.todolist.presentation.view.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.R
import com.example.todolist.presentation.view.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    title: String,
    isSearching: Boolean = false,
    onSearchToggle: (Boolean) -> Unit = {},
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
) {
    Column {
        TopAppBar(
            title = {
                if (isSearching) {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { onSearchQueryChange(it) },
                        singleLine = true,
                        textStyle = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        stringResource(R.string.search_hint),
                                        style = Typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 14.sp
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        },
                    )
                } else {
                    Text(text = title)
                }
            },
            actions = {
                if (isSearching) {
                    IconButton(onClick = {
                        onSearchToggle(false)
                        onSearchQueryChange("")
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                } else {
                    IconButton(onClick = { onSearchToggle(true) }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    }
                }
            }
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
    }
}