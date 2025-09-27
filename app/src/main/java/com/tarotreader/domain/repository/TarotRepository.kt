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
    
    // Deck operations
    fun getAllDecks(): Flow<List<TarotDeck>>
    suspend fun getDeckById(deckId: String): TarotDeck?
    suspend fun insertDeck(deck: TarotDeck)
    suspend fun insertDecks(decks: List<TarotDeck>)
    
    // Spread operations
    fun getAllSpreads(): Flow<List<TarotSpread>>
    suspend fun getSpreadById(spreadId: String): TarotSpread?
    suspend fun insertSpread(spread: TarotSpread)
    suspend fun insertSpreads(spreads: List<TarotSpread>)
    
    // Reading operations
    fun getAllReadings(): Flow<List<Reading>>
    suspend fun getReadingById(readingId: String): Reading?
    suspend fun insertReading(reading: Reading)
}