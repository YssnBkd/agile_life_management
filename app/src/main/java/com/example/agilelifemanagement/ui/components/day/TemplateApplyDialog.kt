package com.example.agilelifemanagement.ui.components.day

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.agilelifemanagement.domain.model.DayTemplate
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Dialog for applying a template to a specific date.
 *
 * @param template The template to apply
 * @param onDismiss Callback when dialog is dismissed
 * @param onApply Callback when template is applied to a date
 * @param modifier Optional modifier
 */
@Composable
fun TemplateApplyDialog(
    template: DayTemplate,
    onDismiss: () -> Unit,
    onApply: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    // Default to today's date
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    // Date formatter
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.getDefault())
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Apply Template") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Template: ${template.name}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Activities: ${template.activities.size}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Apply to date:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Simple date selector - could be enhanced with a date picker
                Button(
                    onClick = { /* Show date picker */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedDate.format(dateFormatter))
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "This will add all template activities to your schedule for the selected date.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApply(selectedDate)
                    onDismiss()
                }
            ) {
                Text("Apply Template")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        modifier = modifier
    )
}
