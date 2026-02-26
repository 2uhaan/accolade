package com.ruhaan.accolade.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ruhaan.accolade.data.local.entity.PersonCacheEntity

@Dao
interface PersonCacheDao {

    @Query("SELECT * FROM person_cache WHERE cacheKey = :key")
    suspend fun get(key: String): PersonCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PersonCacheEntity)

    @Query("DELETE FROM person_cache WHERE cacheKey = :key")
    suspend fun delete(key: String)

    @Query("DELETE FROM person_cache")
    suspend fun clearAll()
}