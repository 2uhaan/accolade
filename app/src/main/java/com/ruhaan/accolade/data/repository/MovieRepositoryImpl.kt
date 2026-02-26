package com.ruhaan.accolade.data.repository

import com.ruhaan.accolade.data.remote.api.TmdbApiService
import com.ruhaan.accolade.domain.mapper.MovieDetailMapper
import com.ruhaan.accolade.domain.mapper.MovieMapper
import com.ruhaan.accolade.domain.mapper.SearchMapper
import com.ruhaan.accolade.domain.model.CastMember
import com.ruhaan.accolade.domain.model.CrewMember
import com.ruhaan.accolade.domain.model.MediaType
import com.ruhaan.accolade.domain.model.Movie
import com.ruhaan.accolade.domain.model.MovieDetail
import com.ruhaan.accolade.domain.model.PaginatedResult
import com.ruhaan.accolade.domain.model.Person
import com.ruhaan.accolade.domain.model.Review
import com.ruhaan.accolade.domain.model.SearchResult
import com.ruhaan.accolade.domain.repository.MovieRepository
import com.ruhaan.accolade.presentation.home.components.EditorsPicks
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
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

  private fun today(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

  private fun daysFromToday(days: Int): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, days)
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
  }

  private val targetLanguages = "en|hi|ta|te|ml|kn"

  override suspend fun getPreviousMovies(page: Int): PaginatedResult<Movie> {
    val response =
        apiService.discoverMovies(
            page = page,
            minDate = daysFromToday(-30),
            maxDate = today(),
            minVoteCount = 10,
            withOriginalLanguage = targetLanguages,
        )
    return PaginatedResult(
        MovieMapper.mapFromDtoList(response.results),
        response.page,
        response.totalPages,
    )
  }

  override suspend fun getPreviousTvShows(page: Int): PaginatedResult<Movie> {
    val response =
        apiService.discoverTv(
            page = page,
            minDate = daysFromToday(-30),
            maxDate = today(),
            minVoteCount = 10,
            withOriginalLanguage = targetLanguages,
        )
    return PaginatedResult(
        MovieMapper.mapFromTvShowDtoList(response.results),
        response.page,
        response.totalPages,
    )
  }

  override suspend fun getThisWeekMovies(page: Int): PaginatedResult<Movie> {
    val response =
        apiService.discoverMovies(
            page = page,
            minDate = today(),
            maxDate = daysFromToday(7),
            minPopularity = 10f,
            withOriginalLanguage = targetLanguages,
        )
    return PaginatedResult(
        MovieMapper.mapFromDtoList(response.results),
        response.page,
        response.totalPages,
    )
  }

  override suspend fun getThisWeekTvShows(page: Int): PaginatedResult<Movie> {
    val response =
        apiService.discoverTv(
            page = page,
            minDate = today(),
            maxDate = daysFromToday(7),
            minPopularity = 10f,
            withOriginalLanguage = targetLanguages,
        )
    return PaginatedResult(
        MovieMapper.mapFromTvShowDtoList(response.results),
        response.page,
        response.totalPages,
    )
  }

  override suspend fun getUpcomingMovies(page: Int): PaginatedResult<Movie> {
    val response =
        apiService.discoverMovies(
            page = page,
            minDate = daysFromToday(8),
            withOriginalLanguage = targetLanguages,
        )
    return PaginatedResult(
        MovieMapper.mapFromDtoList(response.results),
        response.page,
        response.totalPages,
    )
  }

  override suspend fun getUpcomingTvShows(page: Int): PaginatedResult<Movie> {
    val response =
        apiService.discoverTv(
            page = page,
            minDate = daysFromToday(8),
            withOriginalLanguage = targetLanguages,
        )
    return PaginatedResult(
        MovieMapper.mapFromTvShowDtoList(response.results),
        response.page,
        response.totalPages,
    )
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

  override suspend fun getReviews(id: Int, mediaType: MediaType): List<Review> {
    val response =
        when (mediaType) {
          MediaType.MOVIE -> apiService.getMovieReviews(id)
          MediaType.TV_SHOW -> apiService.getTvShowReviews(id)
        }
    return MovieDetailMapper.mapReviews(response)
  }

  override suspend fun getPersonDetail(personId: Int): Person {
    val dto = apiService.getPersonDetail(personId)
    return Person(
        id = dto.id,
        name = dto.name,
        biography = dto.biography?.takeIf { it.isNotBlank() },
        profilePath = dto.profilePath?.let { "https://image.tmdb.org/t/p/w500$it" },
        birthday = dto.birthday,
        placeOfBirth = dto.placeOfBirth,
    )
  }

  override suspend fun getPersonFilmography(personId: Int): List<Movie> {
    val response = apiService.getPersonCredits(personId)
    return response.cast
        .filter { it.posterPath != null }
        .distinctBy { it.id }
        .sortedByDescending { it.releaseDate ?: it.firstAirDate ?: "" }
        .map { credit ->
          Movie(
              id = credit.id,
              title = credit.title ?: credit.name ?: "Unknown",
              posterPath = "https://image.tmdb.org/t/p/w500${credit.posterPath}",
              mediaType = if (credit.mediaType == "tv") MediaType.TV_SHOW else MediaType.MOVIE,
              year = (credit.releaseDate ?: credit.firstAirDate ?: "").take(4),
          )
        }
  }
}
