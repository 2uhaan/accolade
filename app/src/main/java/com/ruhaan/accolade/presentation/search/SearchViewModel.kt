package com.ruhaan.accolade.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruhaan.accolade.domain.model.SearchResult
import com.ruhaan.accolade.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

sealed class SearchUiState {
  object Initial : SearchUiState()

  object Loading : SearchUiState()

  data class Success(val results: List<SearchResult>) : SearchUiState()

  data class Error(val message: String) : SearchUiState()
}

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: MovieRepository) : ViewModel() {

  private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Initial)
  val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  init {
    // Set up debounced search
    viewModelScope.launch {
      _searchQuery
          .debounce(500) // Wait 500ms after user stops typing
          .distinctUntilChanged() // Only trigger if query actually changed
          .filter { it.isNotBlank() } // Only search if query is not empty
          .collect { query -> performSearch(query) }
    }
  }

  fun onSearchQueryChange(query: String) {
    _searchQuery.value = query

    if (query.isBlank()) {
      _uiState.value = SearchUiState.Initial
    } else {
      _uiState.value = SearchUiState.Loading
    }
  }

  private fun performSearch(query: String) {
    viewModelScope.launch {
      try {
        val results = repository.searchMulti(query)
        _uiState.value =
            if (results.isEmpty()) {
              SearchUiState.Success(emptyList())
            } else {
              SearchUiState.Success(results)
            }
      } catch (e: Exception) {
        _uiState.value = SearchUiState.Error(e.message ?: "Failed to search")
      }
    }
  }

  fun clearSearch() {
    _searchQuery.value = ""
    _uiState.value = SearchUiState.Initial
  }
}
