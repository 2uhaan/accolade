package com.ruhaan.accolade.domain.mapper

import com.ruhaan.accolade.data.remote.dto.*
import com.ruhaan.accolade.domain.model.*
import kotlin.math.roundToInt

object MovieDetailMapper {

    fun mapMovieDetail(
        detailDto: MovieDetailDto,
        credits: CreditsResponse,
        videos: VideosResponse
    ): MovieDetail {
        val director = credits.crew.firstOrNull { it.job == "Director" }?.name ?: "N/A"
        val runtime = formatMovieRuntime(detailDto.runtime)
        val trailer = extractTrailer(videos)

        return MovieDetail(
            id = detailDto.id,
            title = detailDto.title,
            mediaType = MediaType.MOVIE,
            posterPath = "https://image.tmdb.org/t/p/w500${detailDto.posterPath ?: ""}",
            backdropPath = detailDto.backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" },
            country = detailDto.productionCountries.firstOrNull()?.name ?: "N/A",
            language = detailDto.spokenLanguages.firstOrNull()?.englishName ?: "N/A",
            directorOrShowrunner = director,
            runtime = runtime,
            synopsis = detailDto.overview ?: "No synopsis available",
            rating = (detailDto.voteAverage * 10).roundToInt(),
            trailer = trailer,
        )
    }

    fun mapTvShowDetail(
        detailDto: TvShowDetailDto,
        credits: CreditsResponse,
        videos: VideosResponse
    ): MovieDetail {
        val showrunner = detailDto.createdBy.firstOrNull()?.name ?: "N/A"
        val runtime = formatTvRuntime(detailDto.episodeRunTime)
        val trailer = extractTrailer(videos)

        return MovieDetail(
            id = detailDto.id,
            title = detailDto.name,
            mediaType = MediaType.TV_SHOW,
            posterPath = "https://image.tmdb.org/t/p/w500${detailDto.posterPath ?: ""}",
            backdropPath = detailDto.backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" },
            country = detailDto.productionCountries.firstOrNull()?.name ?: "N/A",
            language = detailDto.spokenLanguages.firstOrNull()?.englishName ?: "N/A",
            directorOrShowrunner = showrunner,
            runtime = runtime,
            synopsis = detailDto.overview ?: "No synopsis available",
            rating = (detailDto.voteAverage * 10).roundToInt(),
            trailer = trailer,
        )
    }

    fun mapCast(credits: CreditsResponse): List<CastMember> {
        return credits.cast.take(10).map { dto ->
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
        val keyJobs = setOf("Director", "Writer", "Screenplay", "Producer", "Executive Producer",
            "Director of Photography", "Editor")

        return credits.crew
            .filter { it.department in keyDepartments || it.job in keyJobs }
            .distinctBy { it.id } // Remove duplicates (same person, different roles)
            .map { dto ->
                CrewMember(
                    id = dto.id,
                    name = dto.name,
                    job = dto.job,
                    profilePath = dto.profilePath?.let { "https://image.tmdb.org/t/p/w185$it" },
                )
            }
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
        // Priority: Official trailers from YouTube
        val trailer = videos.results.firstOrNull {
            it.site == "YouTube" && it.type == "Trailer" && it.official
        } ?: videos.results.firstOrNull {
            it.site == "YouTube" && it.type == "Trailer"
        }

        return trailer?.let {
            Trailer(
                key = it.key,
                name = it.name,
                thumbnailUrl = "https://img.youtube.com/vi/${it.key}/maxresdefault.jpg",
            )
        }
    }
}