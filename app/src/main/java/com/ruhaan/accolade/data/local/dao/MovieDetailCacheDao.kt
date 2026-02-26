package com.ruhaan.accolade.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ruhaan.accolade.data.local.entity.MovieDetailCacheEntity

@Dao
interface MovieDetailCacheDao {

    @Query("SELECT * FROM movie_detail_cache WHERE cacheKey = :key")
    suspend fun get(key: String): MovieDetailCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: MovieDetailCacheEntity)

    @Query("DELETE FROM movie_detail_cache WHERE cacheKey = :key")
    suspend fun delete(key: String)

    @Query("DELETE FROM movie_detail_cache")
    suspend fun clearAll()
}