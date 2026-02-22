package com.ruhaan.accolade.domain.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val mediaType: MediaType,
    val posterPath: String,
    val backdropPath: String?,
    val country: String,
    val language: String,
    val directors: List<DirectorInfo>,
    val runtime: String, // formatted as "2h 30m" or "42 min avg"
    val synopsis: String,
    val rating: Int, // percentage (0-100)
    val trailer: Trailer?,
    val genres: List<Genre> = emptyList(), // ADD THIS
)

data class DirectorInfo(val id: Int, val name: String)

data class Trailer(
    val key: String, // YouTube video ID
    val name: String,
    val thumbnailUrl: String, // YouTube thumbnail
)

data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    val profilePath: String?,
)

data class CrewMember(
    val id: Int,
    val name: String,
    val job: String,
    val profilePath: String?,
)

data class Genre(val id: Int, val name: String)
