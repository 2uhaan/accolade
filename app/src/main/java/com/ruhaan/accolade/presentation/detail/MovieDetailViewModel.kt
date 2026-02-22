package com.ruhaan.accolade.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruhaan.accolade.domain.model.CastMember
import com.ruhaan.accolade.domain.model.CrewMember
import com.ruhaan.accolade.domain.model.MediaType
import com.ruhaan.accolade.domain.model.MovieDetail
import com.ruhaan.accolade.domain.model.Review
import com.ruhaan.accolade.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DetailUiState {
  object Loading : DetailUiState()

  data class Success(val detail: MovieDetail) : DetailUiState()

  data class Error(val message: String) : DetailUiState()
}

sealed class CastCrewUiState {
  object Loading : CastCrewUiState()

  data class CastSuccess(val cast: List<CastMember>) : CastCrewUiState()

  data class CrewSuccess(val crew: List<CrewMember>) : CastCrewUiState()

  data class Error(val message: String) : CastCrewUiState()
}

sealed class ReviewsUiState {
  object Loading : ReviewsUiState()

  data class Success(val reviews: List<Review>) : ReviewsUiState()

  object Empty : ReviewsUiState()

  data class Error(val message: String) : ReviewsUiState()
}

@HiltViewModel
class MovieDetailViewModel @Inject constructor(private val repository: MovieRepository) :
    ViewModel() {

  private val _detailState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
  val detailState: StateFlow<DetailUiState> = _detailState.asStateFlow()

  private val _castCrewState = MutableStateFlow<CastCrewUiState>(CastCrewUiState.Loading)
  val castCrewState: StateFlow<CastCrewUiState> = _castCrewState.asStateFlow()

  fun loadDetail(id: Int, mediaType: MediaType) {
    viewModelScope.launch {
      _detailState.value = DetailUiState.Loading
      try {
        val detail = repository.getMovieDetail(id, mediaType)
        _detailState.value = DetailUiState.Success(detail)
      } catch (e: Exception) {
        _detailState.value = DetailUiState.Error(e.message ?: "Failed to load details")
      }
    }
  }

  fun loadCast(id: Int, mediaType: MediaType) {
    viewModelScope.launch {
      _castCrewState.value = CastCrewUiState.Loading
      try {
        val cast = repository.getCast(id, mediaType)
        _castCrewState.value = CastCrewUiState.CastSuccess(cast)
      } catch (e: Exception) {
        _castCrewState.value = CastCrewUiState.Error(e.message ?: "Failed to load cast")
      }
    }
  }

  fun loadCrew(id: Int, mediaType: MediaType) {
    viewModelScope.launch {
      _castCrewState.value = CastCrewUiState.Loading
      try {
        val crew = repository.getCrew(id, mediaType)
        _castCrewState.value = CastCrewUiState.CrewSuccess(crew)
      } catch (e: Exception) {
        _castCrewState.value = CastCrewUiState.Error(e.message ?: "Failed to load crew")
      }
    }
  }

  private val _reviewsState = MutableStateFlow<ReviewsUiState>(ReviewsUiState.Loading)
  val reviewsState: StateFlow<ReviewsUiState> = _reviewsState.asStateFlow()

  fun loadReviews(id: Int, mediaType: MediaType) {
    viewModelScope.launch {
      _reviewsState.value = ReviewsUiState.Loading
      try {
        val reviews = repository.getReviews(id, mediaType)
        _reviewsState.value =
            if (reviews.isEmpty()) {
              ReviewsUiState.Empty
            } else {
              ReviewsUiState.Success(reviews)
            }
      } catch (e: Exception) {
        _reviewsState.value = ReviewsUiState.Error(e.message ?: "Failed to load reviews")
      }
    }
  }
}
