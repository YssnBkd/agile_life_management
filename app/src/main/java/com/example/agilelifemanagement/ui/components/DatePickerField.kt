package com.example.agilelifemanagement.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    date: LocalDate? = null,
    onDateSelected: (LocalDate?) -> Unit,
    allowRemoval: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    
    OutlinedTextField(
        value = date?.format(dateFormatter) ?: "Not set",
        onValueChange = { /* Read-only field, handled by DatePicker */ },
        label = { Text(label) },
        readOnly = true,
        modifier = modifier.clickable { showDatePicker = true },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Select Date"
            )
        },
        trailingIcon = if (allowRemoval && date != null) {
            {
                IconButton(onClick = { onDateSelected(null) }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear Date"
                    )
                }
            }
        } else null
    )
    
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date?.let {
                java.time.ZoneOffset.UTC.let { zone ->
                    it.atStartOfDay().toInstant(zone).toEpochMilli()
                }
            }
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = java.time.Instant
                                .ofEpochMilli(millis)
                                .atZone(java.time.ZoneOffset.UTC)
                                .toLocalDate()
                            onDateSelected(selectedDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
