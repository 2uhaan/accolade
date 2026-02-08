package com.ruhaan.accolade.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ruhaan.accolade.domain.model.MediaType
import com.ruhaan.accolade.domain.model.SearchResult
import com.ruhaan.accolade.presentation.home.components.ModernTopBar

@Composable
fun SearchScreen(
    navController: NavController,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchClose: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsState()

  Column(modifier = Modifier.fillMaxSize()) {
    // Top bar with search (always expanded in this screen)
    ModernTopBar(
        isSearchExpanded = true,
        searchQuery = searchQuery,
        onSearchQueryChange = { query ->
          onSearchQueryChange(query)
          viewModel.onSearchQueryChange(query)
        },
        onSearchClick = {}, // Not used when expanded
        onSearchClose = onSearchClose,
    )

    // Search results
    Box(modifier = Modifier.fillMaxSize()) {
      when (val state = uiState) {
        is SearchUiState.Initial -> {
          InitialView(modifier = Modifier.align(Alignment.Center))
        }
        is SearchUiState.Loading -> {
          CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        is SearchUiState.Success -> {
          if (state.results.isEmpty()) {
            EmptyResultsView(query = searchQuery, modifier = Modifier.align(Alignment.Center))
          } else {
            SearchResultsList(
                results = state.results,
                onResultClick = { result ->
                  navController.navigate("detail/${result.id}/${result.mediaType.name}")
                  onSearchClose() // Close search after navigating
                },
            )
          }
        }
        is SearchUiState.Error -> {
          ErrorView(message = state.message, modifier = Modifier.align(Alignment.Center))
        }
      }
    }
  }
}

@Composable
private fun SearchResultsList(results: List<SearchResult>, onResultClick: (SearchResult) -> Unit) {
  LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    items(results) { result ->
      SearchResultItem(result = result, onClick = { onResultClick(result) })
    }
  }
}

@Composable
private fun SearchResultItem(result: SearchResult, onClick: () -> Unit) {
  Card(
      modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
  ) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      // Title
      Text(
          text = result.title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
      )

      // Year and Type
      Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
            text = result.year,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        )

        Text(text = "•", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))

        Text(
            text = if (result.mediaType == MediaType.MOVIE) "Movie" else "TV Show",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
        )
      }
    }
  }
}

@Composable
private fun InitialView(modifier: Modifier = Modifier) {
  Column(
      modifier = modifier.padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Text(
        text = "Search",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
    )
    Text(
        text = "Search for movies and TV shows",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
    )
  }
}

@Composable
private fun EmptyResultsView(query: String, modifier: Modifier = Modifier) {
  Column(
      modifier = modifier.padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Text(
        text = "No Results",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
    )
    Text(
        text = "No results found for '$query'",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
    )
  }
}

@Composable
private fun ErrorView(message: String, modifier: Modifier = Modifier) {
  Column(
      modifier = modifier.padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp),
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
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
    )
  }
}
