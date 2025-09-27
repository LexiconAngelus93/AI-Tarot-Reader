package com.tarotreader.data.repository

import com.tarotreader.data.database.TarotDao
import com.tarotreader.data.model.*
import kotlinx.coroutines.flow.Flow

class TarotRepository(private val dao: TarotDao) {
    
    // Card operations
    fun getAllCards(): Flow<List<TarotCard>> {
        return dao.getAllCards()
    }
    
    fun getCardsByDeck(deckId: String): Flow<List<TarotCard>> {
        return dao.getCardsByDeck(deckId)
    }
    
    suspend fun getCardById(cardId: String): TarotCard? {
        return dao.getCardById(cardId)?.toTarotCard()
    }
    
    suspend fun insertCard(card: TarotCard) {
        dao.insertCard(card.toTarotCardEntity())
    }
    
    suspend fun insertCards(cards: List<TarotCard>) {
        dao.insertCards(cards.map { it.toTarotCardEntity() })
    }
    
    // Deck operations
    fun getAllDecks(): Flow<List<TarotDeck>> {
        return dao.getAllDecks()
    }
    
    suspend fun getDeckById(deckId: String): TarotDeck? {
        return dao.getDeckById(deckId)?.toTarotDeck()
    }
    
    suspend fun insertDeck(deck: TarotDeck) {
        dao.insertDeck(deck.toTarotDeckEntity())
    }
    
    suspend fun insertDecks(decks: List<TarotDeck>) {
        dao.insertDecks(decks.map { it.toTarotDeckEntity() })
    }
    
    // Spread operations
    fun getAllSpreads(): Flow<List<TarotSpread>> {
        return dao.getAllSpreads()
    }
    
    suspend fun getSpreadById(spreadId: String): TarotSpread? {
        return dao.getSpreadById(spreadId)?.toTarotSpread()
    }
    
    suspend fun insertSpread(spread: TarotSpread) {
        dao.insertSpread(spread.toTarotSpreadEntity())
    }
    
    suspend fun insertSpreads(spreads: List<TarotSpread>) {
        dao.insertSpreads(spreads.map { it.toTarotSpreadEntity() })
    }
    
    // Reading operations
    fun getAllReadings(): Flow<List<Reading>> {
        return dao.getAllReadings()
    }
    
    suspend fun getReadingById(readingId: String): Reading? {
        return dao.getReadingById(readingId)?.toReading()
    }
    
    suspend fun insertReading(reading: Reading) {
        dao.insertReading(reading.toReadingEntity())
        dao.insertCardDrawings(reading.cardDrawings.map { it.toCardDrawingEntity(reading.id) })
    }
}