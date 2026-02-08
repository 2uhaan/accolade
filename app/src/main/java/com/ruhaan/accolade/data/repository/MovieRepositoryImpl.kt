package com.ruhaan.accolade.data.repository

import com.ruhaan.accolade.data.remote.api.TmdbApiService
import com.ruhaan.accolade.domain.mapper.MovieMapper
import com.ruhaan.accolade.domain.model.Movie
import com.ruhaan.accolade.domain.repository.MovieRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(private val apiService: TmdbApiService) :
    MovieRepository {

  override suspend fun getTrendingMovies(): List<Movie> {
    val response =
        apiService.getTrendingMovies(
            region = "IN",
            sortBy = "popularity.desc",
        )
    return MovieMapper.mapFromDtoList(response.results).take(6)
  }

  override suspend fun getTheatreMovies(): List<Movie> {
    val response =
        apiService.getNowPlayingMovies(
            sortBy = "popularity.desc",
            region = "IN",
            releaseType = "3",
        )
    return MovieMapper.mapFromDtoList(response.results).take(6)
  }

  override suspend fun getStreamingMovies(): List<Movie> {
    val response =
        apiService.getNowPlayingMovies(
            sortBy = "popularity",
            region = "IN",
            releaseType = "4",
        )
    return MovieMapper.mapFromDtoList(response.results).take(6)
  }

  override suspend fun getUpcomingMovies(page: Int): List<Movie> {
    val today =
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())

    val response =
        apiService.getUpcomingMovies(
            page = page,
            minDate = today,
            sortBy = "release_date.asc",
            region = "IN",
            releaseType = "3|4", // Theatrical + Digital releases
        )

    return MovieMapper.mapFromDtoList(response.results)
  }

  override suspend fun getUpcomingTvShows(page: Int): List<Movie> {
    val today =
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())

    val response =
        apiService.getAiringTodayTvShows(
            page = page,
            minDate = today,
            sortBy = "first_air_date.asc",
            originCountry = "IN", // Just India
        )

    return MovieMapper.mapFromTvShowDtoList(response.results)
  }
}
