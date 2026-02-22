package com.ruhaan.accolade.presentation.filmography

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruhaan.accolade.domain.model.Movie
import com.ruhaan.accolade.domain.model.Person
import com.ruhaan.accolade.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PersonUiState {
    object Loading : PersonUiState()
    data class Success(val person: Person) : PersonUiState()
    data class Error(val message: String) : PersonUiState()
}

sealed class FilmographyUiState {
    object Loading : FilmographyUiState()
    data class Success(val movies: List<Movie>) : FilmographyUiState()
    data class Error(val message: String) : FilmographyUiState()
}

@HiltViewModel
class FilmographyViewModel @Inject constructor(
    private val repository: MovieRepository,
) : ViewModel() {

    private val _personState = MutableStateFlow<PersonUiState>(PersonUiState.Loading)
    val personState: StateFlow<PersonUiState> = _personState.asStateFlow()

    private val _filmographyState = MutableStateFlow<FilmographyUiState>(FilmographyUiState.Loading)
    val filmographyState: StateFlow<FilmographyUiState> = _filmographyState.asStateFlow()

    fun load(personId: Int) {
        viewModelScope.launch {
            launch {
                try {
                    val person = repository.getPersonDetail(personId)
                    _personState.value = PersonUiState.Success(person)
                } catch (e: Exception) {
                    _personState.value = PersonUiState.Error(e.message ?: "Failed to load person")
                }
            }
            launch {
                try {
                    val movies = repository.getPersonFilmography(personId)
                    _filmographyState.value = FilmographyUiState.Success(movies)
                } catch (e: Exception) {
                    _filmographyState.value = FilmographyUiState.Error(e.message ?: "Failed to load filmography")
                }
            }
        }
    }
}