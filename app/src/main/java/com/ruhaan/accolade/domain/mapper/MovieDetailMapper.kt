package com.ruhaan.accolade.domain.mapper

import com.ruhaan.accolade.data.remote.dto.*
import com.ruhaan.accolade.domain.model.*
import kotlin.math.roundToInt

object MovieDetailMapper {

  fun mapMovieDetail(
      detailDto: MovieDetailDto,
      credits: CreditsResponse,
      videos: VideosResponse,
  ): MovieDetail {
    val directors =
        credits.crew.filter { it.job == "Director" }.map { DirectorInfo(it.id, it.name) }
    val runtime = formatMovieRuntime(detailDto.runtime)
    val trailer = extractTrailer(videos)
    val genres = detailDto.genres.map { Genre(it.id, it.name) }

    return MovieDetail(
        id = detailDto.id,
        title = detailDto.title,
        mediaType = MediaType.MOVIE,
        posterPath = "https://image.tmdb.org/t/p/w500${detailDto.posterPath ?: ""}",
        backdropPath = detailDto.backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" },
        country = detailDto.productionCountries.firstOrNull()?.name ?: "N/A",
        language = detailDto.spokenLanguages.firstOrNull()?.englishName ?: "N/A",
        directors = directors,
        runtime = runtime,
        synopsis = detailDto.overview ?: "No synopsis available",
        rating = (detailDto.voteAverage * 10).roundToInt(),
        trailer = trailer,
        genres = genres,
    )
  }

  fun mapTvShowDetail(
      detailDto: TvShowDetailDto,
      credits: CreditsResponse,
      videos: VideosResponse,
  ): MovieDetail {
    val directors = detailDto.createdBy.map { DirectorInfo(it.id, it.name) }
    val runtime = formatTvRuntime(detailDto.episodeRunTime)
    val trailer = extractTrailer(videos)
    val genres = detailDto.genres.map { Genre(it.id, it.name) }

    return MovieDetail(
        id = detailDto.id,
        title = detailDto.name,
        mediaType = MediaType.TV_SHOW,
        posterPath = "https://image.tmdb.org/t/p/w500${detailDto.posterPath ?: ""}",
        backdropPath = detailDto.backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" },
        country = detailDto.productionCountries.firstOrNull()?.name ?: "N/A",
        language = detailDto.spokenLanguages.firstOrNull()?.englishName ?: "N/A",
        directors = directors,
        runtime = runtime,
        synopsis = detailDto.overview ?: "No synopsis available",
        rating = (detailDto.voteAverage * 10).roundToInt(),
        trailer = trailer,
        genres = genres,
    )
  }

  fun mapCast(credits: CreditsResponse): List<CastMember> {
    // return credits.cast.take(10).map
    return credits.cast.map { dto ->
      CastMember(
          id = dto.id,
          name = dto.name,
          character = dto.character,
          profilePath = dto.profilePath?.let { "https://image.tmdb.org/t/p/w185$it" },
      )
    }
  }

  fun mapCrew(credits: CreditsResponse): List<CrewMember> {
    val keyDepartments = setOf("Directing", "Writing", "Production", "Camera", "Editing")
    val keyJobs =
        setOf(
            "Director",
            "Writer",
            "Screenplay",
            "Producer",
            "Executive Producer",
            "Director of Photography",
            "Editor",
        )

    val jobPriority =
        mapOf(
            "Director" to 1,
            "Writer" to 2,
            "Screenplay" to 3,
            "Producer" to 4,
            "Executive Producer" to 5,
            "Director of Photography" to 6,
            "Editor" to 7,
        )

    return credits.crew
        .filter { it.department in keyDepartments || it.job in keyJobs }
        .groupBy { it.id }
        .map { (_, crewList) ->
          val allJobs = crewList.map { it.job }.distinct().sortedBy { jobPriority[it] ?: 999 }

          val jobs =
              if (allJobs.size <= 2) {
                allJobs.joinToString(", ")
              } else {
                "${allJobs.take(2).joinToString(", ")}..."
              }

          val primaryJob =
              crewList.map { it.job }.minByOrNull { jobPriority[it] ?: 999 } ?: crewList.first().job

          CrewMember(
              id = crewList.first().id,
              name = crewList.first().name,
              job = jobs,
              profilePath =
                  crewList.first().profilePath?.let { "https://image.tmdb.org/t/p/w185$it" },
          )
        }
        .sortedWith(
            compareBy(
                { member -> member.job.split(", ").minOfOrNull { jobPriority[it] ?: 999 } ?: 999 },
                { it.name },
            )
        )
    //        .take(10) // Take top 10 after sorting
  }

  private fun formatMovieRuntime(minutes: Int?): String {
    if (minutes == null || minutes == 0) return "N/A"
    val hours = minutes / 60
    val mins = minutes % 60
    return if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
  }

  private fun formatTvRuntime(runtimes: List<Int>): String {
    if (runtimes.isEmpty()) return "N/A"
    val avg = runtimes.average().roundToInt()
    return "$avg min avg"
  }

  private fun extractTrailer(videos: VideosResponse): Trailer? {
    val trailer =
        videos.results.firstOrNull { it.site == "YouTube" && it.type == "Trailer" && it.official }
            ?: videos.results.firstOrNull { it.site == "YouTube" && it.type == "Trailer" }

    return trailer?.let {
      Trailer(
          key = it.key,
          name = it.name,
          thumbnailUrl = "https://img.youtube.com/vi/${it.key}/maxresdefault.jpg",
      )
    }
  }

  fun mapReviews(response: ReviewsResponse): List<Review> {
    return response.results
        .map { dto ->
          val rating =
              dto.authorDetails.rating?.let { raw ->
                (raw / 10.0 * 100).roundToInt().coerceIn(0, 100)
              }

          val avatarPath =
              dto.authorDetails.avatarPath?.let { path ->
                // TMDB sometimes prefixes avatar paths with /https://... for Gravatar
                if (path.startsWith("/https://") || path.startsWith("/http://")) {
                  path.removePrefix("/")
                } else {
                  "https://image.tmdb.org/t/p/w185$path"
                }
              }

          val formattedDate =
              try {
                val inputFormat =
                    java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US)
                val outputFormat = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.US)
                val date = inputFormat.parse(dto.createdAt)
                outputFormat.format(date ?: return@map null)
              } catch (_: Exception) {
                dto.createdAt.take(10) // fallback: raw "yyyy-MM-dd"
              }

          Review(
              id = dto.id,
              author = dto.authorDetails.name.ifBlank { dto.author },
              content = dto.content,
              rating = rating,
              avatarPath = avatarPath,
              createdAt = formattedDate,
          )
        }
        .filterNotNull()
  }
}
