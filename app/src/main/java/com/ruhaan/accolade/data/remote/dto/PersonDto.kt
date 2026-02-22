package com.ruhaan.accolade.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PersonDetailDto(
    val id: Int,
    val name: String,
    val biography: String?,
    @SerializedName("profile_path") val profilePath: String?,
    val birthday: String?,
    @SerializedName("place_of_birth") val placeOfBirth: String?,
    val popularity: Double,
)

data class PersonCreditsResponse(
    val id: Int,
    val cast: List<PersonCastCreditDto>,
)

data class PersonCastCreditDto(
    val id: Int,
    val title: String?,           // movies
    val name: String?,            // tv shows
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("media_type") val mediaType: String, // "movie" or "tv"
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
)