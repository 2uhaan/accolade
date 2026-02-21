package com.ruhaan.accolade.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ruhaan.accolade.presentation.common.AppSpacing
import com.ruhaan.accolade.presentation.common.MainNavigationScreen
import com.ruhaan.accolade.presentation.common.MovieCard
import com.ruhaan.accolade.presentation.common.ShimmerMovieCard
import com.ruhaan.accolade.presentation.home.components.SectionHeader

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsState()

  MainNavigationScreen(navController = navController) {
    when {
      uiState.isLoading -> {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    top = AppSpacing.contentPaddingTop,
                    bottom = AppSpacing.contentPaddingBottom,
                ),
        ) {
          item { SectionHeader("Trending Today") }
          items(3) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Box(modifier = Modifier.weight(1f)) { ShimmerMovieCard() }
              Box(modifier = Modifier.weight(1f)) { ShimmerMovieCard() }
            }
          }
          item { Spacer(modifier = Modifier.height(32.dp)) }

          item { SectionHeader("Editor's Picks") }
          items(3) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Box(modifier = Modifier.weight(1f)) { ShimmerMovieCard() }
              Box(modifier = Modifier.weight(1f)) { ShimmerMovieCard() }
            }
          }
        }
      }

      uiState.error != null -> {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text(
              text = "Error: ${uiState.error}",
              color = MaterialTheme.colorScheme.error,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(16.dp),
          )
        }
      }

      else -> {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    top = AppSpacing.contentPaddingTop,
                    bottom = AppSpacing.contentPaddingBottom,
                ),
        ) {
          if (uiState.trendingMovies.isNotEmpty()) {
            item { SectionHeader("Trending Today") }

            val trendingChunked = uiState.trendingMovies.chunked(2)
            items(trendingChunked.size) { index ->
              val rowMovies = trendingChunked[index]
              Row(
                  modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
                  horizontalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                rowMovies.forEach { movie ->
                  Box(modifier = Modifier.weight(1f)) {
                    MovieCard(
                        movie = movie,
                        onMovieClick = { clickedMovie ->
                          navController.navigate(
                              "detail/${clickedMovie.id}/${clickedMovie.mediaType.name}"
                          )
                        },
                    )
                  }
                }
                if (rowMovies.size == 1) {
                  Spacer(modifier = Modifier.weight(1f))
                }
              }
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
          }

          if (uiState.editorsPicks.isNotEmpty()) {
            item { SectionHeader("Editor's Picks") }

            val editorsChunked = uiState.editorsPicks.chunked(2)
            items(editorsChunked.size) { index ->
              val rowMovies = editorsChunked[index]
              Row(
                  modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
                  horizontalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                rowMovies.forEach { movie ->
                  Box(modifier = Modifier.weight(1f)) {
                    MovieCard(
                        movie = movie,
                        onMovieClick = { clickedMovie ->
                          navController.navigate(
                              "detail/${clickedMovie.id}/${clickedMovie.mediaType.name}"
                          )
                        },
                    )
                  }
                }
                if (rowMovies.size == 1) {
                  Spacer(modifier = Modifier.weight(1f))
                }
              }
            }
          }
        }
      }
    }
  }
}
