package com.ruhaan.accolade.presentation.schedule

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruhaan.accolade.domain.model.Movie as DomainMovie
import com.ruhaan.accolade.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ScheduleViewModel @Inject constructor(private val repository: MovieRepository) : ViewModel() {

  private val _uiState = MutableStateFlow(ScheduleUiState(isLoading = true))
  val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

  private var allMovies = mutableListOf<DomainMovie>()
  private var allTvShows = mutableListOf<DomainMovie>()
  private var currentMoviePage = 1
  private var currentTvShowPage = 1

  init {
    loadInitialContent()
  }

  // Public functions for UI
  fun refreshContent() {
    currentMoviePage = 1
    currentTvShowPage = 1
    allMovies.clear()
    allTvShows.clear()
    loadInitialContent()
  }

  fun loadMoreContent() {
    if (_uiState.value.isLoadingMore || !_uiState.value.hasMorePages) return

    currentMoviePage++
    currentTvShowPage++
    loadContent(isLoadMore = true)
  }

  fun updateFilter(filter: ContentFilter) {
    _uiState.value = _uiState.value.copy(selectedFilter = filter)
    applyFilterAndGroup()
  }

  // Private loading functions
  private fun loadInitialContent() {
    loadContent(isLoadMore = false)
  }

  private fun loadContent(isLoadMore: Boolean) {
    viewModelScope.launch {
      try {
        if (!isLoadMore) {
          _uiState.value = _uiState.value.copy(isLoading = true, isRefreshing = false, error = null)
        } else {
          _uiState.value = _uiState.value.copy(isLoadingMore = true)
        }

        // Fetch both movies and TV shows in parallel
        val newMovies = repository.getUpcomingMovies(currentMoviePage)
        val newTvShows = repository.getUpcomingTvShows(currentTvShowPage)

        // Debug logs
        Log.d(
            "SCHEDULE_DEBUG",
            "Fetched ${newMovies.size} movies from page $currentMoviePage",
        )
        Log.d(
            "SCHEDULE_DEBUG",
            "Fetched ${newTvShows.size} TV shows from page $currentTvShowPage",
        )

        // Update lists
        if (isLoadMore) {
          allMovies.addAll(newMovies)
          allTvShows.addAll(newTvShows)
        } else {
          allMovies.clear()
          allMovies.addAll(newMovies)
          allTvShows.clear()
          allTvShows.addAll(newTvShows)
        }

        // Check if there are more pages (free Api typically returns 20 items per page)
        val hasMoreMovies = newMovies.size >= 20
        val hasMoreTvShows = newTvShows.size >= 20
        val hasMore = hasMoreMovies || hasMoreTvShows

        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                isRefreshing = false,
                isLoadingMore = false,
                hasMorePages = hasMore,
            )

        applyFilterAndGroup()
      } catch (e: Exception) {
        Log.e("SCHEDULE_ERROR", "Failed to load content", e)
        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                isRefreshing = false,
                isLoadingMore = false,
                error = e.message ?: "Unknown error",
            )
      }
    }
  }

  private fun applyFilterAndGroup() {
    val currentFilter = _uiState.value.selectedFilter
    Log.d(
        "FILTER_DEBUG",
        "Current filter: $currentFilter, Movies: ${allMovies.size}, TV: ${allTvShows.size}",
    )

    // Combine and filter based on selected filter
    val filteredContent =
        when (currentFilter) {
          ContentFilter.MOVIES -> {
            Log.d("FILTER_DEBUG", "Using MOVIES only")
            allMovies
          }
          ContentFilter.TV_SHOWS -> {
            Log.d("FILTER_DEBUG", "Using TV_SHOWS only")
            allTvShows
          }
          ContentFilter.BOTH -> {
            Log.d("FILTER_DEBUG", "Using BOTH")
            (allMovies + allTvShows)
          }
        }

    Log.d("FILTER_DEBUG", "Filtered content size: ${filteredContent.size}")
    val groupedMovies = groupContentByDate(filteredContent)

    _uiState.value = _uiState.value.copy(upcomingMovies = groupedMovies)
  }

  private fun groupContentByDate(content: List<DomainMovie>): List<DateGroupedMovies> {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)

    // Get today's date at start of day
    val todayCalendar =
        Calendar.getInstance().apply {
          set(Calendar.HOUR_OF_DAY, 0)
          set(Calendar.MINUTE, 0)
          set(Calendar.SECOND, 0)
          set(Calendar.MILLISECOND, 0)
        }
    val todayDate = todayCalendar.time

    Log.d("DATE_FILTER", "Today is: ${displayFormat.format(todayDate)}")

    return content
        .mapNotNull { movie ->
          // Parse and validate dates
          if (
              movie.releaseDate.isBlank() ||
                  !movie.releaseDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))
          ) {
            return@mapNotNull null
          }

          try {
            val releaseDate = inputFormat.parse(movie.releaseDate) ?: return@mapNotNull null

            // FILTER: Only include dates >= today
            if (releaseDate.before(todayDate)) {
              Log.d("DATE_FILTER", "❌ Filtered: ${movie.title} (${movie.releaseDate})")
              return@mapNotNull null
            }

            // Return movie with parsed date for sorting
            Pair(releaseDate, movie)
          } catch (e: Exception) {
            Log.e("DATE_FILTER", "Parse error: ${movie.releaseDate}", e)
            null
          }
        }
        .sortedBy { it.first } // Sort by actual Date object (chronological)
        .groupBy { (date, _) -> displayFormat.format(date) } // Group by formatted string
        .map { (dateString, pairs) ->
          DateGroupedMovies(
              dateFormatted = dateString,
              movies = pairs.map { it.second }, // Extract movies from pairs
          )
        }
  }
}
