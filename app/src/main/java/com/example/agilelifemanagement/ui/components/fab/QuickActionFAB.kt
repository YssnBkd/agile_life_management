package com.example.agilelifemanagement.ui.components.fab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

/**
 * A floating action button that expands to show multiple quick actions
 */
@Composable
fun QuickActionFAB(
    actions: List<QuickAction>,
    onActionClick: (QuickAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) 45f else 0f, label = "FAB rotation")
    
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomEnd
    ) {
        // Action items
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                actions.forEach { action ->
                    ActionItem(action = action, onClick = { 
                        onActionClick(action)
                        expanded = false
                    })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        
        // Main FAB
        FloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = if (expanded) Icons.Rounded.Close else Icons.Rounded.Add,
                contentDescription = if (expanded) "Close menu" else "Open menu",
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

@Composable
private fun ActionItem(
    action: QuickAction,
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        text = { Text(text = action.label) },
        icon = { Icon(imageVector = action.icon, contentDescription = action.description) },
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    )
}
