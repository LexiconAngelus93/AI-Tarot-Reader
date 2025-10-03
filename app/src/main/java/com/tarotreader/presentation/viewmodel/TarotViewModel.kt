package com.tarotreader.presentation.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarotreader.domain.model.CardDrawing
import com.tarotreader.domain.model.Reading
import com.tarotreader.domain.model.TarotCard
import com.tarotreader.domain.model.TarotDeck
import com.tarotreader.domain.model.TarotSpread
import com.tarotreader.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TarotViewModel @Inject constructor(
    private val getAllDecksUseCase: GetAllDecksUseCase,
    private val getDeckByIdUseCase: GetDeckByIdUseCase,
    private val getAllSpreadsUseCase: GetAllSpreadsUseCase,
    private val getSpreadByIdUseCase: GetSpreadByIdUseCase,
    private val getAllCardsUseCase: GetAllCardsUseCase,
    private val getCardsByDeckUseCase: GetCardsByDeckUseCase,
    private val getCardByIdUseCase: GetCardByIdUseCase,
    private val getAllReadingsUseCase: GetAllReadingsUseCase,
    private val saveReadingUseCase: SaveReadingUseCase,
    private val analyzeSpreadFromImageUseCase: AnalyzeSpreadFromImageUseCase,
    private val generateReadingUseCase: GenerateReadingUseCase,
    private val calculateEigenvalueUseCase: CalculateEigenvalueUseCase
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow<TarotUiState>(TarotUiState.Loading)
    val uiState: StateFlow<TarotUiState> = _uiState.asStateFlow()
    
    // Decks state
    private val _decks = MutableStateFlow<List<TarotDeck>>(emptyList())
    val decks: StateFlow<List<TarotDeck>> = _decks.asStateFlow()
    
    // Spreads state
    private val _spreads = MutableStateFlow<List<TarotSpread>>(emptyList())
    val spreads: StateFlow<List<TarotSpread>> = _spreads.asStateFlow()
    
    // Cards state
    private val _cards = MutableStateFlow<List<TarotCard>>(emptyList())
    val cards: StateFlow<List<TarotCard>> = _cards.asStateFlow()
    
    // Readings state
    private val _readings = MutableStateFlow<List<Reading>>(emptyList())
    val readings: StateFlow<List<Reading>> = _readings.asStateFlow()
    
    // Current reading state
    private val _currentReading = MutableStateFlow<Reading?>(null)
    val currentReading: StateFlow<Reading?> = _currentReading.asStateFlow()
    
    // Selected deck state
    private val _selectedDeck = MutableStateFlow<TarotDeck?>(null)
    val selectedDeck: StateFlow<TarotDeck?> = _selectedDeck.asStateFlow()
    
    // Selected spread state
    private val _selectedSpread = MutableStateFlow<TarotSpread?>(null)
    val selectedSpread: StateFlow<TarotSpread?> = _selectedSpread.asStateFlow()
    
    init {
        loadInitialData()
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                _uiState.value = TarotUiState.Loading
                
                // Load all data
                launch { loadDecks() }
                launch { loadSpreads() }
                launch { loadCards() }
                launch { loadReadings() }
                
                _uiState.value = TarotUiState.Success
            } catch (e: Exception) {
                _uiState.value = TarotUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
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
    
    private fun loadReadings() {
        viewModelScope.launch {
            getAllReadingsUseCase().collect { readingList ->
                _readings.value = readingList
            }
        }
    }
    
    fun selectDeck(deckId: String) {
        viewModelScope.launch {
            try {
                val deck = getDeckByIdUseCase(deckId)
                _selectedDeck.value = deck
                
                // Load cards for this deck
                if (deck != null) {
                    getCardsByDeckUseCase(deckId).collect { cardList ->
                        _cards.value = cardList
                    }
                }
            } catch (e: Exception) {
                _uiState.value = TarotUiState.Error("Failed to select deck: ${e.message}")
            }
        }
    }
    
    fun selectSpread(spreadId: String) {
        viewModelScope.launch {
            try {
                val spread = getSpreadByIdUseCase(spreadId)
                _selectedSpread.value = spread
            } catch (e: Exception) {
                _uiState.value = TarotUiState.Error("Failed to select spread: ${e.message}")
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
            try {
                _uiState.value = TarotUiState.Loading
                
                val deck = getDeckByIdUseCase(deckId)
                if (deck != null) {
                    val reading = analyzeSpreadFromImageUseCase(bitmap, deck)
                    _currentReading.value = reading
                    saveReadingUseCase(reading)
                    _uiState.value = TarotUiState.Success
                } else {
                    _uiState.value = TarotUiState.Error("Deck not found")
                }
            } catch (e: Exception) {
                _uiState.value = TarotUiState.Error("Failed to analyze image: ${e.message}")
            }
        }
    }
    
    fun generateReading(
        deckId: String, 
        spreadId: String, 
        cardDrawings: List<CardDrawing>,
        question: String? = null
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = TarotUiState.Loading
                
                val reading = generateReadingUseCase(deckId, spreadId, cardDrawings, question)
                _currentReading.value = reading
                saveReadingUseCase(reading)
                
                _uiState.value = TarotUiState.Success
            } catch (e: Exception) {
                _uiState.value = TarotUiState.Error("Failed to generate reading: ${e.message}")
            }
        }
    }
    
    fun calculateEigenvalue(cardDrawings: List<CardDrawing>): Double {
        return try {
            calculateEigenvalueUseCase(
                cardDrawings = cardDrawings,
                cards = _cards.value,
                positions = _selectedSpread.value?.positions ?: emptyList()
            )
        } catch (e: Exception) {
            0.0
        }
    }
    
    fun saveReading(reading: Reading) {
        viewModelScope.launch {
            try {
                saveReadingUseCase(reading)
                _uiState.value = TarotUiState.Success
            } catch (e: Exception) {
                _uiState.value = TarotUiState.Error("Failed to save reading: ${e.message}")
            }
        }
    }
    
    fun clearCurrentReading() {
        _currentReading.value = null
    }
    
    fun shuffleCards(): List<TarotCard> {
        return _cards.value.shuffled()
    }
    
    fun drawCards(count: Int): List<TarotCard> {
        return _cards.value.shuffled().take(count)
    }
}

sealed class TarotUiState {
    object Loading : TarotUiState()
    object Success : TarotUiState()
    data class Error(val message: String) : TarotUiState()
}