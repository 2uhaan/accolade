package com.ruhaan.accolade.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores cast and crew for a given id + mediaType.
 *
 * Cache key conventions:
 *  - "cast_MOVIE_123"
 *  - "cast_TV_456"
 *  - "crew_MOVIE_123"
 *  - "crew_TV_456"
 */
@Entity(tableName = "credits_cache")
data class CreditsCacheEntity(
    @PrimaryKey val cacheKey: String,
    val json: String,   // Gson-serialized List<CastMember> or List<CrewMember>
    val cachedAt: Long,
)