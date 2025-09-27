package com.tarotreader.presentation.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarotreader.domain.model.CardDrawing
import com.tarotreader.domain.usecase.*
import com.tarotreader.utils.PerformanceOptimizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TarotViewModel(
    private val getAllDecksUseCase: GetAllDecksUseCase,
    private val getDeckByIdUseCase: GetDeckByIdUseCase,
    private val getAllSpreadsUseCase: GetAllSpreadsUseCase,
    private val getSpreadByIdUseCase: GetSpreadByIdUseCase,
    private val getAllCardsUseCase: GetAllCardsUseCase,
    private val getCardsByDeckUseCase: GetCardsByDeckUseCase,
    private val saveReadingUseCase: SaveReadingUseCase,
    private val analyzeSpreadFromImageUseCase: AnalyzeSpreadFromImageUseCase,
    private val generateReadingUseCase: GenerateReadingUseCase,
    private val calculateEigenvalueUseCase: CalculateEigenvalueUseCase
) : ViewModel() {
    
    // Decks state
    private val _decks = MutableStateFlow<List<com.tarotreader.domain.model.TarotDeck>>(emptyList())
    val decks: StateFlow<List<com.tarotreader.domain.model.TarotDeck>> = _decks.asStateFlow()
    
    // Spreads state
    private val _spreads = MutableStateFlow<List<com.tarotreader.domain.model.TarotSpread>>(emptyList())
    val spreads: StateFlow<List<com.tarotreader.domain.model.TarotSpread>> = _spreads.asStateFlow()
    
    // Cards state
    private val _cards = MutableStateFlow<List<com.tarotreader.domain.model.TarotCard>>(emptyList())
    val cards: StateFlow<List<com.tarotreader.domain.model.TarotCard>> = _cards.asStateFlow()
    
    // Reading state
    private val _currentReading = MutableStateFlow<com.tarotreader.domain.model.Reading?>(null)
    val currentReading: StateFlow<com.tarotreader.domain.model.Reading?> = _currentReading.asStateFlow()
    
    init {
        loadDecks()
        loadSpreads()
        loadCards()
        PerformanceOptimizer.optimizeDatabaseQueries()
        PerformanceOptimizer.optimizeAIModelLoading()
    }
    
    private fun loadDecks() {
        viewModelScope.launch {
            getAllDecksUseCase().collect { deckList ->
                _decks.value = deckList
            }
        }
    }
    
    private fun loadSpreads() {
        viewModelScope.launch {
            getAllSpreadsUseCase().collect { spreadList ->
                _spreads.value = spreadList
            }
        }
    }
    
    private fun loadCards() {
        viewModelScope.launch {
            getAllCardsUseCase().collect { cardList ->
                _cards.value = cardList
            }
        }
    }
    
    fun getCardsForDeck(deckId: String) {
        viewModelScope.launch {
            getCardsByDeckUseCase(deckId).collect { cardList ->
                _cards.value = cardList
            }
        }
    }
    
    fun analyzeSpreadFromImage(bitmap: Bitmap, deckId: String) {
        viewModelScope.launch {
            val deck = getDeckByIdUseCase(deckId)
            if (deck != null) {
                val reading = analyzeSpreadFromImageUseCase(bitmap, deck)
                _currentReading.value = reading
            }
        }
    }
    
    fun generateReading(deckId: String, spreadId: String, cardDrawings: List<CardDrawing>) {
        viewModelScope.launch {
            val reading = generateReadingUseCase(deckId, spreadId, cardDrawings)
            _currentReading.value = reading
        }
    }
    
    fun calculateEigenvalue(cardDrawings: List<CardDrawing>): Double {
        return calculateEigenvalueUseCase(cardDrawings, _cards.value, _spreads.value.firstOrNull()?.positions ?: emptyList())
    }
    
    fun saveReading(reading: com.tarotreader.domain.model.Reading) {
        viewModelScope.launch {
            saveReadingUseCase(reading)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        PerformanceOptimizer.recycleBitmaps()
    }
}