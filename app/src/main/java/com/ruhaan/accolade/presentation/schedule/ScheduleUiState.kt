package com.ruhaan.accolade.presentation.schedule

import com.ruhaan.accolade.domain.model.Movie

data class ScheduleUiState(
    val selectedTab: ScheduleTab = ScheduleTab.THIS_WEEK,
    val selectedFilter: ContentFilter = ContentFilter.BOTH,
    val previous: TabState = TabState(isLoading = false), // lazy — don't show spinner yet
    val thisWeek: TabState = TabState(isLoading = true), // loads on init — show spinner immediately
    val upcoming: TabState = TabState(isLoading = false), // lazy — don't show spinner yet
)

data class TabState(
    val isLoading: Boolean = false,
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
