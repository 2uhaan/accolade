package com.ruhaan.accolade.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores a MovieDetail for a given id + mediaType combination.
 * Cache key: "MOVIE_123" or "TV_456"
 */
@Entity(tableName = "movie_detail_cache")
data class MovieDetailCacheEntity(
    @PrimaryKey val cacheKey: String,  // e.g. "MOVIE_123"
    val json: String,                  // Gson-serialized MovieDetail
    val cachedAt: Long,
)