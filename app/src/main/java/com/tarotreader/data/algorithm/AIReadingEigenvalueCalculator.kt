package com.tarotreader.data.algorithm

import com.tarotreader.data.model.TarotCard
import com.tarotreader.data.model.CardDrawing
import com.tarotreader.data.model.SpreadPosition
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

object AIReadingEigenvalueCalculator {
    
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
                
                // Numerology factor (0.8 to 1.2)
                val numerologyFactor = calculateNumerologyFactor(card.numerology)
                
                // Elemental harmony (0.7 to 1.3)
                val elementalFactor = calculateElementalFactor(card.element, position)
                
                // Astrological connection (0.6 to 1.4)
                val astrologicalFactor = calculateAstrologicalFactor(card.astrology)
                
                // AI confidence factor (0.5 to 1.0)
                val confidenceFactor = aiConfidence.coerceIn(0.5f, 1.0f).toDouble()
                
                // Combine all factors
                val cardEnergy = baseEnergy * positionModifier * numerologyFactor * elementalFactor * astrologicalFactor * confidenceFactor
                totalEnergy += cardEnergy
                maxPossibleEnergy += 1.0 * 2.0 * 1.2 * 1.3 * 1.4 * 1.0 // Maximum possible values
            }
        }
        
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
    
    private fun calculateBaseEnergy(card: TarotCard, isReversed: Boolean): Double {
        // In a real implementation, this would be based on detailed card meanings
        // For now, we'll use a simple calculation
        return if (isReversed) 0.8 else 1.0
    }
    
    private fun calculatePositionModifier(positionIndex: Int): Double {
        // First positions have more significance
        return when (positionIndex) {
            0 -> 2.0 // Most significant position
            1 -> 1.8
            2 -> 1.6
            else -> 1.0 + (positionIndex * 0.1) // Decreasing significance
        }.coerceAtMost(2.0) // Cap at 2.0
    }
    
    private fun calculateNumerologyFactor(numerology: String?): Double {
        return when (numerology) {
            "0" -> 1.1
            "1" -> 1.2
            "2" -> 1.1
            "3" -> 1.0
            "4" -> 0.9
            "5" -> 1.1
            "6" -> 1.0
            "7" -> 0.8
            "8" -> 0.9
            "9" -> 1.1
            "10" -> 1.0
            "11" -> 1.3 // Master number
            "22" -> 1.4 // Master number
            else -> 1.0
        }
    }
    
    private fun calculateElementalFactor(element: String?, position: SpreadPosition): Double {
        // In a real implementation, this would check elemental compatibility with position
        // For now, we'll use a simple calculation based on position meaning
        return when (position.positionIndex % 4) {
            0 -> 1.3 // Fire element positions
            1 -> 1.1 // Water element positions
            2 -> 0.9 // Air element positions
            3 -> 0.7 // Earth element positions
            else -> 1.0
        }
    }
    
    private fun calculateAstrologicalFactor(astrology: String?): Double {
        // In a real implementation, this would check astrological connections
        // For now, we'll use a placeholder calculation
        return if (astrology != null && astrology.isNotEmpty()) {
            1.2
        } else {
            1.0
        }
    }
}