package com.tarotreader.di

import com.tarotreader.data.repository.TarotRepository
import com.tarotreader.domain.repository.TarotRepository as DomainTarotRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTarotRepository(
        tarotRepository: TarotRepository
    ): DomainTarotRepository
}