package com.ruhaan.accolade.domain.repository

import com.ruhaan.accolade.domain.model.Movie

interface MovieRepository {
  suspend fun getTrendingMovies(): List<Movie>

  suspend fun getTheatreMovies(): List<Movie>

  suspend fun getStreamingMovies(): List<Movie>

  suspend fun getUpcomingMovies(page: Int = 1): List<Movie>

  suspend fun getUpcomingTvShows(page: Int = 1): List<Movie>
}
