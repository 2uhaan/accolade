package com.ruhaan.accolade.presentation.schedule

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruhaan.accolade.domain.model.Movie
import com.ruhaan.accolade.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ScheduleViewModel
@Inject
constructor(
    private val repository: MovieRepository,
) : ViewModel() {

  private val _uiState = MutableStateFlow(ScheduleUiState())
  val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

  // Raw data per tab
  private val prevMovies = mutableListOf<Movie>()
  private val prevTvShows = mutableListOf<Movie>()
  private var prevMoviePage = 1
  private var prevMovieTotalPages = 1
  private var prevTvPage = 1
  private var prevTvTotalPages = 1

  private val weekMovies = mutableListOf<Movie>()
  private val weekTvShows = mutableListOf<Movie>()
  private var weekMoviePage = 1
  private var weekMovieTotalPages = 1
  private var weekTvPage = 1
  private var weekTvTotalPages = 1

  private val upcomingMovies = mutableListOf<Movie>()
  private val upcomingTvShows = mutableListOf<Movie>()
  private var upcomingMoviePage = 1
  private var upcomingMovieTotalPages = 1
  private var upcomingTvPage = 1
  private var upcomingTvTotalPages = 1

  init {
    loadTab(ScheduleTab.THIS_WEEK)
    loadTab(ScheduleTab.PREVIOUS)
    loadTab(ScheduleTab.UPCOMING)
  }

  fun selectTab(tab: ScheduleTab) {
    _uiState.value = _uiState.value.copy(selectedTab = tab)
  }

  fun updateFilter(filter: ContentFilter) {
    _uiState.value = _uiState.value.copy(selectedFilter = filter)
    // Regroup all tabs with new filter
    updateTabContent(ScheduleTab.PREVIOUS)
    updateTabContent(ScheduleTab.THIS_WEEK)
    updateTabContent(ScheduleTab.UPCOMING)
  }

  fun loadMoreContent() {
    val tab = _uiState.value.selectedTab
    val tabState = getTabState(tab)
    if (tabState.isLoading || tabState.isLoadingMore || !tabState.hasMorePages) return
    loadTab(tab, isLoadMore = true)
  }

  fun refreshTab(tab: ScheduleTab) {
    when (tab) {
      ScheduleTab.PREVIOUS -> {
        prevMovies.clear()
        prevTvShows.clear()
        prevMoviePage = 1
        prevMovieTotalPages = 1
        prevTvPage = 1
        prevTvTotalPages = 1
      }
      ScheduleTab.THIS_WEEK -> {
        weekMovies.clear()
        weekTvShows.clear()
        weekMoviePage = 1
        weekMovieTotalPages = 1
        weekTvPage = 1
        weekTvTotalPages = 1
      }
      ScheduleTab.UPCOMING -> {
        upcomingMovies.clear()
        upcomingTvShows.clear()
        upcomingMoviePage = 1
        upcomingMovieTotalPages = 1
        upcomingTvPage = 1
        upcomingTvTotalPages = 1
      }
    }
    loadTab(tab)
  }

  private fun loadTab(tab: ScheduleTab, isLoadMore: Boolean = false) {
    viewModelScope.launch {
      val currentMoviePage = getCurrentMoviePage(tab)
      val currentTvPage = getCurrentTvPage(tab)
      val nextMoviePage = if (isLoadMore) currentMoviePage + 1 else currentMoviePage
      val nextTvPage = if (isLoadMore) currentTvPage + 1 else currentTvPage
      val canLoadMovies = nextMoviePage <= getMovieTotalPages(tab)
      val canLoadTvShows = nextTvPage <= getTvTotalPages(tab)

      Log.d(
          "SCHEDULE",
          "[$tab] loadMore=$isLoadMore movie=$nextMoviePage/${getMovieTotalPages(tab)} tv=$nextTvPage/${getTvTotalPages(tab)}",
      )

      setTabState(tab) {
        it.copy(isLoading = !isLoadMore, isLoadingMore = isLoadMore, error = null)
      }

      try {
        if (canLoadMovies) {
          val result =
              when (tab) {
                ScheduleTab.PREVIOUS -> repository.getPreviousMovies(nextMoviePage)
                ScheduleTab.THIS_WEEK -> repository.getThisWeekMovies(nextMoviePage)
                ScheduleTab.UPCOMING -> repository.getUpcomingMovies(nextMoviePage)
              }
          getMovieList(tab).addAll(result.items)
          setMoviePage(tab, result.currentPage, result.totalPages)
          Log.d(
              "SCHEDULE",
              "[$tab] movies fetched: ${result.items.size} | total=${getMovieList(tab).size}",
          )
        }

        if (canLoadTvShows) {
          val result =
              when (tab) {
                ScheduleTab.PREVIOUS -> repository.getPreviousTvShows(nextTvPage)
                ScheduleTab.THIS_WEEK -> repository.getThisWeekTvShows(nextTvPage)
                ScheduleTab.UPCOMING -> repository.getUpcomingTvShows(nextTvPage)
              }
          getTvList(tab).addAll(result.items)
          setTvPage(tab, result.currentPage, result.totalPages)
          Log.d(
              "SCHEDULE",
              "[$tab] tv fetched: ${result.items.size} | total=${getTvList(tab).size}",
          )
        }

        val hasMore =
            getCurrentMoviePage(tab) < getMovieTotalPages(tab) ||
                getCurrentTvPage(tab) < getTvTotalPages(tab)

        setTabState(tab) {
          it.copy(isLoading = false, isLoadingMore = false, hasMorePages = hasMore)
        }
        updateTabContent(tab)
      } catch (e: Exception) {
        Log.e("SCHEDULE", "[$tab] error: ${e.message}", e)
        setTabState(tab) {
          it.copy(isLoading = false, isLoadingMore = false, error = e.message ?: "Failed to load")
        }
      }
    }
  }

  private fun updateTabContent(tab: ScheduleTab) {
    val filter = _uiState.value.selectedFilter
    val movies = getMovieList(tab).toList()
    val tvShows = getTvList(tab).toList()

    val filtered =
        when (filter) {
          ContentFilter.MOVIES -> movies
          ContentFilter.TV_SHOWS -> tvShows
          ContentFilter.BOTH -> movies + tvShows
        }

    val reversed = tab == ScheduleTab.PREVIOUS
    val grouped = groupByDate(filtered, reversed)
    setTabState(tab) { it.copy(content = grouped) }
    Log.d("SCHEDULE", "[$tab] filter=$filter grouped=${grouped.size} date groups")
  }

  private fun groupByDate(content: List<Movie>, reversed: Boolean): List<DateGroupedMovies> {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)

    val grouped =
        content
            .filter { it.releaseDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) }
            .mapNotNull { movie ->
              try {
                val date = inputFormat.parse(movie.releaseDate) ?: return@mapNotNull null
                Pair(date, movie)
              } catch (_: Exception) {
                null
              }
            }
            .sortedBy { it.first }
            .groupBy { (date, _) -> displayFormat.format(date) }
            .map { (dateString, pairs) ->
              DateGroupedMovies(dateFormatted = dateString, movies = pairs.map { it.second })
            }

    return if (reversed) grouped.reversed() else grouped
  }

  // --- Helpers to access per-tab data cleanly ---

  private fun getTabState(tab: ScheduleTab) =
      when (tab) {
        ScheduleTab.PREVIOUS -> _uiState.value.previous
        ScheduleTab.THIS_WEEK -> _uiState.value.thisWeek
        ScheduleTab.UPCOMING -> _uiState.value.upcoming
      }

  private fun setTabState(tab: ScheduleTab, update: (TabState) -> TabState) {
    _uiState.value =
        when (tab) {
          ScheduleTab.PREVIOUS -> _uiState.value.copy(previous = update(_uiState.value.previous))
          ScheduleTab.THIS_WEEK -> _uiState.value.copy(thisWeek = update(_uiState.value.thisWeek))
          ScheduleTab.UPCOMING -> _uiState.value.copy(upcoming = update(_uiState.value.upcoming))
        }
  }

  private fun getMovieList(tab: ScheduleTab) =
      when (tab) {
        ScheduleTab.PREVIOUS -> prevMovies
        ScheduleTab.THIS_WEEK -> weekMovies
        ScheduleTab.UPCOMING -> upcomingMovies
      }

  private fun getTvList(tab: ScheduleTab) =
      when (tab) {
        ScheduleTab.PREVIOUS -> prevTvShows
        ScheduleTab.THIS_WEEK -> weekTvShows
        ScheduleTab.UPCOMING -> upcomingTvShows
      }

  private fun getCurrentMoviePage(tab: ScheduleTab) =
      when (tab) {
        ScheduleTab.PREVIOUS -> prevMoviePage
        ScheduleTab.THIS_WEEK -> weekMoviePage
        ScheduleTab.UPCOMING -> upcomingMoviePage
      }

  private fun getCurrentTvPage(tab: ScheduleTab) =
      when (tab) {
        ScheduleTab.PREVIOUS -> prevTvPage
        ScheduleTab.THIS_WEEK -> weekTvPage
        ScheduleTab.UPCOMING -> upcomingTvPage
      }

  private fun getMovieTotalPages(tab: ScheduleTab) =
      when (tab) {
        ScheduleTab.PREVIOUS -> prevMovieTotalPages
        ScheduleTab.THIS_WEEK -> weekMovieTotalPages
        ScheduleTab.UPCOMING -> upcomingMovieTotalPages
      }

  private fun getTvTotalPages(tab: ScheduleTab) =
      when (tab) {
        ScheduleTab.PREVIOUS -> prevTvTotalPages
        ScheduleTab.THIS_WEEK -> weekTvTotalPages
        ScheduleTab.UPCOMING -> upcomingTvTotalPages
      }

  private fun setMoviePage(tab: ScheduleTab, page: Int, total: Int) {
    when (tab) {
      ScheduleTab.PREVIOUS -> {
        prevMoviePage = page
        prevMovieTotalPages = total
      }
      ScheduleTab.THIS_WEEK -> {
        weekMoviePage = page
        weekMovieTotalPages = total
      }
      ScheduleTab.UPCOMING -> {
        upcomingMoviePage = page
        upcomingMovieTotalPages = total
      }
    }
  }

  private fun setTvPage(tab: ScheduleTab, page: Int, total: Int) {
    when (tab) {
      ScheduleTab.PREVIOUS -> {
        prevTvPage = page
        prevTvTotalPages = total
      }
      ScheduleTab.THIS_WEEK -> {
        weekTvPage = page
        weekTvTotalPages = total
      }
      ScheduleTab.UPCOMING -> {
        upcomingTvPage = page
        upcomingTvTotalPages = total
      }
    }
  }
}
