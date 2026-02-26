package com.ruhaan.accolade.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ruhaan.accolade.data.local.dao.CreditsCacheDao
import com.ruhaan.accolade.data.local.dao.MovieDetailCacheDao
import com.ruhaan.accolade.data.local.dao.MovieListCacheDao
import com.ruhaan.accolade.data.local.dao.PersonCacheDao
import com.ruhaan.accolade.data.local.dao.ReviewsCacheDao
import com.ruhaan.accolade.data.local.entity.CreditsCacheEntity
import com.ruhaan.accolade.data.local.entity.MovieDetailCacheEntity
import com.ruhaan.accolade.data.local.entity.MovieListCacheEntity
import com.ruhaan.accolade.data.local.entity.PersonCacheEntity
import com.ruhaan.accolade.data.local.entity.ReviewsCacheEntity

@Database(
    entities = [
        MovieListCacheEntity::class,
        MovieDetailCacheEntity::class,
        CreditsCacheEntity::class,
        PersonCacheEntity::class,
        ReviewsCacheEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieListCacheDao(): MovieListCacheDao
    abstract fun movieDetailCacheDao(): MovieDetailCacheDao
    abstract fun creditsCacheDao(): CreditsCacheDao
    abstract fun personCacheDao(): PersonCacheDao
    abstract fun reviewsCacheDao(): ReviewsCacheDao
}