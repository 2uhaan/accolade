package com.ruhaan.accolade.presentation.schedule

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruhaan.accolade.domain.model.Movie
import com.ruhaan.accolade.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.collections.mapNotNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ScheduleViewModel @Inject constructor(private val repository: MovieRepository) : ViewModel() {

  private val _uiState = MutableStateFlow(ScheduleUiState(isLoading = true))
  val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

  private val allMovies = MutableStateFlow<List<Movie>>(emptyList())
  private val allTvShows = MutableStateFlow<List<Movie>>(emptyList())
  private var currentMoviePage = 1
  private var currentTvShowPage = 1

  // Independent exhaustion tracking
  private var moviesExhausted = false
  private var tvShowsExhausted = false

  // Max pages limit to respect API limits
  private val MAX_PAGES = 3

  init {
    loadInitialContent()
  }

  fun refreshContent() {
    currentMoviePage = 1
    currentTvShowPage = 1
    moviesExhausted = false
    tvShowsExhausted = false
    allMovies.value = emptyList()
    allTvShows.value = emptyList()
    _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
    loadInitialContent()
  }

  fun loadMoreContent() {
    if (
        _uiState.value.isLoading ||
            _uiState.value.isLoadingMore ||
            (!hasMoreMovies() && !hasMoreTvShows())
    )
        return

    // Increment only pages that aren't exhausted
    val nextMoviePage = if (hasMoreMovies()) currentMoviePage + 1 else currentMoviePage
    val nextTvShowPage = if (hasMoreTvShows()) currentTvShowPage + 1 else currentTvShowPage

    if (nextMoviePage <= MAX_PAGES) currentMoviePage = nextMoviePage
    if (nextTvShowPage <= MAX_PAGES) currentTvShowPage = nextTvShowPage

    loadContent(isLoadMore = true)
  }

  fun updateFilter(filter: ContentFilter) {
    _uiState.value = _uiState.value.copy(selectedFilter = filter)
    applyFilterAndGroup()
  }

  private fun hasMoreMovies(): Boolean {
    return !moviesExhausted && currentMoviePage <= MAX_PAGES
  }

  private fun hasMoreTvShows(): Boolean {
    return !tvShowsExhausted && currentTvShowPage <= MAX_PAGES
  }

  private fun loadInitialContent() {
    loadContent(isLoadMore = false)
  }

  private fun loadContent(isLoadMore: Boolean) {
    viewModelScope.launch {
      if (!isLoadMore) {
        val isRefresh = _uiState.value.isRefreshing
        _uiState.value =
            _uiState.value.copy(isLoading = !isRefresh, isRefreshing = isRefresh, error = null)
      } else {
        _uiState.value = _uiState.value.copy(isLoadingMore = true)
      }

      var moviesError: String? = null
      var tvShowsError: String? = null
      var newMovies = emptyList<Movie>()
      var newTvShows = emptyList<Movie>()

      // Fetch movies with independent error handling
      if (hasMoreMovies() || !isLoadMore) {
        try {
          newMovies = repository.getUpcomingMovies(currentMoviePage)
          Log.d(
              "SCHEDULE_DEBUG",
              "Fetched ${newMovies.size} movies from page $currentMoviePage",
          )

          // Check if movies are exhausted
          if (newMovies.size < 20) {
            moviesExhausted = true
            Log.d("SCHEDULE_DEBUG", "Movies exhausted at page $currentMoviePage")
          }
        } catch (e: Exception) {
          Log.e("SCHEDULE_ERROR", "Failed to load movies", e)
          moviesError = e.message ?: "Failed to load movies"
          moviesExhausted = true // Stop trying if API fails
        }
      }

      // Fetch TV shows with independent error handling
      if (hasMoreTvShows() || !isLoadMore) {
        try {
          newTvShows = repository.getUpcomingTvShows(currentTvShowPage)
          Log.d(
              "SCHEDULE_DEBUG",
              "Fetched ${newTvShows.size} TV shows from page $currentTvShowPage",
          )

          // Check if TV shows are exhausted
          if (newTvShows.size < 20) {
            tvShowsExhausted = true
            Log.d("SCHEDULE_DEBUG", "TV shows exhausted at page $currentTvShowPage")
          }
        } catch (e: Exception) {
          Log.e("SCHEDULE_ERROR", "Failed to load TV shows", e)
          tvShowsError = e.message ?: "Failed to load TV shows"
          tvShowsExhausted = true // Stop trying if API fails
        }
      }

      // Update lists
      if (isLoadMore) {
        allMovies.value = allMovies.value + newMovies
        allTvShows.value = allTvShows.value + newTvShows
      } else {
        allMovies.value = newMovies
        allTvShows.value = newTvShows
      }

      // Combine errors if both failed
      val combinedError =
          when {
            moviesError != null && tvShowsError != null -> "Failed to load content"
            moviesError != null -> moviesError
            tvShowsError != null -> tvShowsError
            else -> null
          }

      // Only show error if BOTH failed on initial load, or if we got no data at all
      val shouldShowError =
          if (!isLoadMore) {
            moviesError != null && tvShowsError != null
          } else {
            newMovies.isEmpty() &&
                newTvShows.isEmpty() &&
                (moviesError != null || tvShowsError != null)
          }

      _uiState.value =
          _uiState.value.copy(
              isLoading = false,
              isRefreshing = false,
              isLoadingMore = false,
              hasMorePages = hasMoreMovies() || hasMoreTvShows(),
              error = if (shouldShowError) combinedError else null,
              moviesLoaded = allMovies.value.size,
              tvShowsLoaded = allTvShows.value.size,
          )

      applyFilterAndGroup()
    }
  }

  private fun applyFilterAndGroup() {
    val currentFilter = _uiState.value.selectedFilter
    Log.d(
        "FILTER_DEBUG",
        "Current filter: $currentFilter, Movies: ${allMovies.value.size}, TV: ${allTvShows.value.size}",
    )

    val filteredContent =
        when (currentFilter) {
          ContentFilter.MOVIES -> {
            Log.d("FILTER_DEBUG", "Using MOVIES only")
            allMovies.value
          }
          ContentFilter.TV_SHOWS -> {
            Log.d("FILTER_DEBUG", "Using TV_SHOWS only")
            allTvShows.value
          }
          ContentFilter.BOTH -> {
            Log.d("FILTER_DEBUG", "Using BOTH")
            (allMovies.value + allTvShows.value)
          }
        }

    Log.d("FILTER_DEBUG", "Filtered content size: ${filteredContent.size}")
    val groupedMovies = groupContentByDate(filteredContent)

    _uiState.value = _uiState.value.copy(upcomingMovies = groupedMovies)
  }

  private fun groupContentByDate(content: List<Movie>): List<DateGroupedMovies> {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)

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
          if (
              movie.releaseDate.isBlank() ||
                  !movie.releaseDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))
          ) {
            return@mapNotNull null
          }

          try {
            val releaseDate = inputFormat.parse(movie.releaseDate) ?: return@mapNotNull null

            if (releaseDate.before(todayDate)) {
              Log.d("DATE_FILTER", "❌ Filtered: ${movie.title} (${movie.releaseDate})")
              return@mapNotNull null
            }

            Pair(releaseDate, movie)
          } catch (e: Exception) {
            Log.e("DATE_FILTER", "Parse error: ${movie.releaseDate}", e)
            null
          }
        }
        .sortedBy { it.first }
        .groupBy { (date, _) -> displayFormat.format(date) }
        .map { (dateString, pairs) ->
          DateGroupedMovies(
              dateFormatted = dateString,
              movies = pairs.map { it.second },
          )
        }
  }
}

// @HiltViewModel
// class ScheduleViewModel @Inject constructor(private val repository: MovieRepository) :
// ViewModel() {
//  private val _uiState = MutableStateFlow(ScheduleUiState(isLoading = true))
//  val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()
//
//  private var allMovies = mutableListOf<DomainMovie>()
//  private var allTvShows = mutableListOf<DomainMovie>()
//  private var currentMoviePage = 1
//  private var currentTvShowPage = 1
//
//  init {
//    loadInitialContent()
//  }
//
//  fun refreshContent() {
//    currentMoviePage = 1
//    currentTvShowPage = 1
//    allMovies.clear()
//    allTvShows.clear()
//    loadInitialContent()
//  }
//
//  fun loadMoreContent() {
//    if (_uiState.value.isLoadingMore || !_uiState.value.hasMorePages) return
//
//    currentMoviePage++
//    currentTvShowPage++
//    loadContent(isLoadMore = true)
//  }
//
//  fun updateFilter(filter: ContentFilter) {
//    _uiState.value = _uiState.value.copy(selectedFilter = filter)
//    applyFilterAndGroup()
//  }
//
//  private fun loadInitialContent() {
//    loadContent(isLoadMore = false)
//  }
//
//  private fun loadContent(isLoadMore: Boolean) {
//    viewModelScope.launch {
//      try {
//        if (!isLoadMore) {
//          _uiState.value = _uiState.value.copy(isLoading = true, isRefreshing = false, error =
// null)
//        } else {
//          _uiState.value = _uiState.value.copy(isLoadingMore = true)
//        }
//
//        val newMovies = repository.getUpcomingMovies(currentMoviePage)
//        val newTvShows = repository.getUpcomingTvShows(currentTvShowPage)
//
//        Log.d(
//            "SCHEDULE_DEBUG",
//            "Fetched ${newMovies.size} movies from page $currentMoviePage",
//        )
//        Log.d(
//            "SCHEDULE_DEBUG",
//            "Fetched ${newTvShows.size} TV shows from page $currentTvShowPage",
//        )
//
//        if (isLoadMore) {
//          allMovies.addAll(newMovies)
//          allTvShows.addAll(newTvShows)
//        } else {
//          allMovies.clear()
//          allMovies.addAll(newMovies)
//          allTvShows.clear()
//          allTvShows.addAll(newTvShows)
//        }
//
//        val hasMoreMovies = newMovies.size >= 20
//        val hasMoreTvShows = newTvShows.size >= 20
//        val hasMore = hasMoreMovies || hasMoreTvShows
//
//        _uiState.value =
//            _uiState.value.copy(
//                isLoading = false,
//                isRefreshing = false,
//                isLoadingMore = false,
//                hasMorePages = hasMore,
//            )
//
//        applyFilterAndGroup()
//      } catch (e: Exception) {
//        Log.e("SCHEDULE_ERROR", "Failed to load content", e)
//        _uiState.value =
//            _uiState.value.copy(
//                isLoading = false,
//                isRefreshing = false,
//                isLoadingMore = false,
//                error = e.message ?: "Unknown error",
//            )
//      }
//    }
//  }
//
//  private fun applyFilterAndGroup() {
//    val currentFilter = _uiState.value.selectedFilter
//    Log.d(
//        "FILTER_DEBUG",
//        "Current filter: $currentFilter, Movies: ${allMovies.size}, TV: ${allTvShows.size}",
//    )
//
//    val filteredContent =
//        when (currentFilter) {
//          ContentFilter.MOVIES -> {
//            Log.d("FILTER_DEBUG", "Using MOVIES only")
//            allMovies
//          }
//          ContentFilter.TV_SHOWS -> {
//            Log.d("FILTER_DEBUG", "Using TV_SHOWS only")
//            allTvShows
//          }
//          ContentFilter.BOTH -> {
//            Log.d("FILTER_DEBUG", "Using BOTH")
//            (allMovies + allTvShows)
//          }
//        }
//
//    Log.d("FILTER_DEBUG", "Filtered content size: ${filteredContent.size}")
//    val groupedMovies = groupContentByDate(filteredContent)
//
//    _uiState.value = _uiState.value.copy(upcomingMovies = groupedMovies)
//  }
//
//  private fun groupContentByDate(content: List<DomainMovie>): List<DateGroupedMovies> {
//    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
//    val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
//
//    val todayCalendar =
//        Calendar.getInstance().apply {
//          set(Calendar.HOUR_OF_DAY, 0)
//          set(Calendar.MINUTE, 0)
//          set(Calendar.SECOND, 0)
//          set(Calendar.MILLISECOND, 0)
//        }
//    val todayDate = todayCalendar.time
//
//    Log.d("DATE_FILTER", "Today is: ${displayFormat.format(todayDate)}")
//
//    return content
//        .mapNotNull { movie ->
//          if (
//              movie.releaseDate.isBlank() ||
//                  !movie.releaseDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))
//          ) {
//            return@mapNotNull null
//          }
//
//          try {
//            val releaseDate = inputFormat.parse(movie.releaseDate) ?: return@mapNotNull null
//
//            if (releaseDate.before(todayDate)) {
//              Log.d("DATE_FILTER", "❌ Filtered: ${movie.title} (${movie.releaseDate})")
//              return@mapNotNull null
//            }
//
//            Pair(releaseDate, movie)
//          } catch (e: Exception) {
//            Log.e("DATE_FILTER", "Parse error: ${movie.releaseDate}", e)
//            null
//          }
//        }
//        .sortedBy { it.first }
//        .groupBy { (date, _) -> displayFormat.format(date) }
//        .map { (dateString, pairs) ->
//          DateGroupedMovies(
//              dateFormatted = dateString,
//              movies = pairs.map { it.second },
//          )
//        }
//  }
// }
