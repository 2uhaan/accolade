package com.ruhaan.accolade.domain.repository

import com.ruhaan.accolade.domain.model.CastMember
import com.ruhaan.accolade.domain.model.CrewMember
import com.ruhaan.accolade.domain.model.MediaType
import com.ruhaan.accolade.domain.model.Movie
import com.ruhaan.accolade.domain.model.MovieDetail

interface MovieRepository {
  // Home screen methods
  suspend fun getTrendingMovies(): List<Movie>

  suspend fun getTheatreMovies(): List<Movie>

  suspend fun getStreamingMovies(): List<Movie>

  // Schedule screen methods
  suspend fun getUpcomingMovies(page: Int = 1): List<Movie>

  suspend fun getUpcomingTvShows(page: Int = 1): List<Movie>

  // Detail screen methods
  suspend fun getMovieDetail(id: Int, mediaType: MediaType): MovieDetail

  suspend fun getCast(id: Int, mediaType: MediaType): List<CastMember>

  suspend fun getCrew(id: Int, mediaType: MediaType): List<CrewMember>
}
