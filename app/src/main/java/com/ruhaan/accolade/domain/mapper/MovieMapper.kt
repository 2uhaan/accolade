package com.ruhaan.accolade.domain.mapper

import com.ruhaan.accolade.data.remote.dto.MovieDto
import com.ruhaan.accolade.data.remote.dto.TrendingItemDto
import com.ruhaan.accolade.data.remote.dto.TvShowDto
import com.ruhaan.accolade.domain.model.MediaType
import com.ruhaan.accolade.domain.model.Movie
import kotlin.collections.map

object MovieMapper {

  fun mapFromDto(dto: MovieDto): Movie {
    val year = dto.releaseDate?.take(4) ?: "N/A"
    return Movie(
        id = dto.id,
        title = dto.title,
        year = year,
        posterPath = "https://image.tmdb.org/t/p/w500${dto.posterPath ?: ""}",
        backdropPath =
            if (dto.backdropPath != null) "https://image.tmdb.org/t/p/w780${dto.backdropPath}"
            else null,
        releaseDate = dto.releaseDate ?: "",
        mediaType = MediaType.MOVIE, // Mark as movie
    )
  }

  fun mapFromDtoList(dtos: List<MovieDto>): List<Movie> {
    return dtos.map { mapFromDto(it) }
  }

  fun mapFromTvShowDto(dto: TvShowDto): Movie {
    val year = dto.firstAirDate?.take(4) ?: "N/A"
    return Movie(
        id = dto.id,
        title = dto.name, // TV shows use "name"
        year = year,
        posterPath = "https://image.tmdb.org/t/p/w500${dto.posterPath ?: ""}",
        backdropPath =
            if (dto.backdropPath != null) "https://image.tmdb.org/t/p/w780${dto.backdropPath}"
            else null,
        releaseDate = dto.firstAirDate ?: "",
        mediaType = MediaType.TV_SHOW, // Mark as TV show
    )
  }

  fun mapFromTvShowDtoList(dtos: List<TvShowDto>): List<Movie> {
    return dtos.map { mapFromTvShowDto(it) }
  }

  fun mapFromTrendingItem(dto: TrendingItemDto): Movie {
    val isTv = dto.mediaType == "tv"
    val title = if (isTv) dto.name ?: "Unknown" else dto.title ?: "Unknown"
    val releaseDate = if (isTv) dto.firstAirDate ?: "" else dto.releaseDate ?: ""
    val year = releaseDate.take(4).ifEmpty { "N/A" }
    return Movie(
        id = dto.id,
        title = title,
        year = year,
        posterPath = "https://image.tmdb.org/t/p/w500${dto.posterPath ?: ""}",
        backdropPath =
            if (dto.backdropPath != null) "https://image.tmdb.org/t/p/w780${dto.backdropPath}"
            else null,
        releaseDate = releaseDate,
        mediaType = if (isTv) MediaType.TV_SHOW else MediaType.MOVIE,
    )
  }

  fun mapFromTrendingList(dtos: List<TrendingItemDto>): List<Movie> {
    return dtos.map { mapFromTrendingItem(it) }
  }
}
