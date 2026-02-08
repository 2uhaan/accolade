package com.ruhaan.accolade.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieDto(
    val id: Int,
    val title: String,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
)

data class MovieListResponse(val results: List<MovieDto>)
