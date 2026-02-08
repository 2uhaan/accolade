package com.ruhaan.accolade.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ruhaan.accolade.presentation.home.HomeScreen
import com.ruhaan.accolade.presentation.home.components.FloatingBottomBar
import com.ruhaan.accolade.presentation.schedule.ScheduleScreen

@Composable
fun NavGraph() {
  val navController = rememberNavController()

  Box(modifier = Modifier.fillMaxSize()) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.fillMaxSize(),
    ) {
      composable("home") { HomeScreen(navController = navController) }

      composable("schedule") { ScheduleScreen(navController = navController) }

      composable("profile") {
        // Your profile screen
      }
    }

    // Bottom bar overlays on top
    FloatingBottomBar(
        navController = navController,
        modifier = Modifier.align(Alignment.BottomCenter),
    )
  }
}
