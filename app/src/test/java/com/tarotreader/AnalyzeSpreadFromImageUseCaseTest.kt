package com.tarotreader

import com.tarotreader.domain.usecase.AnalyzeSpreadFromImageUseCase
import com.tarotreader.domain.repository.TarotRepository
import com.tarotreader.domain.model.TarotCard
import com.tarotreader.domain.model.TarotDeck
import com.tarotreader.domain.model.TarotSpread
import com.tarotreader.domain.model.CardDrawing
import com.tarotreader.domain.model.SpreadPosition
import android.graphics.Bitmap
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AnalyzeSpreadFromImageUseCaseTest {
    
    @Test
    fun testAnalyzeSpreadFromImage() = runBlocking {
        // Create mock repository
        val repository = object : TarotRepository {
            override suspend fun getAllCards(): List<TarotCard> {
                return listOf(
                    TarotCard(
                        id = "0_rider_waite",
                        name = "The Fool",
                        uprightMeaning = "New beginnings, innocence, spontaneity",
                        reversedMeaning = "Recklessness, taken advantage of, inconsideration",
                        description = "The Fool represents new beginnings, having faith in the future, being inexperienced, not knowing what to expect, having beginner's luck, improvising, believing in yourself and your abilities, being carefree, and having a free spirit.",
                        keywords = listOf("beginnings", "innocence", "spontaneity", "faith"),
                        numerology = "0",
                        element = null,
                        astrology = "Uranus",
                        cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/38/RWS_Tarot_00_Fool.jpg/200px-RWS_Tarot_00_Fool.jpg",
                        deckId = "rider_waite"
                    ),
                    TarotCard(
                        id = "1_rider_waite",
                        name = "The Magician",
                        uprightMeaning = "Manifestation, resourcefulness, power",
                        reversedMeaning = "Manipulation, poor planning, untapped talents",
                        description = "The Magician represents manifestation, resourcefulness, and power. The Magician shows you that you have all the tools and resources you need to make your dreams come true. It is a card of action, desires, and manifestation.",
                        keywords = listOf("manifestation", "power", "resourcefulness", "action"),
                        numerology = "1",
                        element = null,
                        astrology = "Mercury",
                        cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/RWS_Tarot_01_Magician.jpg/200px-RWS_Tarot_01_Magician.jpg",
                        deckId = "rider_waite"
                    ),
                    TarotCard(
                        id = "2_rider_waite",
                        name = "The High Priestess",
                        uprightMeaning = "Intuition, unconscious knowledge, divine feminine",
                        reversedMeaning = "Secrets, disconnected from intuition, withdrawal",
                        description = "The High Priestess represents intuition, unconscious knowledge, and the divine feminine. She is the guardian of the unconscious mind and represents our ability to access hidden knowledge and intuition.",
                        keywords = listOf("intuition", "mystery", "unconscious", "divine feminine"),
                        numerology = "2",
                        element = null,
                        astrology = "Moon",
                        cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/88/RWS_Tarot_02_High_Priestess.jpg/200px-RWS_Tarot_02_High_Priestess.jpg",
                        deckId = "rider_waite"
                    )
                )
            }
            
            override suspend fun getCardsByDeck(deckId: String): List<TarotCard> = emptyList()
            
            override suspend fun getAllDecks(): List<TarotDeck> = emptyList()
            
            override suspend fun getDeckById(deckId: String): TarotDeck? = null
            
            override suspend fun getAllSpreads(): List<TarotSpread> = emptyList()
            
            override suspend fun getSpreadById(spreadId: String): TarotSpread? {
                return TarotSpread(
                    id = "three_card",
                    name = "Three Card Spread",
                    description = "A simple yet powerful spread representing the past, present, and future. Perfect for gaining insight into a situation's progression.",
                    positions = listOf(
                        SpreadPosition(
                            id = "1_three_card",
                            spreadId = "three_card",
                            positionIndex = 0,
                            name = "Past",
                            meaning = "What led you to this moment",
                            x = 0.3f,
                            y = 0.5f
                        ),
                        SpreadPosition(
                            id = "2_three_card",
                            spreadId = "three_card",
                            positionIndex = 1,
                            name = "Present",
                            meaning = "Your current situation",
                            x = 0.5f,
                            y = 0.5f
                        ),
                        SpreadPosition(
                            id = "3_three_card",
                            spreadId = "three_card",
                            positionIndex = 2,
                            name = "Future",
                            meaning = "What is likely to come",
                            x = 0.7f,
                            y = 0.5f
                        )
                    ),
                    imageUrl = "https://example.com/three-card-spread.jpg",
                    isCustom = false
                )
            }
            
            override suspend fun saveReading(reading: com.tarotreader.domain.model.Reading) {}
            
            override suspend fun getAllReadings(): List<com.tarotreader.domain.model.Reading> = emptyList()
            
            override suspend fun getReadingById(readingId: String): com.tarotreader.domain.model.Reading? = null
        }
        
        // Create the use case
        val useCase = AnalyzeSpreadFromImageUseCase(repository)
        
        // Create a mock bitmap
        val bitmap = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888)
        
        // Create a test deck
        val deck = TarotDeck(
            id = "rider_waite",
            name = "Rider-Waite Deck",
            description = "The classic Tarot deck created by A.E. Waite and Pamela Colman Smith. Known for its detailed imagery and accessible symbolism, making it perfect for beginners and experienced readers alike.",
            coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/RWS_Tarot_00_Fool.jpg/300px-RWS_Tarot_00_Fool.jpg",
            cardBackImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f5/RWS_Tarot_00_Fool_%28back%29.jpg/200px-RWS_Tarot_00_Fool_%28back%29.jpg",
            numberOfCards = 78,
            deckType = com.tarotreader.domain.model.DeckType.FULL_DECK
        )
        
        // Analyze the spread from image
        val reading = useCase(bitmap, deck)
        
        // Verify the reading
        assertNotNull(reading)
        assertEquals("rider_waite", reading.deckId)
        assertEquals("three_card", reading.spreadId)
        assertNotNull(reading.date)
        assertTrue(reading.cardDrawings.isNotEmpty())
        assertTrue(reading.interpretation.isNotEmpty())
        assertTrue(reading.eigenvalue != null && reading.eigenvalue >= 0.0 && reading.eigenvalue <= 1.0)
        assertTrue(reading.notes?.contains("AI analysis") ?: false)
    }
}