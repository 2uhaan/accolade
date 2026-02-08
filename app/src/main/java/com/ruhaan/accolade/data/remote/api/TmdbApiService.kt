package com.ruhaan.accolade.data.remote.api

import com.ruhaan.accolade.BuildConfig
import com.ruhaan.accolade.data.remote.dto.MovieListResponse
import com.ruhaan.accolade.data.remote.dto.TvShowListResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface TmdbApiService {
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
}
