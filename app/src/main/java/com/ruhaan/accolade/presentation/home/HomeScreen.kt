package com.ruhaan.accolade.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ruhaan.accolade.presentation.home.components.ModernTopBar
import com.ruhaan.accolade.presentation.home.components.MovieCard
import com.ruhaan.accolade.presentation.home.components.SectionHeader
import com.ruhaan.accolade.presentation.search.SearchScreen
import com.ruhaan.accolade.presentation.search.SearchViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel(), // ADD THIS
) {
  val uiState by viewModel.uiState.collectAsState()
  var isSearchExpanded by remember { mutableStateOf(false) }
  var searchQuery by remember { mutableStateOf("") }

  if (isSearchExpanded) {
    // Show search screen when expanded
    SearchScreen(
        navController = navController,
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        onSearchClose = {
          isSearchExpanded = false
          searchQuery = ""
          searchViewModel.clearSearch()
        },
        viewModel = searchViewModel, // PASS THE VIEWMODEL
    )
  } else {
    Column(modifier = Modifier.fillMaxSize()) {
      ModernTopBar(
          isSearchExpanded = false,
          searchQuery = "",
          onSearchQueryChange = {},
          onSearchClick = { isSearchExpanded = true },
          onSearchClose = {},
      )

      when {
        uiState.isLoading -> {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
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
              contentPadding = PaddingValues(bottom = 100.dp),
          ) {
            item { Spacer(modifier = Modifier.height(20.dp)) }

            // TRENDING NOW SECTION
            if (uiState.trendingMovies.isNotEmpty()) {
              item { SectionHeader("Trending Now") }

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

            // THEATRES SECTION
            if (uiState.theatreMovies.isNotEmpty()) {
              item { SectionHeader("Theatres This Week") }

              val theatreChunked = uiState.theatreMovies.chunked(2)
              items(theatreChunked.size) { index ->
                val rowMovies = theatreChunked[index]
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

            // STREAMING SECTION
            if (uiState.streamingMovies.isNotEmpty()) {
              item { SectionHeader("Streaming Now") }

              val streamingChunked = uiState.streamingMovies.chunked(2)
              items(streamingChunked.size) { index ->
                val rowMovies = streamingChunked[index]
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

              item { Spacer(modifier = Modifier.height(20.dp)) }
            }
          }
        }
      }
    }
  }
}
