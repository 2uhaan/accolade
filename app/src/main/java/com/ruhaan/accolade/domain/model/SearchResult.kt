package com.ruhaan.accolade.domain.model

data class SearchResult(
    val id: Int,
    val title: String,
    val year: String,
    val mediaType: MediaType,
    val popularity: Double = 0.0, // ADD THIS
)
