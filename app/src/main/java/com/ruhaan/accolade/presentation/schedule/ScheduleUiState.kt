package com.ruhaan.accolade.presentation.schedule

import com.ruhaan.accolade.domain.model.Movie

data class ScheduleUiState(
    val selectedTab: ScheduleTab = ScheduleTab.THIS_WEEK,
    val selectedFilter: ContentFilter = ContentFilter.BOTH,
    val previous: TabState = TabState(),
    val thisWeek: TabState = TabState(),
    val upcoming: TabState = TabState(),
)

data class TabState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val hasMorePages: Boolean = false,
    val content: List<DateGroupedMovies> = emptyList(),
)

data class DateGroupedMovies(
    val dateFormatted: String,
    val movies: List<Movie>,
)

enum class ScheduleTab {
  PREVIOUS,
  THIS_WEEK,
  UPCOMING,
}

enum class ContentFilter {
  MOVIES,
  TV_SHOWS,
  BOTH,
}
