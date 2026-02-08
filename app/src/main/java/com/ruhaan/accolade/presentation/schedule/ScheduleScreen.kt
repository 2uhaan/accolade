package com.ruhaan.accolade.presentation.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ruhaan.accolade.domain.model.Movie
import com.ruhaan.accolade.presentation.home.components.FloatingBottomBar
import com.ruhaan.accolade.presentation.home.components.ModernTopBar
import com.ruhaan.accolade.presentation.home.components.MovieCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    navController: NavHostController,
    viewModel: ScheduleViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterMenu by remember { mutableStateOf(false) }

    Box(
        modifier =
            Modifier.fillMaxSize()
                .background(
                    brush =
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                    MaterialTheme.colorScheme.background,
                                )
                        )
                )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 140.dp, top = 20.dp),
        ) {
            // Page title + filter
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Schedule",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )

                    // Filter dropdown
                    Box {
                        TextButton(
                            onClick = { showFilterMenu = true },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = when (uiState.selectedFilter) {
                                    ContentFilter.MOVIES -> "Movies"
                                    ContentFilter.TV_SHOWS -> "TV Shows"
                                    ContentFilter.BOTH -> "All"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("▼", fontSize = 12.sp)
                        }

                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Movies Only") },
                                onClick = {
                                    viewModel.updateFilter(ContentFilter.MOVIES)
                                    showFilterMenu = false
                                },
                                leadingIcon = {
                                    if (uiState.selectedFilter == ContentFilter.MOVIES) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("TV Shows Only") },
                                onClick = {
                                    viewModel.updateFilter(ContentFilter.TV_SHOWS)
                                    showFilterMenu = false
                                },
                                leadingIcon = {
                                    if (uiState.selectedFilter == ContentFilter.TV_SHOWS) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("All Content") },
                                onClick = {
                                    viewModel.updateFilter(ContentFilter.BOTH)
                                    showFilterMenu = false
                                },
                                leadingIcon = {
                                    if (uiState.selectedFilter == ContentFilter.BOTH) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }

            when {
                uiState.isLoading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                uiState.error != null -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    "Network Error",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    uiState.error ?: "Unknown error",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { viewModel.refreshContent() }, shape = RoundedCornerShape(12.dp)) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }

                uiState.upcomingMovies.isEmpty() -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No upcoming releases",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = { viewModel.refreshContent() }) {
                                    Text("Refresh")
                                }
                            }
                        }
                    }
                }

                else -> {
                    // Date-grouped sections
                    items(uiState.upcomingMovies) { dateGroup ->
                        DateSection(
                            dateGroup = dateGroup,
                            onMovieClick = { movie -> println("📅 SCHEDULE Clicked: ${movie.title}") },
                        )
                    }

                    // Load More button - only show if has more pages
                    if (uiState.hasMorePages) {
                        item {
                            Button(
                                onClick = { viewModel.loadMoreContent() },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !uiState.isLoadingMore,
                            ) {
                                if (uiState.isLoadingMore) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Loading...")
                                } else {
                                    Text("Load More")
                                }
                            }
                        }
                    }
                }
            }
        }

        ModernTopBar(
            onSearchClick = { println("Search clicked") },
            modifier = Modifier.align(Alignment.TopCenter),
        )
        FloatingBottomBar(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
fun DateSection(dateGroup: DateGroupedMovies, onMovieClick: (Movie) -> Unit) {
  Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
      // LEFT - Date
      Column(modifier = Modifier.width(70.dp).padding(top = 4.dp)) {
        val dateParts = dateGroup.dateFormatted.split(" ")
        Text(
            text = dateParts[0], // Day
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = dateParts[1], // Month
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
      }

      Spacer(modifier = Modifier.width(16.dp))

      // RIGHT - Movies in 2-column grid
      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        dateGroup.movies.chunked(2).forEach { rowMovies ->
          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            rowMovies.forEach { movie ->
              Box(modifier = Modifier.weight(1f)) {
                MovieCard(movie = movie, onMovieClick = onMovieClick)
              }
            }
            if (rowMovies.size == 1) {
              Spacer(modifier = Modifier.weight(1f))
            }
          }
        }
      }
    }

    // Divider
    HorizontalDivider(
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
    )
  }
}
