package com.ruhaan.accolade.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ruhaan.accolade.domain.model.MediaType
import com.ruhaan.accolade.presentation.category.AllCategoriesScreen
import com.ruhaan.accolade.presentation.category.CategoryScreen
import com.ruhaan.accolade.presentation.detail.DetailScreen
import com.ruhaan.accolade.presentation.detail.components.CastScreen
import com.ruhaan.accolade.presentation.detail.components.CrewScreen
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

      composable("category") { AllCategoriesScreen(navController = navController) }

      // Detail Screen
      composable(
          route = "detail/{movieId}/{mediaType}",
          arguments =
              listOf(
                  navArgument("movieId") { type = NavType.IntType },
                  navArgument("mediaType") { type = NavType.StringType },
              ),
      ) { backStackEntry ->
        val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
        val mediaTypeString = backStackEntry.arguments?.getString("mediaType") ?: "MOVIE"
        val mediaType = MediaType.valueOf(mediaTypeString)

        DetailScreen(navController = navController, movieId = movieId, mediaType = mediaType)
      }

      // Cast Screen
      composable(
          route = "cast/{movieId}/{mediaType}",
          arguments =
              listOf(
                  navArgument("movieId") { type = NavType.IntType },
                  navArgument("mediaType") { type = NavType.StringType },
              ),
      ) { backStackEntry ->
        val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
        val mediaTypeString = backStackEntry.arguments?.getString("mediaType") ?: "MOVIE"
        val mediaType = MediaType.valueOf(mediaTypeString)

        CastScreen(navController = navController, movieId = movieId, mediaType = mediaType)
      }

      // Crew Screen
      composable(
          route = "crew/{movieId}/{mediaType}",
          arguments =
              listOf(
                  navArgument("movieId") { type = NavType.IntType },
                  navArgument("mediaType") { type = NavType.StringType },
              ),
      ) { backStackEntry ->
        val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
        val mediaTypeString = backStackEntry.arguments?.getString("mediaType") ?: "MOVIE"
        val mediaType = MediaType.valueOf(mediaTypeString)

        CrewScreen(navController = navController, movieId = movieId, mediaType = mediaType)
      }
      // ADD THIS - Category Screen
      composable(
          route = "category/{genreId}/{genreName}",
          arguments =
              listOf(
                  navArgument("genreId") { type = NavType.IntType },
                  navArgument("genreName") { type = NavType.StringType },
              ),
      ) { backStackEntry ->
        val genreId = backStackEntry.arguments?.getInt("genreId") ?: 0
        val genreName = backStackEntry.arguments?.getString("genreName") ?: "Category"

        CategoryScreen(navController = navController, genreId = genreId, genreName = genreName)
      }
    }

    // Bottom bar overlays on top
    FloatingBottomBar(
        navController = navController,
        modifier = Modifier.align(Alignment.BottomCenter),
    )
  }
}
