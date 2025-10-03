package com.tarotreader.data.repository

import com.tarotreader.data.database.TarotDao
import com.tarotreader.data.database.TarotDatabase
import com.tarotreader.data.database.toEntity
import com.tarotreader.data.database.toDomain
import com.tarotreader.domain.model.Reading
import com.tarotreader.domain.model.TarotCard
import com.tarotreader.domain.model.TarotDeck
import com.tarotreader.domain.model.TarotSpread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TarotRepository @Inject constructor(
    private val tarotDao: TarotDao,
    private val database: TarotDatabase
) {
    // Decks
    fun getAllDecks(): Flow<List<TarotDeck>> = 
        tarotDao.getAllDecks().map { entities -> 
            entities.map { it.toDomain() } 
        }
    
    suspend fun getDeckById(deckId: String): TarotDeck? = 
        tarotDao.getDeckById(deckId)?.toDomain()
    
    suspend fun insertDeck(deck: TarotDeck) = 
        tarotDao.insertDeck(deck.toEntity())
    
    suspend fun insertDecks(decks: List<TarotDeck>) = 
        tarotDao.insertDecks(decks.map { it.toEntity() })
    
    suspend fun updateDeck(deck: TarotDeck) = 
        tarotDao.updateDeck(deck.toEntity())
    
    suspend fun deleteDeck(deck: TarotDeck) = 
        tarotDao.deleteDeck(deck.toEntity())
    
    // Cards
    fun getAllCards(): Flow<List<TarotCard>> = 
        tarotDao.getAllCards().map { entities -> 
            entities.map { it.toDomain() } 
        }
    
    fun getCardsByDeck(deckId: String): Flow<List<TarotCard>> = 
        tarotDao.getCardsByDeck(deckId).map { entities -> 
            entities.map { it.toDomain() } 
        }
    
    suspend fun getCardById(cardId: String): TarotCard? = 
        tarotDao.getCardById(cardId)?.toDomain()
    
    suspend fun insertCard(card: TarotCard) = 
        tarotDao.insertCard(card.toEntity())
    
    suspend fun insertCards(cards: List<TarotCard>) = 
        tarotDao.insertCards(cards.map { it.toEntity() })
    
    suspend fun updateCard(card: TarotCard) = 
        tarotDao.updateCard(card.toEntity())
    
    suspend fun deleteCard(card: TarotCard) = 
        tarotDao.deleteCard(card.toEntity())
    
    suspend fun searchCards(query: String): List<TarotCard> = 
        tarotDao.searchCards(query).map { it.toDomain() }
    
    // Spreads
    fun getAllSpreads(): Flow<List<TarotSpread>> = 
        tarotDao.getAllSpreads().map { entities -> 
            entities.map { entity ->
                val positions = tarotDao.getPositionsForSpread(entity.id).map { it.toDomain() }
                entity.toDomain(positions)
            }
        }
    
    suspend fun getSpreadById(spreadId: String): TarotSpread? {
        val entity = tarotDao.getSpreadById(spreadId) ?: return null
        val positions = tarotDao.getPositionsForSpread(spreadId).map { it.toDomain() }
        return entity.toDomain(positions)
    }
    
    suspend fun insertSpread(spread: TarotSpread) {
        tarotDao.insertSpread(spread.toEntity())
        spread.positions.forEach { position ->
            tarotDao.insertSpreadPosition(position.toEntity(spread.id))
        }
    }
    
    suspend fun insertSpreads(spreads: List<TarotSpread>) {
        spreads.forEach { spread ->
            tarotDao.insertSpread(spread.toEntity())
            spread.positions.forEach { position ->
                tarotDao.insertSpreadPosition(position.toEntity(spread.id))
            }
        }
    }
    
    suspend fun updateSpread(spread: TarotSpread) {
        tarotDao.updateSpread(spread.toEntity())
        // Delete old positions and insert new ones
        tarotDao.deletePositionsForSpread(spread.id)
        spread.positions.forEach { position ->
            tarotDao.insertSpreadPosition(position.toEntity(spread.id))
        }
    }
    
    suspend fun deleteSpread(spread: TarotSpread) {
        tarotDao.deletePositionsForSpread(spread.id)
        tarotDao.deleteSpread(spread.toEntity())
    }
    
    // Readings
    fun getAllReadings(): Flow<List<Reading>> = 
        tarotDao.getAllReadings().map { entities -> 
            entities.map { entity ->
                val cardDrawings = tarotDao.getCardDrawingsForReading(entity.id).map { it.toDomain() }
                entity.toDomain(cardDrawings)
            }
        }
    
    suspend fun getReadingById(readingId: String): Reading? {
        val entity = tarotDao.getReadingById(readingId) ?: return null
        val cardDrawings = tarotDao.getCardDrawingsForReading(readingId).map { it.toDomain() }
        return entity.toDomain(cardDrawings)
    }
    
    suspend fun insertReading(reading: Reading) {
        tarotDao.insertReading(reading.toEntity())
        reading.cardDrawings.forEach { drawing ->
            tarotDao.insertCardDrawing(drawing.toEntity(reading.id))
        }
    }
    
    suspend fun updateReading(reading: Reading) {
        tarotDao.updateReading(reading.toEntity())
        // Update card drawings
        tarotDao.deleteCardDrawingsForReading(reading.id)
        reading.cardDrawings.forEach { drawing ->
            tarotDao.insertCardDrawing(drawing.toEntity(reading.id))
        }
    }
    
    suspend fun deleteReading(reading: Reading) {
        tarotDao.deleteCardDrawingsForReading(reading.id)
        tarotDao.deleteReading(reading.toEntity())
    }
    
    suspend fun getReadingsByDateRange(startDate: Long, endDate: Long): List<Reading> {
        return tarotDao.getReadingsByDateRange(startDate, endDate).map { entity ->
            val cardDrawings = tarotDao.getCardDrawingsForReading(entity.id).map { it.toDomain() }
            entity.toDomain(cardDrawings)
        }
    }
    
    suspend fun getReadingsByDeck(deckId: String): List<Reading> {
        return tarotDao.getReadingsByDeck(deckId).map { entity ->
            val cardDrawings = tarotDao.getCardDrawingsForReading(entity.id).map { it.toDomain() }
            entity.toDomain(cardDrawings)
        }
    }
    
    suspend fun getReadingsBySpread(spreadId: String): List<Reading> {
        return tarotDao.getReadingsBySpread(spreadId).map { entity ->
            val cardDrawings = tarotDao.getCardDrawingsForReading(entity.id).map { it.toDomain() }
            entity.toDomain(cardDrawings)
        }
    }
    
    // Database initialization
    suspend fun initializeDatabase() {
        database.clearAllTables()
        // Database will be initialized by DatabaseInitializer
    }
    
    // Utility functions
    suspend fun getDatabaseSize(): Long {
        return database.openHelper.writableDatabase.pageSize * 
               database.openHelper.writableDatabase.pageCount
    }
    
    suspend fun clearAllData() {
        database.clearAllTables()
    }
}