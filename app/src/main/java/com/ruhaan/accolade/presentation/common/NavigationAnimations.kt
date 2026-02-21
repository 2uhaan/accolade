package com.ruhaan.accolade.presentation.common

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry

object NavigationAnimations {

  // Define bottom nav route order (matches your FloatingBottomBar)
  private val mainRouteOrder = listOf("home", "schedule", "category")

  /**
   * Determines slide direction based on route positions in bottom nav Returns true if sliding left
   * (forward), false if sliding right (backward)
   */
  private fun shouldSlideLeft(
      initialState: NavBackStackEntry,
      targetState: NavBackStackEntry,
  ): Boolean {
    val fromRoute = initialState.destination.route ?: return true
    val toRoute = targetState.destination.route ?: return true

    val fromIndex = mainRouteOrder.indexOf(fromRoute)
    val toIndex = mainRouteOrder.indexOf(toRoute)

    // If routes not in main nav, default to slide left
    if (fromIndex == -1 || toIndex == -1) return true

    // Slide left if moving to higher index (forward)
    return toIndex > fromIndex
  }

  /** Enter transition for main navigation screens */
  fun mainScreenEnter(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    val slideLeft = shouldSlideLeft(initialState, targetState)

    slideIntoContainer(
        towards =
            if (slideLeft) {
              AnimatedContentTransitionScope.SlideDirection.Left
            } else {
              AnimatedContentTransitionScope.SlideDirection.Right
            },
        animationSpec = AnimationConstants.navigationTweenIntOffset(),
    ) + fadeIn(animationSpec = AnimationConstants.navigationTween())
  }

  /** Exit transition for main navigation screens */
  fun mainScreenExit(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    val slideLeft = shouldSlideLeft(initialState, targetState)

    slideOutOfContainer(
        towards =
            if (slideLeft) {
              AnimatedContentTransitionScope.SlideDirection.Left
            } else {
              AnimatedContentTransitionScope.SlideDirection.Right
            },
        animationSpec = AnimationConstants.navigationTweenIntOffset(),
    ) + fadeOut(animationSpec = AnimationConstants.navigationTween())
  }

  /** Pop enter transition (when coming back) */
  fun mainScreenPopEnter():
      AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = AnimationConstants.navigationTweenIntOffset(),
    ) + fadeIn(animationSpec = AnimationConstants.navigationTween())
  }

  /** Pop exit transition (when going back) */
  fun mainScreenPopExit(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
      {
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = AnimationConstants.navigationTweenIntOffset(),
        ) + fadeOut(animationSpec = AnimationConstants.navigationTween())
      }
}
