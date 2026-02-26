package com.ruhaan.accolade.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores Person detail and their filmography.
 *
 * Cache key conventions:
 *  - "person_123"
 *  - "filmography_123"
 */
@Entity(tableName = "person_cache")
data class PersonCacheEntity(
    @PrimaryKey val cacheKey: String,
    val json: String,   // Gson-serialized Person or List<Movie>
    val cachedAt: Long,
)