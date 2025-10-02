package com.tarotreader.data.algorithm

import com.tarotreader.data.model.TarotCard
import com.tarotreader.data.model.CardDrawing
import com.tarotreader.data.model.SpreadPosition
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

object AIReadingEigenvalueCalculator {
    
    // AI-specific confidence thresholds
    private const val HIGH_CONFIDENCE_THRESHOLD = 0.85f
    private const val MEDIUM_CONFIDENCE_THRESHOLD = 0.65f
    private const val LOW_CONFIDENCE_THRESHOLD = 0.45f
    
    /**
     * Calculate the eigenvalue for an AI-generated tarot reading based on:
     * 1. Card meanings (upright vs reversed)
     * 2. Card positions in the spread
     * 3. Numerological significance of cards
     * 4. Elemental associations
     * 5. Astrological connections
     * 6. Confidence levels from AI detection
     */
    fun calculateEigenvalue(
        cardDrawings: List<CardDrawing>,
        cards: List<TarotCard>,
        spreadPositions: List<SpreadPosition>,
        aiConfidence: Float
    ): Double {
        if (cardDrawings.isEmpty()) return 0.0
        
        var totalEnergy = 0.0
        var maxPossibleEnergy = 0.0
        
        cardDrawings.forEach { drawing ->
            val card = cards.find { it.id == drawing.cardId }
            val position = spreadPositions.find { it.id == drawing.positionId }
            
            if (card != null && position != null) {
                // Base energy value (0.0 to 1.0)
                val baseEnergy = calculateBaseEnergy(card, drawing.isReversed)
                
                // Position modifier (0.5 to 2.0)
                val positionModifier = calculatePositionModifier(position.positionIndex)
                
                // Numerology factor (0.8 to 1.6)
                val numerologyFactor = calculateNumerologyFactor(card.numerology)
                
                // Elemental harmony (0.7 to 1.3)
                val elementalFactor = calculateElementalFactor(card.element, position, cards, cardDrawings)
                
                // Astrological connection (0.6 to 1.4)
                val astrologicalFactor = calculateAstrologicalFactor(card.astrology)
                
                // AI confidence factor (0.5 to 1.0)
                val confidenceFactor = calculateConfidenceFactor(aiConfidence)
                
                // Combine all factors
                val cardEnergy = baseEnergy * positionModifier * numerologyFactor * 
                               elementalFactor * astrologicalFactor * confidenceFactor
                
                totalEnergy += cardEnergy
                maxPossibleEnergy += 1.0 * 2.0 * 1.6 * 1.3 * 1.4 * 1.0 // Maximum possible values
            }
        }
        
        // Add AI-specific adjustments
        val detectionQualityBonus = calculateDetectionQualityBonus(aiConfidence, cardDrawings.size)
        totalEnergy += detectionQualityBonus
        
        // Normalize to 0.0 - 1.0 range
        val normalizedEnergy = if (maxPossibleEnergy > 0) totalEnergy / maxPossibleEnergy else 0.0
        
        // Adjust based on number of cards (more cards = more complex reading)
        val cardCountFactor = when (cardDrawings.size) {
            1 -> 0.7
            2 -> 0.8
            3 -> 1.0
            in 4..7 -> 1.1
            in 8..10 -> 1.2
            else -> 1.3
        }
        
        // Final eigenvalue calculation
        val eigenvalue = normalizedEnergy * cardCountFactor
        
        // Ensure value is between 0.0 and 1.0
        return eigenvalue.coerceIn(0.0, 1.0)
    }
    
    /**
     * Calculate base energy with detailed card meaning analysis
     */
    private fun calculateBaseEnergy(card: TarotCard, isReversed: Boolean): Double {
        // Analyze card type
        val isMajorArcana = card.numerology?.toIntOrNull() in 0..21
        val baseWeight = if (isMajorArcana) 1.2 else 1.0
        
        // Analyze keywords for energy level
        val keywords = if (isReversed) card.reversedKeywords else card.uprightKeywords
        val keywordEnergy = analyzeKeywordEnergy(keywords)
        
        // Reversed cards have modified energy
        val orientationModifier = if (isReversed) 0.75 else 1.0
        
        // Combine factors
        return (baseWeight * keywordEnergy * orientationModifier).coerceIn(0.0, 1.0)
    }
    
    private fun analyzeKeywordEnergy(keywords: List<String>): Double {
        if (keywords.isEmpty()) return 0.8
        
        val positiveKeywords = setOf(
            "success", "growth", "abundance", "joy", "love", "harmony", "peace",
            "clarity", "wisdom", "strength", "power", "victory", "achievement"
        )
        
        val challengingKeywords = setOf(
            "conflict", "loss", "sorrow", "confusion", "fear", "struggle",
            "defeat", "betrayal", "stagnation", "restriction", "chaos"
        )
        
        var positiveCount = 0
        var challengingCount = 0
        
        keywords.forEach { keyword ->
            val lower = keyword.lowercase()
            if (positiveKeywords.any { lower.contains(it) }) positiveCount++
            if (challengingKeywords.any { lower.contains(it) }) challengingCount++
        }
        
        val total = positiveCount + challengingCount
        return if (total > 0) {
            0.5 + (positiveCount - challengingCount).toDouble() / (total * 2)
        } else {
            0.8
        }
    }
    
    private fun calculatePositionModifier(positionIndex: Int): Double {
        // First positions have more significance
        return when (positionIndex) {
            0 -> 2.0    // Most significant position
            1 -> 1.8
            2 -> 1.6
            3 -> 1.5
            4 -> 1.4
            5 -> 1.3
            6 -> 1.2
            7 -> 1.1
            8 -> 1.0
            9 -> 0.9
            else -> 1.0 - (positionIndex * 0.05)
        }.coerceAtLeast(0.5).coerceAtMost(2.0)
    }
    
    private fun calculateNumerologyFactor(numerology: String?): Double {
        return when (numerology) {
            "0" -> 1.5   // The Fool - Infinite potential
            "1" -> 1.3   // New beginnings
            "2" -> 1.2   // Balance
            "3" -> 1.4   // Creativity
            "4" -> 1.0   // Stability
            "5" -> 1.3   // Change
            "6" -> 1.2   // Harmony
            "7" -> 1.5   // Spirituality
            "8" -> 1.3   // Power
            "9" -> 1.4   // Completion
            "10" -> 1.2  // Cycle completion
            "11" -> 1.6  // Master number
            "22" -> 1.6  // Master number
            else -> 1.0
        }
    }
    
    /**
     * Calculate elemental factor with compatibility checking
     */
    private fun calculateElementalFactor(
        element: String?,
        position: SpreadPosition,
        allCards: List<TarotCard>,
        cardDrawings: List<CardDrawing>
    ): Double {
        if (element == null) return 1.0
        
        // Elemental compatibility matrix
        val compatibility = mapOf(
            "Fire" to mapOf("Fire" to 1.3, "Water" to 0.7, "Air" to 1.2, "Earth" to 0.9),
            "Water" to mapOf("Fire" to 0.7, "Water" to 1.3, "Air" to 0.9, "Earth" to 1.2),
            "Air" to mapOf("Fire" to 1.2, "Water" to 0.9, "Air" to 1.3, "Earth" to 0.8),
            "Earth" to mapOf("Fire" to 0.9, "Water" to 1.2, "Air" to 0.8, "Earth" to 1.3)
        )
        
        // Get position's natural element
        val positionElement = when (position.positionIndex % 4) {
            0 -> "Fire"
            1 -> "Water"
            2 -> "Air"
            3 -> "Earth"
            else -> "Fire"
        }
        
        // Check compatibility
        val compatibilityScore = compatibility[element]?.get(positionElement) ?: 1.0
        
        // Check harmony with other cards
        val otherElements = cardDrawings.mapNotNull { drawing ->
            allCards.find { it.id == drawing.cardId }?.element
        }.filter { it != element }
        
        val harmonyScore = if (otherElements.isNotEmpty()) {
            var totalCompat = 0.0
            otherElements.forEach { other ->
                totalCompat += compatibility[element]?.get(other) ?: 1.0
            }
            totalCompat / otherElements.size
        } else {
            1.0
        }
        
        return (compatibilityScore + harmonyScore) / 2
    }
    
    /**
     * Calculate astrological factor with detailed planetary and zodiac influences
     */
    private fun calculateAstrologicalFactor(astrology: String?): Double {
        if (astrology == null || astrology.isEmpty()) return 1.0
        
        val planetaryWeights = mapOf(
            "Sun" to 1.4, "Moon" to 1.3, "Mercury" to 1.2,
            "Venus" to 1.25, "Mars" to 1.3, "Jupiter" to 1.35,
            "Saturn" to 1.15, "Uranus" to 1.25, "Neptune" to 1.2, "Pluto" to 1.3
        )
        
        val zodiacWeights = mapOf(
            "Aries" to 1.3, "Taurus" to 1.1, "Gemini" to 1.2,
            "Cancer" to 1.15, "Leo" to 1.3, "Virgo" to 1.1,
            "Libra" to 1.2, "Scorpio" to 1.25, "Sagittarius" to 1.2,
            "Capricorn" to 1.15, "Aquarius" to 1.25, "Pisces" to 1.2
        )
        
        var factor = 1.0
        
        // Check planetary influences
        planetaryWeights.forEach { (planet, weight) ->
            if (astrology.contains(planet, ignoreCase = true)) {
                factor *= weight
            }
        }
        
        // Check zodiac influences
        zodiacWeights.forEach { (sign, weight) ->
            if (astrology.contains(sign, ignoreCase = true)) {
                factor *= weight
            }
        }
        
        return factor.coerceIn(0.6, 1.4)
    }
    
    /**
     * Calculate confidence factor based on AI detection confidence
     */
    private fun calculateConfidenceFactor(aiConfidence: Float): Double {
        return when {
            aiConfidence >= HIGH_CONFIDENCE_THRESHOLD -> 1.0
            aiConfidence >= MEDIUM_CONFIDENCE_THRESHOLD -> 0.85
            aiConfidence >= LOW_CONFIDENCE_THRESHOLD -> 0.7
            else -> 0.5
        }.toDouble()
    }
    
    /**
     * Calculate bonus based on detection quality
     */
    private fun calculateDetectionQualityBonus(aiConfidence: Float, cardCount: Int): Double {
        // High confidence with multiple cards gets a bonus
        return if (aiConfidence >= HIGH_CONFIDENCE_THRESHOLD && cardCount >= 3) {
            0.1 * (cardCount - 2).coerceAtMost(5)
        } else if (aiConfidence >= MEDIUM_CONFIDENCE_THRESHOLD && cardCount >= 5) {
            0.05 * (cardCount - 4).coerceAtMost(3)
        } else {
            0.0
        }
    }
}