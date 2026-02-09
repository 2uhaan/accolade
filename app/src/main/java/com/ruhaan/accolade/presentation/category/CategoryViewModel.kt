package com.ruhaan.accolade.presentation.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruhaan.accolade.domain.model.Movie
import com.ruhaan.accolade.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CategoryUiState {
    object Loading : CategoryUiState()
    data class Success(
        val movies: List<Movie>,
        val isLoadingMore: Boolean = false,
        val hasMorePages: Boolean = true
    ) : CategoryUiState()
    data class Error(val message: String) : CategoryUiState()
}

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var genreId: Int = 0
    private val allMovies = mutableListOf<Movie>()

    fun loadCategory(genreId: Int, isInitial: Boolean = true) {
        if (isInitial) {
            this.genreId = genreId
            currentPage = 1
            allMovies.clear()
            _uiState.value = CategoryUiState.Loading
        } else {
            // Loading more pages
            val currentState = _uiState.value
            if (currentState is CategoryUiState.Success && currentState.isLoadingMore) {
                return // Already loading
            }
            if (currentState is CategoryUiState.Success && !currentState.hasMorePages) {
                return // No more pages
            }

            _uiState.value = CategoryUiState.Success(
                movies = allMovies,
                isLoadingMore = true,
                hasMorePages = true
            )
            currentPage++
        }

        viewModelScope.launch {
            try {
                // Fetch both movies and TV shows for this genre
                val movies = repository.getMoviesByGenre(genreId, currentPage)
                val tvShows = repository.getTvShowsByGenre(genreId, currentPage)

                // Combine and sort by popularity (already sorted by API)
                val combined = (movies + tvShows).distinctBy { it.id }

                allMovies.addAll(combined)

                _uiState.value = CategoryUiState.Success(
                    movies = allMovies,
                    isLoadingMore = false,
                    hasMorePages = combined.isNotEmpty() // If we got results, there might be more
                )
            } catch (e: Exception) {
                if (isInitial) {
                    _uiState.value = CategoryUiState.Error(e.message ?: "Failed to load category")
                } else {
                    // If loading more failed, revert to previous state
                    _uiState.value = CategoryUiState.Success(
                        movies = allMovies,
                        isLoadingMore = false,
                        hasMorePages = false
                    )
                }
            }
        }
    }

    fun loadMore() {
        loadCategory(genreId, isInitial = false)
    }
}