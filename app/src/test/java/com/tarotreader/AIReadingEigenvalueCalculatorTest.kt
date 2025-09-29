package com.tarotreader

import com.tarotreader.data.algorithm.AIReadingEigenvalueCalculator
import com.tarotreader.data.model.TarotCard
import com.tarotreader.data.model.CardDrawing
import com.tarotreader.data.model.SpreadPosition
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class AIReadingEigenvalueCalculatorTest {
    
    @Test
    fun testCalculateEigenvalue() {
        // Create test cards
        val cards = listOf(
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
            )
        )
        
        // Create test positions
        val positions = listOf(
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
            )
        )
        
        // Create test card drawings
        val cardDrawings = listOf(
            CardDrawing(
                cardId = "0_rider_waite",
                positionId = "1_three_card",
                isReversed = false
            ),
            CardDrawing(
                cardId = "1_rider_waite",
                positionId = "2_three_card",
                isReversed = true
            )
        )
        
        // Calculate eigenvalue with AI confidence
        val eigenvalue = AIReadingEigenvalueCalculator.calculateEigenvalue(cardDrawings, cards, positions, 0.85f)
        
        // Verify the result is between 0.0 and 1.0
        assertTrue(eigenvalue >= 0.0 && eigenvalue <= 1.0)
        
        // Verify the result is a specific value (this might need adjustment based on the actual algorithm)
        // For now, we're just checking that it returns a valid value
        assertTrue(eigenvalue > 0.0)
    }
    
    @Test
    fun testCalculateEigenvalueWithEmptyDrawings() {
        // Calculate eigenvalue with empty card drawings
        val eigenvalue = AIReadingEigenvalueCalculator.calculateEigenvalue(emptyList(), emptyList(), emptyList(), 0.85f)
        
        // Verify the result is 0.0
        assertEquals(0.0, eigenvalue, 0.001)
    }
    
    @Test
    fun testCalculateEigenvalueWithLowConfidence() {
        // Create test cards
        val cards = listOf(
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
            )
        )
        
        // Create test positions
        val positions = listOf(
            SpreadPosition(
                id = "1_three_card",
                spreadId = "three_card",
                positionIndex = 0,
                name = "Past",
                meaning = "What led you to this moment",
                x = 0.3f,
                y = 0.5f
            )
        )
        
        // Create test card drawings
        val cardDrawings = listOf(
            CardDrawing(
                cardId = "0_rider_waite",
                positionId = "1_three_card",
                isReversed = false
            )
        )
        
        // Calculate eigenvalue with low AI confidence
        val eigenvalue = AIReadingEigenvalueCalculator.calculateEigenvalue(cardDrawings, cards, positions, 0.3f)
        
        // Verify the result is between 0.0 and 1.0
        assertTrue(eigenvalue >= 0.0 && eigenvalue <= 1.0)
        
        // With low confidence, the eigenvalue should be lower
        assertTrue(eigenvalue < 0.5)
    }
}