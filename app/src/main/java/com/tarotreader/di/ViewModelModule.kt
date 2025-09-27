package com.tarotreader.di

import com.tarotreader.domain.usecase.*
import com.tarotreader.presentation.viewmodel.TarotViewModel

object ViewModelModule {
    fun provideTarotViewModel(
        getAllDecksUseCase: GetAllDecksUseCase,
        getDeckByIdUseCase: GetDeckByIdUseCase,
        getAllSpreadsUseCase: GetAllSpreadsUseCase,
        getSpreadByIdUseCase: GetSpreadByIdUseCase,
        getAllCardsUseCase: GetAllCardsUseCase,
        getCardsByDeckUseCase: GetCardsByDeckUseCase,
        saveReadingUseCase: SaveReadingUseCase,
        analyzeSpreadFromImageUseCase: AnalyzeSpreadFromImageUseCase,
        generateReadingUseCase: GenerateReadingUseCase,
        calculateEigenvalueUseCase: CalculateEigenvalueUseCase
    ): TarotViewModel {
        return TarotViewModel(
            getAllDecksUseCase,
            getDeckByIdUseCase,
            getAllSpreadsUseCase,
            getSpreadByIdUseCase,
            getAllCardsUseCase,
            getCardsByDeckUseCase,
            saveReadingUseCase,
            analyzeSpreadFromImageUseCase,
            generateReadingUseCase,
            calculateEigenvalueUseCase
        )
    }
}