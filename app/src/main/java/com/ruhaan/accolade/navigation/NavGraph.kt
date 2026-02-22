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
import com.ruhaan.accolade.presentation.common.NavigationAnimations
import com.ruhaan.accolade.presentation.detail.DetailScreen
import com.ruhaan.accolade.presentation.detail.components.CastCrewScreen
import com.ruhaan.accolade.presentation.detail.components.CastCrewScreenType
import com.ruhaan.accolade.presentation.filmography.FilmographyScreen
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
      composable(
          route = "home",
          enterTransition = NavigationAnimations.mainScreenEnter(),
          exitTransition = NavigationAnimations.mainScreenExit(),
          popEnterTransition = NavigationAnimations.mainScreenPopEnter(),
          popExitTransition = NavigationAnimations.mainScreenPopExit(),
      ) {
        HomeScreen(navController = navController)
      }

      composable(
          route = "schedule",
          enterTransition = NavigationAnimations.mainScreenEnter(),
          exitTransition = NavigationAnimations.mainScreenExit(),
          popEnterTransition = NavigationAnimations.mainScreenPopEnter(),
          popExitTransition = NavigationAnimations.mainScreenPopExit(),
      ) {
        ScheduleScreen(navController = navController)
      }

      composable(
          route = "category",
          enterTransition = NavigationAnimations.mainScreenEnter(),
          exitTransition = NavigationAnimations.mainScreenExit(),
          popEnterTransition = NavigationAnimations.mainScreenPopEnter(),
          popExitTransition = NavigationAnimations.mainScreenPopExit(),
      ) {
        AllCategoriesScreen(navController = navController)
      }

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

      composable(
          route = "castcrew/{movieId}/{mediaType}/{screenType}",
          arguments =
              listOf(
                  navArgument("movieId") { type = NavType.IntType },
                  navArgument("mediaType") { type = NavType.StringType },
                  navArgument("screenType") { type = NavType.StringType },
              ),
      ) { backStackEntry ->
        val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
        val mediaTypeString = backStackEntry.arguments?.getString("mediaType") ?: return@composable
        val screenTypeString =
            backStackEntry.arguments?.getString("screenType") ?: return@composable

        val mediaType = MediaType.valueOf(mediaTypeString.uppercase())
        val screenType = CastCrewScreenType.valueOf(screenTypeString.uppercase())

        CastCrewScreen(
            navController = navController,
            movieId = movieId,
            mediaType = mediaType,
            screenType = screenType,
            onPersonClick = { personId -> navController.navigate("filmography/$personId") },
        )
      }

      composable("filmography/{personId}") { backStackEntry ->
        val personId = backStackEntry.arguments?.getString("personId")?.toInt() ?: return@composable
        FilmographyScreen(navController = navController, personId = personId)
      }

      // Category Screen
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

    FloatingBottomBar(
        navController = navController,
        modifier = Modifier.align(Alignment.BottomCenter),
    )
  }
}
