package com.tarotreader.data.database

import com.tarotreader.data.provider.TarotDataProvider
import com.tarotreader.data.provider.MinorArcanaProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseInitializer(private val tarotDao: TarotDao) {
    
    fun initializeDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            // Check if database is already populated
            val deckCount = tarotDao.getAllDecks().count()
            if (deckCount == 0) {
                // Populate with initial data
                populateInitialData()
            }
        }
    }
    
    private suspend fun populateInitialData() {
        // Insert Rider-Waite deck
        val riderWaiteDeck = TarotDataProvider.getRiderWaiteDeck().toTarotDeckEntity()
        tarotDao.insertDeck(riderWaiteDeck)
        
        // Insert major arcana cards for Rider-Waite deck
        val majorArcanaCards = TarotDataProvider.getMajorArcanaCards("rider_waite")
        tarotDao.insertCards(majorArcanaCards.map { it.toTarotCardEntity() })
        
        // Insert minor arcana cards for Rider-Waite deck
        val minorArcanaCards = MinorArcanaProvider.getSuitsCards("rider_waite")
        tarotDao.insertCards(minorArcanaCards.map { it.toTarotCardEntity() })
        
        // Insert popular spreads
        val celticCrossSpread = TarotDataProvider.getCelticCrossSpread().toTarotSpreadEntity()
        tarotDao.insertSpread(celticCrossSpread)
        
        val threeCardSpread = TarotDataProvider.getThreeCardSpread().toTarotSpreadEntity()
        tarotDao.insertSpread(threeCardSpread)
        
        val horseshoeSpread = TarotDataProvider.getHorseshoeSpread().toTarotSpreadEntity()
        tarotDao.insertSpread(horseshoeSpread)
        
        val dailyDrawSpread = TarotDataProvider.getDailyDrawSpread().toTarotSpreadEntity()
        tarotDao.insertSpread(dailyDrawSpread)
        
        // Insert spread positions
        tarotDao.insertPositions(TarotDataProvider.getCelticCrossSpread().positions.map { it.toSpreadPositionEntity() })
        tarotDao.insertPositions(TarotDataProvider.getThreeCardSpread().positions.map { it.toSpreadPositionEntity() })
        tarotDao.insertPositions(TarotDataProvider.getHorseshoeSpread().positions.map { it.toSpreadPositionEntity() })
        tarotDao.insertPositions(TarotDataProvider.getDailyDrawSpread().positions.map { it.toSpreadPositionEntity() })
    }
}