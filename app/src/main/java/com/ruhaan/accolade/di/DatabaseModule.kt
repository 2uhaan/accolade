package com.ruhaan.accolade.di

import android.content.Context
import androidx.room.Room
import com.ruhaan.accolade.data.local.AppDatabase
import com.ruhaan.accolade.data.local.dao.CreditsCacheDao
import com.ruhaan.accolade.data.local.dao.MovieDetailCacheDao
import com.ruhaan.accolade.data.local.dao.MovieListCacheDao
import com.ruhaan.accolade.data.local.dao.PersonCacheDao
import com.ruhaan.accolade.data.local.dao.ReviewsCacheDao
import com.ruhaan.accolade.data.remote.api.TmdbApiService
import com.ruhaan.accolade.data.repository.MovieRepositoryImpl
import com.ruhaan.accolade.domain.repository.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "accolade_cache.db",
        ).build()

    @Provides fun provideMovieListCacheDao(db: AppDatabase): MovieListCacheDao = db.movieListCacheDao()
    @Provides fun provideMovieDetailCacheDao(db: AppDatabase): MovieDetailCacheDao = db.movieDetailCacheDao()
    @Provides fun provideCreditsCacheDao(db: AppDatabase): CreditsCacheDao = db.creditsCacheDao()
    @Provides fun providePersonCacheDao(db: AppDatabase): PersonCacheDao = db.personCacheDao()
    @Provides fun provideReviewsCacheDao(db: AppDatabase): ReviewsCacheDao = db.reviewsCacheDao()

    @Provides
    @Singleton
    fun provideMovieRepository(
        apiService: TmdbApiService,
        movieListCacheDao: MovieListCacheDao,
        movieDetailCacheDao: MovieDetailCacheDao,
        creditsCacheDao: CreditsCacheDao,
        personCacheDao: PersonCacheDao,
        reviewsCacheDao: ReviewsCacheDao,
    ): MovieRepository = MovieRepositoryImpl(
        apiService = apiService,
        movieListCacheDao = movieListCacheDao,
        movieDetailCacheDao = movieDetailCacheDao,
        creditsCacheDao = creditsCacheDao,
        personCacheDao = personCacheDao,
        reviewsCacheDao = reviewsCacheDao,
    )
}