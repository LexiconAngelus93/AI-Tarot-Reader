package com.tarotreader.data.algorithm

import com.tarotreader.data.model.TarotCard
import com.tarotreader.data.model.CardDrawing
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

object EigenvalueCalculator {
    
    // Card meaning weights for detailed calculations
    private val MAJOR_ARCANA_WEIGHTS = mapOf(
        "0" to 1.0,   // The Fool - New beginnings
        "1" to 1.2,   // The Magician - Manifestation
        "2" to 1.1,   // The High Priestess - Intuition
        "3" to 1.15,  // The Empress - Abundance
        "4" to 1.1,   // The Emperor - Authority
        "5" to 1.25,  // The Hierophant - Tradition
        "6" to 1.2,   // The Lovers - Relationships
        "7" to 1.3,   // The Chariot - Willpower
        "8" to 1.15,  // Strength - Inner strength
        "9" to 1.35,  // The Hermit - Introspection
        "10" to 1.4,  // Wheel of Fortune - Destiny
        "11" to 1.2,  // Justice - Balance
        "12" to 1.25, // The Hanged Man - Surrender
        "13" to 1.5,  // Death - Transformation
        "14" to 1.2,  // Temperance - Moderation
        "15" to 1.3,  // The Devil - Bondage
        "16" to 1.45, // The Tower - Upheaval
        "17" to 1.25, // The Star - Hope
        "18" to 1.35, // The Moon - Illusion
        "19" to 1.4,  // The Sun - Success
        "20" to 1.5,  // Judgement - Rebirth
        "21" to 1.6   // The World - Completion
    )
    
    // Elemental compatibility matrix
    private val ELEMENTAL_COMPATIBILITY = mapOf(
        "Fire" to mapOf("Fire" to 1.3, "Water" to 0.7, "Air" to 1.2, "Earth" to 0.9),
        "Water" to mapOf("Fire" to 0.7, "Water" to 1.3, "Air" to 0.9, "Earth" to 1.2),
        "Air" to mapOf("Fire" to 1.2, "Water" to 0.9, "Air" to 1.3, "Earth" to 0.8),
        "Earth" to mapOf("Fire" to 0.9, "Water" to 1.2, "Air" to 0.8, "Earth" to 1.3)
    )
    
    // Suit energies for Minor Arcana
    private val SUIT_ENERGIES = mapOf(
        "Wands" to 1.2,    // Fire - Action, passion
        "Cups" to 1.1,     // Water - Emotion, intuition
        "Swords" to 1.15,  // Air - Intellect, conflict
        "Pentacles" to 1.0 // Earth - Material, practical
    )
    
    /**
     * Calculate the eigenvalue for a tarot reading based on:
     * 1. Card meanings (upright vs reversed)
     * 2. Card positions in the spread
     * 3. Numerological significance of cards
     * 4. Elemental associations
     * 5. Astrological connections
     */
    fun calculateEigenvalue(
        cardDrawings: List<CardDrawing>,
        cards: List<TarotCard>,
        spreadPositions: List<com.tarotreader.data.model.SpreadPosition>
    ): Double {
        if (cardDrawings.isEmpty()) return 0.0
        
        var totalEnergy = 0.0
        var maxPossibleEnergy = 0.0
        
        cardDrawings.forEachIndexed { index, drawing ->
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
                val astrologicalFactor = calculateAstrologicalFactor(card.astrology, position)
                
                // Card type weight (Major vs Minor Arcana)
                val cardTypeWeight = getCardTypeWeight(card)
                
                // Combine all factors
                val cardEnergy = baseEnergy * positionModifier * numerologyFactor * 
                               elementalFactor * astrologicalFactor * cardTypeWeight
                
                totalEnergy += cardEnergy
                maxPossibleEnergy += 1.0 * 2.0 * 1.6 * 1.3 * 1.4 * 1.6 // Maximum possible values
            }
        }
        
        // Add synergy bonus for card interactions
        val synergyBonus = calculateSynergyBonus(cardDrawings, cards)
        totalEnergy += synergyBonus
        
        // Normalize to 0.0 - 1.0 range
        return if (maxPossibleEnergy > 0) {
            (totalEnergy / maxPossibleEnergy).coerceIn(0.0, 1.0)
        } else {
            0.0
        }
    }
    
    /**
     * Calculate base energy based on detailed card meanings
     */
    private fun calculateBaseEnergy(card: TarotCard, isReversed: Boolean): Double {
        // Get card-specific weight
        val cardWeight = MAJOR_ARCANA_WEIGHTS[card.numerology] ?: 1.0
        
        // Analyze keyword sentiment
        val keywords = if (isReversed) card.reversedKeywords else card.uprightKeywords
        val keywordEnergy = analyzeKeywordEnergy(keywords)
        
        // Reversed cards have modified energy
        val orientationModifier = if (isReversed) 0.75 else 1.0
        
        // Combine factors
        return (cardWeight * keywordEnergy * orientationModifier).coerceIn(0.0, 1.0)
    }
    
    /**
     * Analyze energy from card keywords
     */
    private fun analyzeKeywordEnergy(keywords: List<String>): Double {
        if (keywords.isEmpty()) return 0.8
        
        // Positive energy keywords
        val positiveKeywords = setOf(
            "success", "growth", "abundance", "joy", "love", "harmony", "peace",
            "clarity", "wisdom", "strength", "power", "victory", "achievement",
            "prosperity", "happiness", "fulfillment", "balance", "hope"
        )
        
        // Challenging energy keywords
        val challengingKeywords = setOf(
            "conflict", "loss", "sorrow", "confusion", "fear", "anxiety", "struggle",
            "defeat", "betrayal", "deception", "stagnation", "restriction", "bondage",
            "chaos", "destruction", "pain", "suffering", "darkness"
        )
        
        var positiveCount = 0
        var challengingCount = 0
        
        keywords.forEach { keyword ->
            val lowerKeyword = keyword.lowercase()
            if (positiveKeywords.any { lowerKeyword.contains(it) }) {
                positiveCount++
            }
            if (challengingKeywords.any { lowerKeyword.contains(it) }) {
                challengingCount++
            }
        }
        
        // Calculate energy based on keyword balance
        val totalRelevant = positiveCount + challengingCount
        return if (totalRelevant > 0) {
            0.5 + (positiveCount - challengingCount).toDouble() / (totalRelevant * 2)
        } else {
            0.8 // Neutral energy
        }
    }
    
    private fun calculatePositionModifier(positionIndex: Int): Double {
        // First positions have more significance
        return when (positionIndex) {
            0 -> 2.0    // Most significant position (present/situation)
            1 -> 1.8    // Challenge/obstacle
            2 -> 1.6    // Past influences
            3 -> 1.5    // Future potential
            4 -> 1.4    // Above (conscious)
            5 -> 1.3    // Below (subconscious)
            6 -> 1.2    // Advice
            7 -> 1.1    // External influences
            8 -> 1.0    // Hopes/fears
            9 -> 0.9    // Outcome
            else -> 1.0 - (positionIndex * 0.05) // Decreasing significance
        }.coerceAtLeast(0.5).coerceAtMost(2.0)
    }
    
    private fun calculateNumerologyFactor(numerology: String?): Double {
        return when (numerology) {
            "1" -> 1.3   // New beginnings, initiative
            "2" -> 1.2   // Balance, duality
            "3" -> 1.4   // Creativity, expression
            "4" -> 1.0   // Stability, foundation
            "5" -> 1.3   // Change, freedom
            "6" -> 1.2   // Harmony, responsibility
            "7" -> 1.5   // Spirituality, introspection
            "8" -> 1.3   // Power, manifestation
            "9" -> 1.4   // Completion, wisdom
            "10" -> 1.2  // Cycle completion
            "11" -> 1.6  // Master number - Illumination
            "22" -> 1.6  // Master number - Master builder
            "0" -> 1.5   // The Fool - Infinite potential
            else -> 1.0
        }
    }
    
    /**
     * Calculate elemental factor with compatibility checking
     */
    private fun calculateElementalFactor(
        element: String?,
        position: com.tarotreader.data.model.SpreadPosition,
        allCards: List<TarotCard>,
        cardDrawings: List<CardDrawing>
    ): Double {
        if (element == null) return 1.0
        
        // Get position's natural element (based on position index)
        val positionElement = getPositionElement(position.positionIndex)
        
        // Check compatibility with position element
        val compatibilityScore = ELEMENTAL_COMPATIBILITY[element]?.get(positionElement) ?: 1.0
        
        // Check harmony with other cards in spread
        val harmonyScore = calculateElementalHarmony(element, allCards, cardDrawings)
        
        return (compatibilityScore + harmonyScore) / 2
    }
    
    private fun getPositionElement(positionIndex: Int): String {
        return when (positionIndex % 4) {
            0 -> "Fire"   // Action, will
            1 -> "Water"  // Emotion, intuition
            2 -> "Air"    // Thought, communication
            3 -> "Earth"  // Material, practical
            else -> "Fire"
        }
    }
    
    private fun calculateElementalHarmony(
        element: String,
        allCards: List<TarotCard>,
        cardDrawings: List<CardDrawing>
    ): Double {
        val otherElements = cardDrawings.mapNotNull { drawing ->
            allCards.find { it.id == drawing.cardId }?.element
        }.filter { it != element }
        
        if (otherElements.isEmpty()) return 1.0
        
        var totalCompatibility = 0.0
        otherElements.forEach { otherElement ->
            totalCompatibility += ELEMENTAL_COMPATIBILITY[element]?.get(otherElement) ?: 1.0
        }
        
        return totalCompatibility / otherElements.size
    }
    
    /**
     * Calculate astrological factor with zodiac and planetary influences
     */
    private fun calculateAstrologicalFactor(
        astrology: String?,
        position: com.tarotreader.data.model.SpreadPosition
    ): Double {
        if (astrology == null || astrology.isEmpty()) return 1.0
        
        // Planetary influences
        val planetaryWeights = mapOf(
            "Sun" to 1.4,
            "Moon" to 1.3,
            "Mercury" to 1.2,
            "Venus" to 1.25,
            "Mars" to 1.3,
            "Jupiter" to 1.35,
            "Saturn" to 1.15,
            "Uranus" to 1.25,
            "Neptune" to 1.2,
            "Pluto" to 1.3
        )
        
        // Zodiac influences
        val zodiacWeights = mapOf(
            "Aries" to 1.3,
            "Taurus" to 1.1,
            "Gemini" to 1.2,
            "Cancer" to 1.15,
            "Leo" to 1.3,
            "Virgo" to 1.1,
            "Libra" to 1.2,
            "Scorpio" to 1.25,
            "Sagittarius" to 1.2,
            "Capricorn" to 1.15,
            "Aquarius" to 1.25,
            "Pisces" to 1.2
        )
        
        // Check for planetary influence
        var factor = 1.0
        planetaryWeights.forEach { (planet, weight) ->
            if (astrology.contains(planet, ignoreCase = true)) {
                factor *= weight
            }
        }
        
        // Check for zodiac influence
        zodiacWeights.forEach { (sign, weight) ->
            if (astrology.contains(sign, ignoreCase = true)) {
                factor *= weight
            }
        }
        
        return factor.coerceIn(0.6, 1.4)
    }
    
    private fun getCardTypeWeight(card: TarotCard): Double {
        // Major Arcana cards have higher weight
        return if (card.numerology != null && card.numerology.toIntOrNull() in 0..21) {
            MAJOR_ARCANA_WEIGHTS[card.numerology] ?: 1.2
        } else {
            // Minor Arcana - check suit
            val suit = when {
                card.id.contains("wands", ignoreCase = true) -> "Wands"
                card.id.contains("cups", ignoreCase = true) -> "Cups"
                card.id.contains("swords", ignoreCase = true) -> "Swords"
                card.id.contains("pentacles", ignoreCase = true) -> "Pentacles"
                else -> null
            }
            SUIT_ENERGIES[suit] ?: 1.0
        }
    }
    
    /**
     * Calculate synergy bonus for card interactions
     */
    private fun calculateSynergyBonus(
        cardDrawings: List<CardDrawing>,
        cards: List<TarotCard>
    ): Double {
        if (cardDrawings.size < 2) return 0.0
        
        var synergyScore = 0.0
        
        // Check for Major Arcana clusters
        val majorArcanaCount = cardDrawings.count { drawing ->
            val card = cards.find { it.id == drawing.cardId }
            card?.numerology?.toIntOrNull() in 0..21
        }
        if (majorArcanaCount >= 3) {
            synergyScore += 0.2 * (majorArcanaCount - 2)
        }
        
        // Check for suit dominance
        val suitCounts = mutableMapOf<String, Int>()
        cardDrawings.forEach { drawing ->
            val card = cards.find { it.id == drawing.cardId }
            val suit = when {
                card?.id?.contains("wands", ignoreCase = true) == true -> "Wands"
                card?.id?.contains("cups", ignoreCase = true) == true -> "Cups"
                card?.id?.contains("swords", ignoreCase = true) == true -> "Swords"
                card?.id?.contains("pentacles", ignoreCase = true) == true -> "Pentacles"
                else -> null
            }
            suit?.let { suitCounts[it] = (suitCounts[it] ?: 0) + 1 }
        }
        val maxSuitCount = suitCounts.values.maxOrNull() ?: 0
        if (maxSuitCount >= 3) {
            synergyScore += 0.15 * (maxSuitCount - 2)
        }
        
        // Check for reversed card patterns
        val reversedCount = cardDrawings.count { it.isReversed }
        if (reversedCount == cardDrawings.size) {
            // All reversed - significant challenge pattern
            synergyScore += 0.1
        } else if (reversedCount == 0) {
            // All upright - harmonious pattern
            synergyScore += 0.15
        }
        
        return synergyScore.coerceAtMost(0.5)
    }
}