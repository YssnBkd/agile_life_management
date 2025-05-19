package com.example.agilelifemanagement.ui.screens.task.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A reusable search bar component for task screens.
 * Manages both active and inactive states for searching tasks.
 *
 * @param query Current search query text
 * @param onQueryChange Callback when query text changes
 * @param onSearch Callback when search is submitted
 * @param active Whether search is currently active
 * @param onActiveChange Callback when active state changes
 * @param onBackClick Callback when back button is clicked in active state
 * @param placeholder Optional placeholder text
 * @param modifier Optional modifier
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    placeholder: String = "Search tasks...",
    modifier: Modifier = Modifier
) {
    SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        active = active,
        onActiveChange = onActiveChange,
        placeholder = { Text(text = placeholder) },
        leadingIcon = {
            if (active) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search"
                )
            }
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        colors = SearchBarDefaults.colors(),
        modifier = modifier.fillMaxWidth(),
        content = {}
    )
}
