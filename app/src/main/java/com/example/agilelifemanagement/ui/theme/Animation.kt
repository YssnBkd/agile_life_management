package com.example.agilelifemanagement.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Stable

/**
 * Animation constants for the Agile Life Management app.
 */
object AnimationTokens {
    // Duration constants
    const val DurationShort1 = 50
    const val DurationShort2 = 100
    const val DurationShort3 = 150
    const val DurationShort4 = 200
    
    const val DurationMedium1 = 250
    const val DurationMedium2 = 300
    const val DurationMedium3 = 350
    const val DurationMedium4 = 400
    
    const val DurationLong1 = 450
    const val DurationLong2 = 500
    const val DurationLong3 = 550
    const val DurationLong4 = 600
    
    // Easing curves
    val StandardEasing = FastOutSlowInEasing
    val EmphasizedEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val DecelerateEasing = LinearOutSlowInEasing
    val AccelerateEasing = FastOutLinearInEasing
    
    // Common animation specs
    @Stable
    fun standardFadeIn(durationMillis: Int = DurationMedium2) = fadeIn(
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = StandardEasing
        )
    )
    
    @Stable
    fun standardFadeOut(durationMillis: Int = DurationMedium2) = fadeOut(
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = StandardEasing
        )
    )
    
    @Stable
    fun slideInFromRight(durationMillis: Int = DurationMedium2) = slideInHorizontally(
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = EmphasizedEasing
        ),
        initialOffsetX = { fullWidth -> fullWidth }
    )
    
    @Stable
    fun slideOutToLeft(durationMillis: Int = DurationMedium2) = slideOutHorizontally(
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = EmphasizedEasing
        ),
        targetOffsetX = { fullWidth -> -fullWidth }
    )
}
