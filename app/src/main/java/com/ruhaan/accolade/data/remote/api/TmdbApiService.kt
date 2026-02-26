package com.ruhaan.accolade.data.remote.api

import com.ruhaan.accolade.BuildConfig
import com.ruhaan.accolade.data.remote.dto.*
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

  // Search
  @GET("search/multi")
  suspend fun searchMulti(
      @Header("Authorization") authorization: String = "Bearer ${BuildConfig.TMDB_API_KEY}",
      @Query("query") query: String,
      @Query("page") page: Int = 1,
  ): SearchResponse

  // Home Screen
  @GET("trending/all/day")
  suspend fun getTrendingAll(
      @Header("Authorization") authorization: String = "Bearer ${BuildConfig.TMDB_API_KEY}",
  ): TrendingResponse

  // Schedule Screen
  @GET("discover/movie")
  suspend fun discoverMovies(
      @Header("Authorization") authorization: String = "Bearer ${BuildConfig.TMDB_API_KEY}",
      @Query("page") page: Int,
      @Query("language") language: String = "en-US",
      @Query("primary_release_date.gte") minDate: String? = null,
      @Query("primary_release_date.lte") maxDate: String? = null,
      @Query("sort_by") sortBy: String = "popularity.desc",
      @Query("vote_count.gte") minVoteCount: Int? = null,
      @Query("popularity.gte") minPopularity: Float? = null,
      @Query("with_original_language") withOriginalLanguage: String? = null,
  ): MovieListResponse

  @GET("discover/tv")
  suspend fun discoverTv(
      @Header("Authorization") authorization: String = "Bearer ${BuildConfig.TMDB_API_KEY}",
      @Query("page") page: Int,
      @Query("language") language: String = "en-US",
      @Query("first_air_date.gte") minDate: String? = null,
      @Query("first_air_date.lte") maxDate: String? = null,
      @Query("sort_by") sortBy: String = "popularity.desc",
      @Query("vote_count.gte") minVoteCount: Int? = null,
      @Query("popularity.gte") minPopularity: Float? = null,
      @Query("with_original_language") withOriginalLanguage: String? = null,
  ): TvShowListResponse

  // Content Detailed Page

  @GET("movie/{movie_id}")
  suspend fun getMovieDetail(
      @Path("movie_id") movieId: Int,
      @Header("Authorization") authorization: String = "Bearer ${BuildConfig.TMDB_API_KEY}",
  ): MovieDetailDto

  @GET("tv/{tv_id}")
  suspend fun getTvShowDetail(
      @Path("tv_id") tvId: Int,
      @Header("Authorization") authorization: String = "Bearer ${BuildConfig.TMDB_API_KEY}",
  ): TvShowDetailDto

  @GET("movie/{movie_id}/credits")
  suspend fun getMovieCredits(
      @Path("movie_id") movieId: Int,
      @Header("Authorization") authorization: String = "Bearer ${BuildConfig.TMDB_API_KEY}",
  ): CreditsResponse

  @GET("tv/{tv_id}/credits")
  suspend fun getTvShowCredits(
      @Path("tv_id") tvId: Int,
      @Header("Authorization") authorization: String = "Bearer ${BuildConfig.TMDB_API_KEY}",
  ): CreditsResponse

  @GET("movie/{movie_id}/videos")
  suspend fun getMovieVideos(
      @Path("movie_id") movieId: Int,
      @Header("Authorization") authorization: String = "Bearer ${BuildConfig.TMDB_API_KEY}",
  ): VideosResponse

  @GET("tv/{tv_id}/videos")
  suspend fun getTvShowVideos(
      @Path("tv_id") tvId: Int,
      @Header("Authorization") authorization: String = "Bearer ${BuildConfig.TMDB_API_KEY}",
  ): VideosResponse

  @GET("discover/movie")
  suspend fun discoverMoviesByGenre(
      @Header("Authorization") authorization: String = "Bearer ${BuildConfig.TMDB_API_KEY}",
      @Query("with_genres") genreId: Int,
      @Query("sort_by") sortBy: String = "popularity.desc",
      @Query("page") page: Int = 1,
  ): MovieListResponse

  @GET("discover/tv")
  suspend fun discoverTvShowsByGenre(
      @Header("Authorization") authorization: String = "Bearer ${BuildConfig.TMDB_API_KEY}",
      @Query("with_genres") genreId: Int,
      @Query("sort_by") sortBy: String = "popularity.desc",
      @Query("page") page: Int = 1,
  ): TvShowListResponse

  @GET("movie/{movie_id}/reviews")
  suspend fun getMovieReviews(
      @Path("movie_id") movieId: Int,
      @Header("Authorization") authorization: String = "Bearer ${BuildConfig.TMDB_API_KEY}",
  ): ReviewsResponse

  @GET("tv/{tv_id}/reviews")
  suspend fun getTvShowReviews(
      @Path("tv_id") tvId: Int,
      @Header("Authorization") authorization: String = "Bearer ${BuildConfig.TMDB_API_KEY}",
  ): ReviewsResponse

  @GET("person/{person_id}")
  suspend fun getPersonDetail(
      @Path("person_id") personId: Int,
      @Header("Authorization") authorization: String = "Bearer ${BuildConfig.TMDB_API_KEY}",
  ): PersonDetailDto

  @GET("person/{person_id}/combined_credits")
  suspend fun getPersonCredits(
      @Path("person_id") personId: Int,
      @Header("Authorization") authorization: String = "Bearer ${BuildConfig.TMDB_API_KEY}",
  ): PersonCreditsResponse
}
