package com.ruhaan.accolade.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruhaan.accolade.domain.model.Movie
import com.ruhaan.accolade.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val trendingMovies: List<Movie> = emptyList(),
    val theatreMovies: List<Movie> = emptyList(),
    val streamingMovies: List<Movie> = emptyList(),
)

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: MovieRepository) : ViewModel() {

  private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
  val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

  init {
    loadMovies()
  }

  fun retry() {
    loadMovies()
  }

  private fun loadMovies() {
    viewModelScope.launch {
      try {
        _uiState.value = _uiState.value.copy(isLoading = true)

        val trending = repository.getTrendingMovies()
        val theatre = repository.getTheatreMovies()
        val streaming = repository.getStreamingMovies()

        _uiState.value =
            HomeUiState(
                isLoading = false,
                trendingMovies = trending,
                theatreMovies = theatre,
                streamingMovies = streaming,
            )
      } catch (e: Exception) {
        _uiState.value =
            _uiState.value.copy(isLoading = false, error = e.message ?: "Unknown error")
      }
    }
  }
}
