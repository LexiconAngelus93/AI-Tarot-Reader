package com.tarotreader.di

import com.tarotreader.data.database.TarotDao
import com.tarotreader.data.repository.DailyDrawRepositoryImpl
import com.tarotreader.domain.repository.DailyDrawRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DailyDrawRepositoryModule {
    
    @Provides
    @Singleton
    fun provideDailyDrawRepository(dao: TarotDao): DailyDrawRepository {
        return DailyDrawRepositoryImpl(dao)
    }
}