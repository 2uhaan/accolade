package com.ruhaan.accolade.data.remote.api

import com.ruhaan.accolade.BuildConfig
import com.ruhaan.accolade.data.remote.dto.*
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

  // Home Screen
  @GET("trending/movie/week")
  suspend fun getTrendingMovies(
      @Header("Authorization") authorization: String = "Bearer ${BuildConfig.TMDB_API_KEY}",
      @Query("region") region: String,
      @Query("sort_by") sortBy: String,
  ): MovieListResponse

  @GET("movie/now_playing")
  suspend fun getNowPlayingMovies(
      @Header("Authorization") authorization: String = "Bearer ${BuildConfig.TMDB_API_KEY}",
      @Query("region") region: String,
      @Query("sort_by") sortBy: String,
      @Query("with_release_type") releaseType: String,
  ): MovieListResponse

  @GET("discover/movie")
  suspend fun getUpcomingMovies(
      @Header("Authorization") authorization: String = "Bearer ${BuildConfig.TMDB_API_KEY}",
      @Query("page") page: Int,
      @Query("release_date.gte") minDate: String,
      @Query("sort_by") sortBy: String,
      @Query("region") region: String,
      @Query("with_release_type") releaseType: String,
  ): MovieListResponse

  @GET("discover/tv")
  suspend fun getAiringTodayTvShows(
      @Header("Authorization") authorization: String = "Bearer ${BuildConfig.TMDB_API_KEY}",
      @Query("page") page: Int,
      @Query("first_air_date.gte") minDate: String,
      @Query("sort_by") sortBy: String,
      @Query("with_origin_country") originCountry: String,
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
}
