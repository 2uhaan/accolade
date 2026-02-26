package com.ruhaan.accolade.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ruhaan.accolade.data.local.entity.ReviewsCacheEntity

@Dao
interface ReviewsCacheDao {

    @Query("SELECT * FROM reviews_cache WHERE cacheKey = :key")
    suspend fun get(key: String): ReviewsCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ReviewsCacheEntity)

    @Query("DELETE FROM reviews_cache WHERE cacheKey = :key")
    suspend fun delete(key: String)

    @Query("DELETE FROM reviews_cache")
    suspend fun clearAll()
}