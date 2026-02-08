package com.ruhaan.accolade.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TvShowDto(
    val id: Int,
    val name: String, // TV shows use "name" instead of "title"
    @SerializedName("first_air_date") val firstAirDate: String?, // TV shows use "first_air_date"
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?
)

data class TvShowListResponse(
    val page: Int,
    val results: List<TvShowDto>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)