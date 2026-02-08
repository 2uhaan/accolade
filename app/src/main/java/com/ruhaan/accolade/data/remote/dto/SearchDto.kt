package com.ruhaan.accolade.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    val page: Int,
    val results: List<SearchResultDto>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int,
)

data class SearchResultDto(
    val id: Int,
    @SerializedName("media_type") val mediaType: String, // "movie" or "tv"
    val title: String? = null, // For movies
    val name: String? = null, // For TV shows
    @SerializedName("release_date") val releaseDate: String? = null, // For movies
    @SerializedName("first_air_date") val firstAirDate: String? = null, // For TV shows
    val popularity: Double = 0.0, // ADD THIS - TMDB provides popularity score
)
