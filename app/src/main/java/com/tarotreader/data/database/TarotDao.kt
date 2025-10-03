package com.tarotreader.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TarotDao {
    
    // Tarot Card operations
    @Query("SELECT * FROM tarot_cards")
    fun getAllCards(): Flow<List<TarotCardEntity>>
    
    @Query("SELECT * FROM tarot_cards WHERE deckId = :deckId")
    fun getCardsByDeck(deckId: String): Flow<List<TarotCardEntity>>
    
    @Query("SELECT * FROM tarot_cards WHERE id = :cardId")
    suspend fun getCardById(cardId: String): TarotCardEntity?
    
    @Query("SELECT * FROM tarot_cards WHERE name LIKE '%' || :query || '%' OR uprightKeywords LIKE '%' || :query || '%'")
    suspend fun searchCards(query: String): List<TarotCardEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: TarotCardEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<TarotCardEntity>)
    
    @Update
    suspend fun updateCard(card: TarotCardEntity)
    
    @Delete
    suspend fun deleteCard(card: TarotCardEntity)
    
    // Tarot Deck operations
    @Query("SELECT * FROM tarot_decks")
    fun getAllDecks(): Flow<List<TarotDeckEntity>>
    
    @Query("SELECT * FROM tarot_decks WHERE id = :deckId")
    suspend fun getDeckById(deckId: String): TarotDeckEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: TarotDeckEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDecks(decks: List<TarotDeckEntity>)
    
    @Update
    suspend fun updateDeck(deck: TarotDeckEntity)
    
    @Delete
    suspend fun deleteDeck(deck: TarotDeckEntity)
    
    // Tarot Spread operations
    @Query("SELECT * FROM tarot_spreads")
    fun getAllSpreads(): Flow<List<TarotSpreadEntity>>
    
    @Query("SELECT * FROM tarot_spreads WHERE id = :spreadId")
    suspend fun getSpreadById(spreadId: String): TarotSpreadEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpread(spread: TarotSpreadEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpreads(spreads: List<TarotSpreadEntity>)
    
    @Update
    suspend fun updateSpread(spread: TarotSpreadEntity)
    
    @Delete
    suspend fun deleteSpread(spread: TarotSpreadEntity)
    
    // Spread Position operations
    @Query("SELECT * FROM spread_positions WHERE spreadId = :spreadId ORDER BY positionOrder")
    suspend fun getPositionsForSpread(spreadId: String): List<SpreadPositionEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpreadPosition(position: SpreadPositionEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpreadPositions(positions: List<SpreadPositionEntity>)
    
    @Update
    suspend fun updateSpreadPosition(position: SpreadPositionEntity)
    
    @Delete
    suspend fun deleteSpreadPosition(position: SpreadPositionEntity)
    
    @Query("DELETE FROM spread_positions WHERE spreadId = :spreadId")
    suspend fun deletePositionsForSpread(spreadId: String)
    
    // Reading operations
    @Query("SELECT * FROM readings ORDER BY date DESC")
    fun getAllReadings(): Flow<List<ReadingEntity>>
    
    @Query("SELECT * FROM readings WHERE id = :readingId")
    suspend fun getReadingById(readingId: String): ReadingEntity?
    
    @Query("SELECT * FROM readings WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getReadingsByDateRange(startDate: Long, endDate: Long): List<ReadingEntity>
    
    @Query("SELECT * FROM readings WHERE deckId = :deckId ORDER BY date DESC")
    suspend fun getReadingsByDeck(deckId: String): List<ReadingEntity>
    
    @Query("SELECT * FROM readings WHERE spreadId = :spreadId ORDER BY date DESC")
    suspend fun getReadingsBySpread(spreadId: String): List<ReadingEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: ReadingEntity)
    
    @Update
    suspend fun updateReading(reading: ReadingEntity)
    
    @Delete
    suspend fun deleteReading(reading: ReadingEntity)
    
    // Card Drawing operations
    @Query("SELECT * FROM card_drawings WHERE readingId = :readingId")
    suspend fun getCardDrawingsForReading(readingId: String): List<CardDrawingEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardDrawing(cardDrawing: CardDrawingEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardDrawings(cardDrawings: List<CardDrawingEntity>)
    
    @Delete
    suspend fun deleteCardDrawing(cardDrawing: CardDrawingEntity)
    
    @Query("DELETE FROM card_drawings WHERE readingId = :readingId")
    suspend fun deleteCardDrawingsForReading(readingId: String)
}