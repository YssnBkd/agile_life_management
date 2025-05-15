package com.example.agilelifemanagement.ui.components.cards

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * ExpressiveCard component following Material 3 Expressive principles
 * 
 * Features:
 * - More generous corner radius
 * - Subtle elevation change on press
 * - Optional accent color 
 * - Vibrant but harmonious color scheme
 */
@Composable
fun ExpressiveCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    borderColor: Color? = null,
    borderWidth: Dp = 1.dp,
    elevation: Dp = 2.dp,
    pressedElevation: Dp = 0.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable ColumnScope.() -> Unit
) {
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Animate elevation change on press
    val animatedElevation by animateDpAsState(
        targetValue = if (isPressed && onClick != null) pressedElevation else elevation,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardElevation"
    )
    
    // Animate container color to slightly darker when pressed
    val animatedContainerColor by animateColorAsState(
        targetValue = if (isPressed && onClick != null) {
            containerColor.copy(
                red = (containerColor.red * 0.95f).coerceAtLeast(0f),
                green = (containerColor.green * 0.95f).coerceAtLeast(0f),
                blue = (containerColor.blue * 0.95f).coerceAtLeast(0f)
            )
        } else containerColor,
        label = "containerColor"
    )

    Card(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = animatedContainerColor,
            contentColor = contentColor,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = animatedElevation
        ),
        border = borderColor?.let { BorderStroke(borderWidth, it) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}

/**
 * Feature Card with title and optional accent color
 */
@Composable
fun FeatureCard(
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    accentColor: Color? = null,
    elevation: Dp = 2.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    ExpressiveCard(
        modifier = modifier,
        onClick = onClick,
        containerColor = containerColor,
        contentColor = contentColor,
        borderColor = accentColor,
        elevation = elevation
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = accentColor ?: contentColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

/**
 * Summary Card with subtle background and lower elevation
 */
@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    title: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable ColumnScope.() -> Unit
) {
    ExpressiveCard(
        modifier = modifier,
        onClick = onClick,
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = 1.dp
    ) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        content()
    }
}
