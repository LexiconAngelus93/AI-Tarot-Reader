package com.tarotreader.presentation.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarotreader.domain.model.CardDrawing
import com.tarotreader.domain.model.DailyDraw
import com.tarotreader.domain.model.UserPreferences
import com.tarotreader.domain.usecase.*
import com.tarotreader.utils.PerformanceOptimizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

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
    private val calculateEigenValueUseCase: CalculateEigenvalueUseCase,
    private val getDailyDrawUseCase: GetDailyDrawUseCase,
    private val getRecentDailyDrawsUseCase: GetRecentDailyDrawsUseCase,
    private val saveDailyDrawUseCase: SaveDailyDrawUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val saveUserPreferencesUseCase: SaveUserPreferencesUseCase,
    private val updateUserPreferencesUseCase: UpdateUserPreferencesUseCase
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
    
    // Daily Draw state
    private val _currentDailyDraw = MutableStateFlow<com.tarotreader.domain.model.DailyDraw?>(null)
    val currentDailyDraw: StateFlow<com.tarotreader.domain.model.DailyDraw?> = _currentDailyDraw.asStateFlow()
    
    private val _recentDailyDraws = MutableStateFlow<List<com.tarotreader.domain.model.DailyDraw>>(emptyList())
    val recentDailyDraws: StateFlow<List<com.tarotreader.domain.model.DailyDraw>> = _recentDailyDraws.asStateFlow()
    
    // User Preferences state
    private val _userPreferences = MutableStateFlow<com.tarotreader.domain.model.UserPreferences?>(null)
    val userPreferences: StateFlow<com.tarotreader.domain.model.UserPreferences?> = _userPreferences.asStateFlow()
    
    init {
        loadDecks()
        loadSpreads()
        loadCards()
        loadRecentDailyDraws()
        loadUserPreferences()
        PerformanceOptimizer.optimizeDatabaseQueries()
        PerformanceOptimizer.optimizeAIModelLoading()
    }
    
    private fun loadUserPreferences() {
        viewModelScope.launch {
            try {
                _userPreferences.value = getUserPreferencesUseCase() ?: UserPreferences()
            } catch (e: Exception) {
                // Handle error appropriately
            }
        }
    }
    
    private fun loadRecentDailyDraws() {
        viewModelScope.launch {
            getRecentDailyDrawsUseCase().collect { dailyDrawList ->
                _recentDailyDraws.value = dailyDrawList
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
    
    fun generateDailyDraw(deckId: String) {
        viewModelScope.launch {
            try {
                val today = LocalDate.now()
                val existingDraw = getDailyDrawUseCase(today)
                
                if (existingDraw != null) {
                    _currentDailyDraw.value = existingDraw
                } else {
                    val deck = getDeckByIdUseCase(deckId)
                    if (deck != null) {
                        val cards = getCardsByDeckUseCase(deckId)
                        if (cards.isNotEmpty()) {
                            val randomCard = cards.shuffled().first()
                            
                            val orientation = if (System.currentTimeMillis() % 2 == 0) {
                                com.tarotreader.domain.model.CardOrientation.UPRIGHT
                            } else {
                                com.tarotreader.domain.model.CardOrientation.REVERSED
                            }
                            
                            val dailyDraw = DailyDraw(
                                date = today,
                                card = randomCard,
                                deck = deck,
                                orientation = orientation
                            )
                            
                            _currentDailyDraw.value = dailyDraw
                            saveDailyDrawUseCase(dailyDraw)
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle error appropriately
            }
        }
    }
    
    fun saveDailyDrawReflection(dailyDraw: DailyDraw, reflection: String) {
        viewModelScope.launch {
            val updatedDraw = dailyDraw.copy(reflection = reflection)
            _currentDailyDraw.value = updatedDraw
            saveDailyDrawUseCase(updatedDraw)
        }
    }
    
    fun updateUserPreferences(preferences: UserPreferences) {
        viewModelScope.launch {
            _userPreferences.value = preferences
            updateUserPreferencesUseCase(preferences)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        PerformanceOptimizer.recycleBitmaps()
    }
}