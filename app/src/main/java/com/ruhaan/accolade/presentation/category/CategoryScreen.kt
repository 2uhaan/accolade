package com.ruhaan.accolade.presentation.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ruhaan.accolade.domain.model.Movie
import com.ruhaan.accolade.presentation.common.MovieCard
import com.ruhaan.accolade.presentation.common.ShimmerMovieCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navController: NavController,
    genreId: Int,
    genreName: String,
    viewModel: CategoryViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsState()
  val gridState = rememberLazyGridState()

  LaunchedEffect(genreId) { viewModel.loadCategory(genreId, isInitial = true) }

  // Infinite scroll logic
  LaunchedEffect(gridState) {
    snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
        .collect { lastVisibleIndex ->
          if (lastVisibleIndex != null && uiState is CategoryUiState.Success) {
            val state = uiState as CategoryUiState.Success
            val totalItems = state.movies.size

            if (lastVisibleIndex >= totalItems - 5 && !state.isLoadingMore && state.hasMorePages) {
              viewModel.loadMore()
            }
          }
        }
  }

  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(text = genreName, fontWeight = FontWeight.Bold)
              }
            },
            navigationIcon = {
              IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
              }
            },
            actions = { Spacer(modifier = Modifier.width(48.dp)) },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
        )
      },
      containerColor = MaterialTheme.colorScheme.background,
  ) { paddingValues ->
    Box(modifier = Modifier.fillMaxSize().padding(top = paddingValues.calculateTopPadding())) {
      when (val state = uiState) {
        is CategoryUiState.Loading -> {
          ShimmerLoadingGrid()
        }
        is CategoryUiState.Error -> {
          ErrorView(
              message = state.message,
              onRetry = { viewModel.loadCategory(genreId, isInitial = true) },
              modifier = Modifier.align(Alignment.Center),
          )
        }
        is CategoryUiState.Success -> {
          if (state.movies.isEmpty()) {
            ShimmerLoadingGrid()
          } else {
            CategoryGrid(
                movies = state.movies,
                isLoadingMore = state.isLoadingMore,
                gridState = gridState,
                onMovieClick = { movie ->
                  navController.navigate("detail/${movie.id}/${movie.mediaType.name}")
                },
            )
          }
        }
      }
    }
  }
}

@Composable
private fun CategoryGrid(
    movies: List<Movie>,
    isLoadingMore: Boolean,
    gridState: androidx.compose.foundation.lazy.grid.LazyGridState,
    onMovieClick: (Movie) -> Unit,
) {
  LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      state = gridState,
      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.fillMaxSize(),
  ) {
    items(movies) { movie -> MovieCard(movie = movie, onMovieClick = onMovieClick) }

    if (isLoadingMore) {
      item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) { // Changed from 3
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
          CircularProgressIndicator()
        }
      }
    }
  }
}

@Composable
fun ShimmerLoadingGrid() {
  LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      modifier = Modifier.fillMaxSize(),
  ) {
    items(6) { ShimmerMovieCard() }
  }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
  Column(
      modifier = modifier.padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
        text = "Error",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.error,
    )
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
    )
    Button(onClick = onRetry) { Text("Retry") }
  }
}
