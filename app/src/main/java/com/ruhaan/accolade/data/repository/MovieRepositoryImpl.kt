package com.ruhaan.accolade.data.repository

import com.ruhaan.accolade.data.remote.api.TmdbApiService
import com.ruhaan.accolade.domain.mapper.MovieDetailMapper
import com.ruhaan.accolade.domain.mapper.MovieMapper
import com.ruhaan.accolade.domain.mapper.SearchMapper
import com.ruhaan.accolade.domain.model.*
import com.ruhaan.accolade.domain.repository.MovieRepository
import com.ruhaan.accolade.presentation.home.components.EditorsPicks
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

@Singleton
class MovieRepositoryImpl @Inject constructor(private val apiService: TmdbApiService) :
    MovieRepository {

  override suspend fun getTrendingMovies(): List<Movie> {
    val response = apiService.getTrendingAll()
    return MovieMapper.mapFromTrendingList(response.results).take(6)
  }

  override suspend fun getEditorsPicks(): List<Movie> = coroutineScope {
    EditorsPicks.items
        .map { curated ->
          async {
            runCatching {
                  when (curated.mediaType) {
                    MediaType.MOVIE -> {
                      val dto = apiService.getMovieDetail(curated.tmdbId)
                      Movie(
                          id = dto.id,
                          title = dto.title,
                          year = dto.releaseDate?.take(4) ?: "N/A",
                          posterPath = "https://image.tmdb.org/t/p/w500${dto.posterPath ?: ""}",
                          backdropPath =
                              if (dto.backdropPath != null)
                                  "https://image.tmdb.org/t/p/w780${dto.backdropPath}"
                              else null,
                          releaseDate = dto.releaseDate ?: "",
                          mediaType = MediaType.MOVIE,
                      )
                    }
                    MediaType.TV_SHOW -> {
                      val dto = apiService.getTvShowDetail(curated.tmdbId)
                      Movie(
                          id = dto.id,
                          title = dto.name,
                          year = dto.firstAirDate?.take(4) ?: "N/A",
                          posterPath = "https://image.tmdb.org/t/p/w500${dto.posterPath ?: ""}",
                          backdropPath =
                              if (dto.backdropPath != null)
                                  "https://image.tmdb.org/t/p/w780${dto.backdropPath}"
                              else null,
                          releaseDate = dto.firstAirDate ?: "",
                          mediaType = MediaType.TV_SHOW,
                      )
                    }
                  }
                }
                .getOrNull()
          }
        }
        .awaitAll()
        .filterNotNull()
  }

  override suspend fun getUpcomingMovies(page: Int): List<Movie> {
    val today =
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())

    val response =
        apiService.getUpcomingMovies(
            page = page,
            minDate = today,
            sortBy = "release_date.asc",
            region = "IN", // Content available in India
            releaseType = "3|4|6", // Theatrical + Digital + Streaming
            minVoteCount = 10, // Filter out obscure content
        )

    return MovieMapper.mapFromDtoList(response.results)
  }

  override suspend fun getUpcomingTvShows(page: Int): List<Movie> {
    val response =
        apiService.getUpcomingTvShows(
            page = page,
            region = "IN", // Content available in India
            minVoteCount = 10, // Filter out obscure content
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

  override suspend fun getMoviesByGenre(genreId: Int, page: Int): List<Movie> {
    val response =
        apiService.discoverMoviesByGenre(genreId = genreId, sortBy = "popularity.desc", page = page)
    return MovieMapper.mapFromDtoList(response.results)
  }

  override suspend fun getTvShowsByGenre(genreId: Int, page: Int): List<Movie> {
    val response =
        apiService.discoverTvShowsByGenre(
            genreId = genreId,
            sortBy = "popularity.desc",
            page = page,
        )
    return MovieMapper.mapFromTvShowDtoList(response.results)
  }

  override suspend fun searchMulti(query: String): List<SearchResult> {
    val response = apiService.searchMulti(query = query, page = 1)
    return SearchMapper.mapSearchResults(response.results).take(20) // Limit to 20 results
  }
}
