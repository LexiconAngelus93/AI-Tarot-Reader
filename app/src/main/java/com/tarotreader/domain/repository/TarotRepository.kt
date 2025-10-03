package com.tarotreader.domain.repository

import com.tarotreader.domain.model.*
import kotlinx.coroutines.flow.Flow

interface TarotRepository {
    // Card operations
    fun getAllCards(): Flow<List<TarotCard>>
    fun getCardsByDeck(deckId: String): Flow<List<TarotCard>>
    suspend fun getCardById(cardId: String): TarotCard?
    suspend fun insertCard(card: TarotCard)
    suspend fun insertCards(cards: List<TarotCard>)
    suspend fun updateCard(card: TarotCard)
    suspend fun deleteCard(card: TarotCard)
    suspend fun searchCards(query: String): List<TarotCard>
    
    // Deck operations
    fun getAllDecks(): Flow<List<TarotDeck>>
    suspend fun getDeckById(deckId: String): TarotDeck?
    suspend fun insertDeck(deck: TarotDeck)
    suspend fun insertDecks(decks: List<TarotDeck>)
    suspend fun updateDeck(deck: TarotDeck)
    suspend fun deleteDeck(deck: TarotDeck)
    
    // Spread operations
    fun getAllSpreads(): Flow<List<TarotSpread>>
    suspend fun getSpreadById(spreadId: String): TarotSpread?
    suspend fun insertSpread(spread: TarotSpread)
    suspend fun insertSpreads(spreads: List<TarotSpread>)
    suspend fun updateSpread(spread: TarotSpread)
    suspend fun deleteSpread(spread: TarotSpread)
    
    // Reading operations
    fun getAllReadings(): Flow<List<Reading>>
    suspend fun getReadingById(readingId: String): Reading?
    suspend fun insertReading(reading: Reading)
    suspend fun updateReading(reading: Reading)
    suspend fun deleteReading(reading: Reading)
    suspend fun getReadingsByDateRange(startDate: Long, endDate: Long): List<Reading>
    suspend fun getReadingsByDeck(deckId: String): List<Reading>
    suspend fun getReadingsBySpread(spreadId: String): List<Reading>
    
    // Utility operations
    suspend fun initializeDatabase()
    suspend fun getDatabaseSize(): Long
    suspend fun clearAllData()
}