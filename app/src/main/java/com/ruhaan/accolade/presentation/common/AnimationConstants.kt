package com.ruhaan.accolade.presentation.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset

object AnimationConstants {
    // Navigation transition duration
    const val NAVIGATION_DURATION = 400

    // Easing for smooth feel
    val NAVIGATION_EASING = FastOutSlowInEasing

    // Animation spec for navigation
    fun <T> navigationTween() = tween<T>(
        durationMillis = NAVIGATION_DURATION,
        easing = NAVIGATION_EASING
    )

    fun navigationTweenIntOffset() = tween<IntOffset>(
        durationMillis = NAVIGATION_DURATION,
        easing = NAVIGATION_EASING
    )
}