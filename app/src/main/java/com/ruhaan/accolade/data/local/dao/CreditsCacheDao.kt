package com.ruhaan.accolade.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ruhaan.accolade.data.local.entity.CreditsCacheEntity

@Dao
interface CreditsCacheDao {

    @Query("SELECT * FROM credits_cache WHERE cacheKey = :key")
    suspend fun get(key: String): CreditsCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CreditsCacheEntity)

    @Query("DELETE FROM credits_cache WHERE cacheKey = :key")
    suspend fun delete(key: String)

    @Query("DELETE FROM credits_cache")
    suspend fun clearAll()
}