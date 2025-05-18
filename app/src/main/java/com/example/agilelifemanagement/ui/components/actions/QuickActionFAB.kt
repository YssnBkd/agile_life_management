package com.example.agilelifemanagement.ui.components.actions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

/**
 * A floating action button that expands to show quick actions when clicked
 */
@Composable
fun QuickActionFAB(
    onActionClick: (QuickActions) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f, 
        label = "FAB rotation"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        // Quick action menu
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
        ) {
            Surface(
                modifier = Modifier
                    .padding(bottom = 76.dp)
                    .clip(RoundedCornerShape(16.dp)),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    QuickActions.values().forEach { action ->
                        QuickActionItem(
                            action = action,
                            onClick = {
                                onActionClick(action)
                                expanded = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    onActionClick(action)
                                    expanded = false
                                }
                                .padding(vertical = 12.dp, horizontal = 16.dp)
                        )
                        
                        if (action.ordinal < QuickActions.values().size - 1) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            )
                        }
                    }
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
                imageVector = Icons.Rounded.Add,
                contentDescription = "Quick Actions",
                modifier = Modifier.rotate(rotationAngle)
            )
        }
    }
}
