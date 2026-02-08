package com.ruhaan.accolade.data.repository

import com.ruhaan.accolade.data.remote.api.TmdbApiService
import com.ruhaan.accolade.domain.mapper.MovieDetailMapper
import com.ruhaan.accolade.domain.mapper.MovieMapper
import com.ruhaan.accolade.domain.model.*
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

  override suspend fun getMovieDetail(id: Int, mediaType: MediaType): MovieDetail {
    return when (mediaType) {
      MediaType.MOVIE -> {
        val detail = apiService.getMovieDetail(id)
        val credits = apiService.getMovieCredits(id)
        val videos = apiService.getMovieVideos(id)
        MovieDetailMapper.mapMovieDetail(detail, credits, videos)
      }
      MediaType.TV_SHOW -> {
        val detail = apiService.getTvShowDetail(id)
        val credits = apiService.getTvShowCredits(id)
        val videos = apiService.getTvShowVideos(id)
        MovieDetailMapper.mapTvShowDetail(detail, credits, videos)
      }
    }
  }

  override suspend fun getCast(id: Int, mediaType: MediaType): List<CastMember> {
    val credits =
        when (mediaType) {
          MediaType.MOVIE -> apiService.getMovieCredits(id)
          MediaType.TV_SHOW -> apiService.getTvShowCredits(id)
        }
    return MovieDetailMapper.mapCast(credits)
  }

  override suspend fun getCrew(id: Int, mediaType: MediaType): List<CrewMember> {
    val credits =
        when (mediaType) {
          MediaType.MOVIE -> apiService.getMovieCredits(id)
          MediaType.TV_SHOW -> apiService.getTvShowCredits(id)
        }
    return MovieDetailMapper.mapCrew(credits)
  }
}
