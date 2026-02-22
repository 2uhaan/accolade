package com.ruhaan.accolade.domain.repository

import com.ruhaan.accolade.domain.model.CastMember
import com.ruhaan.accolade.domain.model.CrewMember
import com.ruhaan.accolade.domain.model.MediaType
import com.ruhaan.accolade.domain.model.Movie
import com.ruhaan.accolade.domain.model.MovieDetail
import com.ruhaan.accolade.domain.model.Review
import com.ruhaan.accolade.domain.model.SearchResult
import com.ruhaan.accolade.domain.model.Person

interface MovieRepository {
  // Home screen methods
  suspend fun getTrendingMovies(): List<Movie>

  suspend fun getEditorsPicks(): List<Movie>

  // Schedule screen methods
  suspend fun getUpcomingMovies(page: Int = 1): List<Movie>

  suspend fun getUpcomingTvShows(page: Int = 1): List<Movie>

  // Detail screen methods
  suspend fun getMovieDetail(id: Int, mediaType: MediaType): MovieDetail

  suspend fun getCast(id: Int, mediaType: MediaType): List<CastMember>

  suspend fun getCrew(id: Int, mediaType: MediaType): List<CrewMember>

  suspend fun getMoviesByGenre(genreId: Int, page: Int): List<Movie>

  suspend fun getTvShowsByGenre(genreId: Int, page: Int): List<Movie>

  suspend fun getReviews(id: Int, mediaType: MediaType): List<Review>

  // Search
  suspend fun searchMulti(query: String): List<SearchResult>

  suspend fun getPersonDetail(personId: Int): Person

  suspend fun getPersonFilmography(personId: Int): List<Movie>
}
