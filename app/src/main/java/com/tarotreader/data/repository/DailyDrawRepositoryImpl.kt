package com.tarotreader.data.repository

import com.tarotreader.data.database.DailyDrawEntity
import com.tarotreader.data.database.DailyDrawMapper
import com.tarotreader.data.database.TarotDao
import com.tarotreader.domain.model.CardOrientation
import com.tarotreader.domain.model.DailyDraw
import com.tarotreader.domain.repository.DailyDrawRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class DailyDrawRepositoryImpl(
    private val dao: TarotDao
) : DailyDrawRepository {
    
    override suspend fun getDailyDrawByDate(date: LocalDate): DailyDraw? {
        val dailyDrawEntity = dao.getDailyDrawByDate(date.toString())
        return dailyDrawEntity?.let { entity ->
            val card = dao.getCardById(entity.cardId)?.toTarotCard()
            val deck = dao.getDeckById(entity.deckId)?.toTarotDeck()
            if (card != null && deck != null) {
                DailyDrawMapper.fromEntity(entity, card, deck)
            } else {
                null
            }
        }
    }
    
    override fun getRecentDailyDraws(): Flow<List<DailyDraw>> {
        return dao.getRecentDailyDraws().map { entities ->
            entities.mapNotNull { entity ->
                val card = dao.getCardById(entity.cardId)?.toTarotCard()
                val deck = dao.getDeckById(entity.deckId)?.toTarotDeck()
                if (card != null && deck != null) {
                    DailyDrawMapper.fromEntity(entity, card, deck)
                } else {
                    null
                }
            }
        }
    }
    
    override suspend fun saveDailyDraw(dailyDraw: DailyDraw) {
        val dailyDrawEntity = DailyDrawMapper.toEntity(dailyDraw)
        dao.insertDailyDraw(dailyDrawEntity)
    }
    
    override suspend fun updateDailyDraw(dailyDraw: DailyDraw) {
        val dailyDrawEntity = DailyDrawMapper.toEntity(dailyDraw)
        dao.updateDailyDraw(dailyDrawEntity)
    }
    
    override suspend fun deleteDailyDraw(dailyDraw: DailyDraw) {
        val dailyDrawEntity = DailyDrawMapper.toEntity(dailyDraw)
        dao.deleteDailyDraw(dailyDrawEntity)
    }
}