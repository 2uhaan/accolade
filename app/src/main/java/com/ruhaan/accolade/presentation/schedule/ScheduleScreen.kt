package com.ruhaan.accolade.presentation.schedule

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ruhaan.accolade.domain.model.Movie
import com.ruhaan.accolade.presentation.common.AppSpacing
import com.ruhaan.accolade.presentation.common.MainNavigationScreen
import com.ruhaan.accolade.presentation.common.MovieCard
import com.ruhaan.accolade.presentation.common.ShimmerMovieCard
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    navController: NavController,
    viewModel: ScheduleViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsState()
  val view = LocalView.current

  MainNavigationScreen(navController = navController) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

      // Tab Row
      TabRow(
          selectedTabIndex = uiState.selectedTab.ordinal,
          containerColor = Color.White,
      ) {
        listOf("Previous", "This Week", "Upcoming").forEachIndexed { index, label ->
          val tab = ScheduleTab.entries[index]
          val isSelected = uiState.selectedTab == tab

          val animatedScale by
              animateFloatAsState(
                  targetValue = if (isSelected) 1f else 0.87f,
                  animationSpec = tween(durationMillis = 250, easing = EaseInOutCubic),
                  label = "tabScale_$index",
              )

          val animatedColor by
              animateColorAsState(
                  targetValue =
                      if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF9E9E9E),
                  animationSpec = tween(durationMillis = 250),
                  label = "tabColor_$index",
              )

          Tab(
              selected = isSelected,
              onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                viewModel.selectTab(tab)
              },
              text = {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    modifier =
                        Modifier.graphicsLayer {
                          scaleX = animatedScale
                          scaleY = animatedScale
                        },
                    color = animatedColor,
                    fontWeight = FontWeight.Medium,
                )
              },
          )
        }
      }

      // Filter chips
      Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        ScheduleFilterChip("All", uiState.selectedFilter == ContentFilter.BOTH) {
          viewModel.updateFilter(ContentFilter.BOTH)
        }
        ScheduleFilterChip("Movies", uiState.selectedFilter == ContentFilter.MOVIES) {
          viewModel.updateFilter(ContentFilter.MOVIES)
        }
        ScheduleFilterChip("Shows", uiState.selectedFilter == ContentFilter.TV_SHOWS) {
          viewModel.updateFilter(ContentFilter.TV_SHOWS)
        }
      }

      // Tab content
      val currentTabState =
          when (uiState.selectedTab) {
            ScheduleTab.PREVIOUS -> uiState.previous
            ScheduleTab.THIS_WEEK -> uiState.thisWeek
            ScheduleTab.UPCOMING -> uiState.upcoming
          }

      TabContent(
          tabState = currentTabState,
          tab = uiState.selectedTab,
          onMovieClick = { movie ->
            navController.navigate("detail/${movie.id}/${movie.mediaType.name}")
          },
          onLoadMore = { viewModel.loadMoreContent() },
          onRetry = { viewModel.refreshTab(uiState.selectedTab) },
      )
    }
  }
}

@Composable
fun TabContent(
    tabState: TabState,
    tab: ScheduleTab,
    onMovieClick: (Movie) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
) {
  val listState = rememberLazyListState()

  LaunchedEffect(listState) {
    snapshotFlow {
          val total = listState.layoutInfo.totalItemsCount
          val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
          total > 0 && lastVisible >= total - 4
        }
        .distinctUntilChanged()
        .collect { nearEnd -> if (nearEnd) onLoadMore() }
  }

  LazyColumn(
      state = listState,
      modifier = Modifier.fillMaxSize(),
      contentPadding =
          PaddingValues(
              top = 8.dp,
              bottom = AppSpacing.contentPaddingBottom,
          ),
  ) {
    when {
      tabState.isLoading -> {
        items(4) { ShimmerDateSection() }
      }

      tabState.error != null -> {
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
                  tabState.error,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
              )
              Spacer(modifier = Modifier.height(16.dp))
              Button(onClick = onRetry, shape = RoundedCornerShape(12.dp)) { Text("Retry") }
            }
          }
        }
      }

      tabState.content.isEmpty() -> {
        item {
          Box(
              modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp),
              contentAlignment = Alignment.Center,
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                  text =
                      when (tab) {
                        ScheduleTab.THIS_WEEK -> "Nothing releasing this week"
                        ScheduleTab.PREVIOUS -> "Nothing in the last 30 days"
                        ScheduleTab.UPCOMING -> "No upcoming releases found"
                      },
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
              )
              Spacer(modifier = Modifier.height(8.dp))
              TextButton(onClick = onRetry) { Text("Refresh") }
            }
          }
        }
      }

      else -> {
        items(tabState.content) { dateGroup ->
          DateSection(dateGroup = dateGroup, onMovieClick = onMovieClick)
        }
        if (tabState.isLoadingMore) {
          items(2) { ShimmerDateSection() }
        }
      }
    }
  }
}

@Composable
fun ScheduleFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
  Surface(
      onClick = onClick,
      shape = RoundedCornerShape(50),
      color = if (selected) MaterialTheme.colorScheme.primary else Color(0xFFEEF0F4),
      shadowElevation = if (selected) 2.dp else 0.dp,
      modifier = Modifier.height(36.dp),
  ) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(horizontal = 20.dp),
    ) {
      Text(
          text = label,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
          color = if (selected) Color.White else Color(0xFF6B7280),
      )
    }
  }
}

@Composable
fun DateSection(dateGroup: DateGroupedMovies, onMovieClick: (Movie) -> Unit) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.Top,
    ) {
      Column(modifier = Modifier.width(36.dp).padding(top = 4.dp)) {
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

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
fun ShimmerDateSection() {
  Row(
      modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 28.dp),
      verticalAlignment = Alignment.Top,
  ) {
    Column(
        modifier = Modifier.width(56.dp),
        horizontalAlignment = Alignment.Start,
    ) {
      ShimmerBox(width = 28.dp, height = 11.dp) // Weekday
      Spacer(modifier = Modifier.height(4.dp))
      ShimmerBox(width = 42.dp, height = 42.dp) // Day number
      Spacer(modifier = Modifier.height(4.dp))
      ShimmerBox(width = 28.dp, height = 11.dp) // Month
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      repeat(2) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Box(modifier = Modifier.weight(1f)) { ShimmerMovieCard() }
          Box(modifier = Modifier.weight(1f)) { ShimmerMovieCard() }
        }
      }
    }
  }
}

@Composable
fun ShimmerBox(width: Dp, height: Dp) {
  val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
  val translateAnim by
      infiniteTransition.animateFloat(
          initialValue = 0f,
          targetValue = 1000f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(durationMillis = 1200, easing = LinearEasing),
                  repeatMode = RepeatMode.Restart,
              ),
          label = "translate",
      )
  val shimmerColors =
      listOf(
          Color(0xFFE5E7EB),
          Color(0xFFF3F4F6),
          Color(0xFFE5E7EB),
      )
  val brush =
      remember(translateAnim) {
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim - 1000f, translateAnim - 1000f),
            end = Offset(translateAnim, translateAnim),
        )
      }
  Box(
      modifier =
          Modifier.width(width).height(height).clip(RoundedCornerShape(4.dp)).background(brush)
  )
}
