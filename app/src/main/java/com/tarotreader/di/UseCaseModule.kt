package com.tarotreader.di

import com.tarotreader.domain.repository.TarotRepository
import com.tarotreader.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    @Provides
    @Singleton
    fun provideGetAllDecksUseCase(repository: TarotRepository): GetAllDecksUseCase {
        return GetAllDecksUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetDeckByIdUseCase(repository: TarotRepository): GetDeckByIdUseCase {
        return GetDeckByIdUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetAllSpreadsUseCase(repository: TarotRepository): GetAllSpreadsUseCase {
        return GetAllSpreadsUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetSpreadByIdUseCase(repository: TarotRepository): GetSpreadByIdUseCase {
        return GetSpreadByIdUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetAllCardsUseCase(repository: TarotRepository): GetAllCardsUseCase {
        return GetAllCardsUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetCardsByDeckUseCase(repository: TarotRepository): GetCardsByDeckUseCase {
        return GetCardsByDeckUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideSaveReadingUseCase(repository: TarotRepository): SaveReadingUseCase {
        return SaveReadingUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetAllReadingsUseCase(repository: TarotRepository): GetAllReadingsUseCase {
        return GetAllReadingsUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetCardByIdUseCase(repository: TarotRepository): GetCardByIdUseCase {
        return GetCardByIdUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideAnalyzeSpreadFromImageUseCase(repository: TarotRepository): AnalyzeSpreadFromImageUseCase {
        return AnalyzeSpreadFromImageUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGenerateReadingUseCase(repository: TarotRepository): GenerateReadingUseCase {
        return GenerateReadingUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideCalculateEigenvalueUseCase(): CalculateEigenvalueUseCase {
        return CalculateEigenvalueUseCase()
    }
}