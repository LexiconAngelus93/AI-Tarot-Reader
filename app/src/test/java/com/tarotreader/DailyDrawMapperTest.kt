package com.tarotreader

import com.tarotreader.data.database.DailyDrawEntity
import com.tarotreader.data.database.DailyDrawMapper
import com.tarotreader.domain.model.CardOrientation
import com.tarotreader.domain.model.DailyDraw
import com.tarotreader.domain.model.TarotCard
import com.tarotreader.domain.model.TarotDeck
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class DailyDrawMapperTest {
    
    @Test
    fun `test toEntity conversion`() {
        val dailyDraw = DailyDraw(
            id = 1,
            date = LocalDate.of(2025, 1, 1),
            card = TarotCard(
                id = "card_1",
                name = "The Fool",
                meaning = "New beginnings, innocence, spontaneity",
                description = "The Fool represents new beginnings, having faith in the future, and taking risks.",
                deckId = "deck_1"
            ),
            deck = TarotDeck(
                id = "deck_1",
                name = "Rider-Waite",
                description = "The classic Rider-Waite deck"
            ),
            orientation = CardOrientation.REVERSED,
            reflection = "Today I feel like taking a new path"
        )
        
        val entity = DailyDrawMapper.toEntity(dailyDraw)
        
        assertEquals(1, entity.id)
        assertEquals(LocalDate.of(2025, 1, 1), entity.date)
        assertEquals("card_1", entity.cardId)
        assertEquals("deck_1", entity.deckId)
        assertEquals("REVERSED", entity.orientation)
        assertEquals("Today I feel like taking a new path", entity.reflection)
    }
    
    @Test
    fun `test fromEntity conversion`() {
        val entity = DailyDrawEntity(
            id = 1,
            date = LocalDate.of(2025, 1, 1),
            cardId = "card_1",
            deckId = "deck_1",
            orientation = "UPRIGHT",
            reflection = "Today I feel like taking a new path"
        )
        
        val card = TarotCard(
            id = "card_1",
            name = "The Fool",
            meaning = "New beginnings, innocence, spontaneity",
            description = "The Fool represents new beginnings, having faith in the future, and taking risks.",
            deckId = "deck_1"
        )
        
        val deck = TarotDeck(
            id = "deck_1",
            name = "Rider-Waite",
            description = "The classic Rider-Waite deck"
        )
        
        val dailyDraw = DailyDrawMapper.fromEntity(entity, card, deck)
        
        assertEquals(1, dailyDraw.id)
        assertEquals(LocalDate.of(2025, 1, 1), dailyDraw.date)
        assertEquals(card, dailyDraw.card)
        assertEquals(deck, dailyDraw.deck)
        assertEquals(CardOrientation.UPRIGHT, dailyDraw.orientation)
        assertEquals("Today I feel like taking a new path", dailyDraw.reflection)
    }
    
    @Test
    fun `test fromEntity with invalid orientation defaults to upright`() {
        val entity = DailyDrawEntity(
            id = 1,
            date = LocalDate.of(2025, 1, 1),
            cardId = "card_1",
            deckId = "deck_1",
            orientation = "INVALID",
            reflection = "Today I feel like taking a new path"
        )
        
        val card = TarotCard(
            id = "card_1",
            name = "The Fool",
            meaning = "New beginnings, innocence, spontaneity",
            description = "The Fool represents new beginnings, having faith in the future, and taking risks.",
            deckId = "deck_1"
        )
        
        val deck = TarotDeck(
            id = "deck_1",
            name = "Rider-Waite",
            description = "The classic Rider-Waite deck"
        )
        
        val dailyDraw = DailyDrawMapper.fromEntity(entity, card, deck)
        
        assertEquals(CardOrientation.UPRIGHT, dailyDraw.orientation)
    }
}