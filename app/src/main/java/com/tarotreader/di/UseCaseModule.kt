package com.tarotreader.di

import com.tarotreader.domain.repository.TarotRepository
import com.tarotreader.domain.usecase.*

object UseCaseModule {
    fun provideGetAllDecksUseCase(repository: TarotRepository): GetAllDecksUseCase {
        return GetAllDecksUseCase(repository)
    }
    
    fun provideGetDeckByIdUseCase(repository: TarotRepository): GetDeckByIdUseCase {
        return GetDeckByIdUseCase(repository)
    }
    
    fun provideGetAllSpreadsUseCase(repository: TarotRepository): GetAllSpreadsUseCase {
        return GetAllSpreadsUseCase(repository)
    }
    
    fun provideGetSpreadByIdUseCase(repository: TarotRepository): GetSpreadByIdUseCase {
        return GetSpreadByIdUseCase(repository)
    }
    
    fun provideGetAllCardsUseCase(repository: TarotRepository): GetAllCardsUseCase {
        return GetAllCardsUseCase(repository)
    }
    
    fun provideGetCardsByDeckUseCase(repository: TarotRepository): GetCardsByDeckUseCase {
        return GetCardsByDeckUseCase(repository)
    }
    
    fun provideSaveReadingUseCase(repository: TarotRepository): SaveReadingUseCase {
        return SaveReadingUseCase(repository)
    }
    
    fun provideGetAllReadingsUseCase(repository: TarotRepository): GetAllReadingsUseCase {
        return GetAllReadingsUseCase(repository)
    }
    
    fun provideGetCardByIdUseCase(repository: TarotRepository): GetCardByIdUseCase {
        return GetCardByIdUseCase(repository)
    }
    
    fun provideAnalyzeSpreadFromImageUseCase(repository: TarotRepository): AnalyzeSpreadFromImageUseCase {
        return AnalyzeSpreadFromImageUseCase(repository)
    }
    
    fun provideGenerateReadingUseCase(repository: TarotRepository): GenerateReadingUseCase {
        return GenerateReadingUseCase(repository)
    }
    
    fun provideCalculateEigenvalueUseCase(): CalculateEigenvalueUseCase {
        return CalculateEigenvalueUseCase()
    }
}