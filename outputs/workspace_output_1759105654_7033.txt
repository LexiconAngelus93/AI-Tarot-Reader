package com.tarotreader.data.database

import androidx.room.*
import com.tarotreader.data.model.*
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
    @Query("SELECT * FROM spread_positions WHERE spreadId = :spreadId")
    fun getPositionsBySpread(spreadId: String): Flow<List<SpreadPositionEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosition(position: SpreadPositionEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPositions(positions: List<SpreadPositionEntity>)
    
    @Update
    suspend fun updatePosition(position: SpreadPositionEntity)
    
    @Delete
    suspend fun deletePosition(position: SpreadPositionEntity)
    
    // Reading operations
    @Query("SELECT * FROM readings ORDER BY date DESC")
    fun getAllReadings(): Flow<List<ReadingEntity>>
    
    @Query("SELECT * FROM readings WHERE id = :readingId")
    suspend fun getReadingById(readingId: String): ReadingEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: ReadingEntity)
    
    @Update
    suspend fun updateReading(reading: ReadingEntity)
    
    @Delete
    suspend fun deleteReading(reading: ReadingEntity)
    
    // Card Drawing operations
    @Query("SELECT * FROM card_drawings WHERE readingId = :readingId")
    fun getCardDrawingsByReading(readingId: String): Flow<List<CardDrawingEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardDrawing(cardDrawing: CardDrawingEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardDrawings(cardDrawings: List<CardDrawingEntity>)
    
    @Delete
    suspend fun deleteCardDrawings(cardDrawings: List<CardDrawingEntity>)
}