package com.ruhaan.accolade.di

import com.ruhaan.accolade.BuildConfig
import com.ruhaan.accolade.data.remote.api.TmdbApiService
import com.ruhaan.accolade.data.repository.MovieRepositoryImpl
import com.ruhaan.accolade.domain.repository.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient {
    val logging =
        HttpLoggingInterceptor().apply {
          level =
              if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
              } else {
                HttpLoggingInterceptor.Level.NONE
              }
        }

    return OkHttpClient.Builder().addInterceptor(logging).build()
  }

  @Provides
  @Singleton
  fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
  }

  @Provides
  @Singleton
  fun provideTmdbApiService(retrofit: Retrofit): TmdbApiService {
    return retrofit.create(TmdbApiService::class.java)
  }
}
