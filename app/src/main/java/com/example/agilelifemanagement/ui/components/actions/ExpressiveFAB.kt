package com.example.agilelifemanagement.ui.components.actions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme

/**
 * QuickActionFAB component for Material 3 Expressive
 * 
 * Features:
 * - Expandable actions with smooth animations
 * - Tactile visual feedback on interaction
 * - High visibility with primary action focus
 * - Labeled actions for better clarity
 */
@Composable
fun QuickActionFAB(
    modifier: Modifier = Modifier,
    actions: List<QuickAction>,
    onActionClick: (QuickAction) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 135f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "fabRotation"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        // Expanded actions
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 80.dp, end = 8.dp)
            ) {
                actions.forEach { action ->
                    QuickActionItem(
                        action = action,
                        onClick = {
                            onActionClick(action)
                            expanded = false
                        }
                    )
                }
            }
        }
        
        // Semi-transparent overlay when expanded
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                modifier = Modifier.matchParentSize(),
                color = Color.Black.copy(alpha = 0.3f),
                onClick = { expanded = false }
            ) {}
        }
        
        // Main FAB
        FloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 12.dp
            ),
            shape = MaterialTheme.shapes.large
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = if (expanded) "Close Quick Actions" else "Open Quick Actions",
                modifier = Modifier
                    .size(28.dp)
                    .rotate(rotation)
            )
        }
    }
}

/**
 * Individual quick action item with label and FAB
 */
@Composable
private fun QuickActionItem(
    action: QuickAction,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 4.dp)
    ) {
        // Label with contrasting background for better visibility
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 2.dp
        ) {
            Text(
                text = action.label,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Action button with custom color
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = action.color,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.label,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Quick action data class for FAB actions
 */
data class QuickAction(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val color: Color
)

/**
 * Predefined quick actions for the dashboard
 */
object QuickActions {
    val NewTask = QuickAction(
        id = "new_task",
        label = "New Task",
        icon = Icons.Rounded.Edit,
        color = AgileLifeTheme.extendedColors.accentCoral
    )
    
    val CheckIn = QuickAction(
        id = "check_in",
        label = "Check-In",
        icon = Icons.Rounded.Check,
        color = AgileLifeTheme.extendedColors.accentMint
    )
    
    val QuickNote = QuickAction(
        id = "quick_note",
        label = "Quick Note",
        icon = Icons.Rounded.Edit,
        color = AgileLifeTheme.extendedColors.accentLavender
    )
    
    val AllActions = listOf(NewTask, CheckIn, QuickNote)
}
