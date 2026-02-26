package com.ruhaan.accolade.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ruhaan.accolade.data.local.entity.MovieListCacheEntity

@Dao
interface MovieListCacheDao {

    @Query("SELECT * FROM movie_list_cache WHERE cacheKey = :key")
    suspend fun get(key: String): MovieListCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: MovieListCacheEntity)

    @Query("DELETE FROM movie_list_cache WHERE cacheKey = :key")
    suspend fun delete(key: String)

    @Query("DELETE FROM movie_list_cache")
    suspend fun clearAll()
}