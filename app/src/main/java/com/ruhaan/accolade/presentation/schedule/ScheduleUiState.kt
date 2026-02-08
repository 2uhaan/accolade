package com.ruhaan.accolade.presentation.schedule

import com.ruhaan.accolade.domain.model.Movie

data class ScheduleUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val isLoadingMore: Boolean = false,
    val hasMorePages: Boolean = true,
    val currentPage: Int = 1,
    val upcomingMovies: List<DateGroupedMovies> = emptyList(),
    val selectedFilter: ContentFilter = ContentFilter.BOTH, // NEW
)

data class DateGroupedMovies(
    val dateFormatted: String, // "12 Jan 2026"
    val movies: List<Movie>,
)

enum class ContentFilter {
    MOVIES,
    TV_SHOWS,
    BOTH
}