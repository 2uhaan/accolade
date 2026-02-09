package com.ruhaan.accolade.data.remote.dto

import com.google.gson.annotations.SerializedName

// Movie Detail Response
data class MovieDetailDto(
    val id: Int,
    val title: String,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    val runtime: Int?, // in minutes
    @SerializedName("production_countries") val productionCountries: List<ProductionCountry>,
    @SerializedName("spoken_languages") val spokenLanguages: List<SpokenLanguage>,
    @SerializedName("vote_average") val voteAverage: Double,
    val genres: List<GenreDto> = emptyList(), // ADD THIS
)

// TV Show Detail Response
data class TvShowDetailDto(
    val id: Int,
    val name: String,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("episode_run_time") val episodeRunTime: List<Int>, // array of runtimes
    @SerializedName("production_countries") val productionCountries: List<ProductionCountry>,
    @SerializedName("spoken_languages") val spokenLanguages: List<SpokenLanguage>,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("created_by") val createdBy: List<Creator>,
    val genres: List<GenreDto> = emptyList(), // ADD THIS
)

data class GenreDto(val id: Int, val name: String)

data class ProductionCountry(@SerializedName("iso_3166_1") val code: String, val name: String)

data class SpokenLanguage(
    @SerializedName("iso_639_1") val code: String,
    val name: String,
    @SerializedName("english_name") val englishName: String,
)

data class Creator(
    val id: Int,
    val name: String,
    @SerializedName("profile_path") val profilePath: String?,
)

// Credits Response (same for movies and TV)
data class CreditsResponse(
    val id: Int,
    val cast: List<CastMemberDto>,
    val crew: List<CrewMemberDto>,
)

data class CastMemberDto(
    val id: Int,
    val name: String,
    val character: String,
    @SerializedName("profile_path") val profilePath: String?,
    val order: Int, // TMDB orders cast by importance
)

data class CrewMemberDto(
    val id: Int,
    val name: String,
    val job: String,
    val department: String,
    @SerializedName("profile_path") val profilePath: String?,
)

// Videos Response
data class VideosResponse(val id: Int, val results: List<VideoDto>)

data class VideoDto(
    val id: String,
    val key: String, // YouTube video ID
    val name: String,
    val site: String, // "YouTube"
    val type: String, // "Trailer", "Teaser", etc.
    val official: Boolean,
)
