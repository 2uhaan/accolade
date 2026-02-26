package com.ruhaan.accolade.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores any List<Movie> response keyed by a string.
 *
 * Cache key conventions:
 *  - "trending"
 *  - "editors_picks"
 *  - "prev_movies_1", "prev_tv_1"
 *  - "this_week_movies_1", "this_week_tv_1"
 *  - "upcoming_movies_1", "upcoming_tv_1"
 *  - "genre_movie_{genreId}_1", "genre_tv_{genreId}_1"
 */
@Entity(tableName = "movie_list_cache")
data class MovieListCacheEntity(
    @PrimaryKey val cacheKey: String,
    val json: String,          // Gson-serialized List<Movie>
    val cachedAt: Long,        // System.currentTimeMillis()
    val currentPage: Int = 1,
    val totalPages: Int = 1,
)