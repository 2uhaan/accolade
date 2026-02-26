package com.ruhaan.accolade.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores reviews for a given id + mediaType.
 * Cache key: "reviews_MOVIE_123" or "reviews_TV_456"
 */
@Entity(tableName = "reviews_cache")
data class ReviewsCacheEntity(
    @PrimaryKey val cacheKey: String,
    val json: String,   // Gson-serialized List<Review>
    val cachedAt: Long,
)