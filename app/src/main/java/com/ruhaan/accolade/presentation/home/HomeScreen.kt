package com.ruhaan.accolade.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ruhaan.accolade.presentation.home.components.ModernTopBar
import com.ruhaan.accolade.presentation.home.components.MovieCard
import com.ruhaan.accolade.presentation.home.components.SectionHeader

@Composable
fun HomeScreen(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {
  val uiState by viewModel.uiState.collectAsState()

  Column(modifier = Modifier.fillMaxSize()) {
    ModernTopBar(onSearchClick = { println("Search clicked") })

    when {
      uiState.isLoading -> {
        // Loading state
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator()
        }
      }

      uiState.error != null -> {
        // Error state
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
        // Success state - show content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp),
        ) {
          item { Spacer(modifier = Modifier.height(20.dp)) }

          // TRENDING NOW SECTION
          if (uiState.trendingMovies.isNotEmpty()) {
            item { SectionHeader("Trending Now") }

            items(uiState.trendingMovies.chunked(2)) { rowMovies ->
              Row(
                  modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
                  horizontalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                rowMovies.forEach { movie ->
                  Box(modifier = Modifier.weight(1f)) {
                    MovieCard(
                        movie = movie,
                        onMovieClick = { println("Clicked: ${it.title} (ID: ${it.id})") },
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

          // THEATRES SECTION
          if (uiState.theatreMovies.isNotEmpty()) {
            item { SectionHeader("Theatres This Week") }

            items(uiState.theatreMovies.chunked(2)) { rowMovies ->
              Row(
                  modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
                  horizontalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                rowMovies.forEach { movie ->
                  Box(modifier = Modifier.weight(1f)) {
                    MovieCard(
                        movie = movie,
                        onMovieClick = { println("Clicked: ${it.title} (ID: ${it.id})") },
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

          // STREAMING SECTION
          if (uiState.streamingMovies.isNotEmpty()) {
            item { SectionHeader("Streaming Now") }

            items(uiState.streamingMovies.chunked(2)) { rowMovies ->
              Row(
                  modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
                  horizontalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                rowMovies.forEach { movie ->
                  Box(modifier = Modifier.weight(1f)) {
                    MovieCard(
                        movie = movie,
                        onMovieClick = { println("Clicked: ${it.title} (ID: ${it.id})") },
                    )
                  }
                }
                if (rowMovies.size == 1) {
                  Spacer(modifier = Modifier.weight(1f))
                }
              }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
          }
        }
      }
    }
  }
}

