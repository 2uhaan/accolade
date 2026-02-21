package com.ruhaan.accolade.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TrendingItemDto(
    val id: Int,
    val title: String?, // movies
    val name: String?, // TV shows
    @SerializedName("media_type") val mediaType: String, // "movie" or "tv"
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
)

data class TrendingResponse(
    val page: Int,
    val results: List<TrendingItemDto>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int,
)
