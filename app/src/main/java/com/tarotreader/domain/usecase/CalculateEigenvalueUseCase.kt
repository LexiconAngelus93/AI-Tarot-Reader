package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.TarotCard
import com.tarotreader.domain.model.CardDrawing
import com.tarotreader.domain.model.SpreadPosition
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.pow

class CalculateEigenvalueUseCase @Inject constructor() {
    
    /**
     * Calculate the eigenvalue for a tarot reading
     * This proprietary algorithm analyzes the spiritual energy of the reading
     * by considering card meanings, positions, and their relationships
     */
    operator fun invoke(
        cardDrawings: List<CardDrawing>,
        cards: List<TarotCard>,
        positions: List<SpreadPosition>
    ): Double {
        if (cardDrawings.isEmpty() || cards.isEmpty()) {
            return 0.0
        }
        
        var totalEnergy = 0.0
        var weightSum = 0.0
        
        cardDrawings.forEachIndexed { index, drawing ->
            val card = cards.find { it.id == drawing.cardId } ?: return@forEachIndexed
            val position = positions.find { it.id == drawing.positionId } ?: return@forEachIndexed
            
            // Calculate individual card energy
            val cardEnergy = calculateCardEnergy(card, drawing.isReversed)
            
            // Calculate position weight (earlier positions have more weight)
            val positionWeight = calculatePositionWeight(position.positionOrder, positions.size)
            
            // Calculate numerological influence
            val numerologyFactor = calculateNumerologyFactor(card.numerology)
            
            // Calculate elemental influence
            val elementalFactor = calculateElementalFactor(card.element)
            
            // Combine factors
            val weightedEnergy = cardEnergy * positionWeight * numerologyFactor * elementalFactor
            
            totalEnergy += weightedEnergy
            weightSum += positionWeight
        }
        
        // Calculate card interactions (synergy between cards)
        val interactionBonus = calculateCardInteractions(cardDrawings, cards)
        
        // Normalize the eigenvalue to 0-1 range
        val rawEigenvalue = if (weightSum > 0) {
            (totalEnergy / weightSum) + interactionBonus
        } else {
            0.0
        }
        
        // Clamp to 0-1 range
        return rawEigenvalue.coerceIn(0.0, 1.0)
    }
    
    /**
     * Calculate the energy of a single card
     */
    private fun calculateCardEnergy(card: TarotCard, isReversed: Boolean): Double {
        // Base energy from card type
        val baseEnergy = when (card.arcana) {
            "Major" -> 0.8 // Major Arcana cards have higher base energy
            "Minor" -> 0.6
            else -> 0.5
        }
        
        // Reversed cards have inverted energy
        val orientationModifier = if (isReversed) -0.3 else 0.3
        
        // Keyword count influences energy (more keywords = more complex energy)
        val keywordFactor = (card.uprightKeywords.size + card.reversedKeywords.size) / 20.0
        
        return (baseEnergy + orientationModifier + keywordFactor).coerceIn(0.0, 1.0)
    }
    
    /**
     * Calculate position weight based on order in spread
     */
    private fun calculatePositionWeight(positionOrder: Int, totalPositions: Int): Double {
        // Earlier positions have slightly more weight
        return 1.0 - (positionOrder.toDouble() / (totalPositions * 2.0))
    }
    
    /**
     * Calculate numerological influence
     */
    private fun calculateNumerologyFactor(numerology: String?): Double {
        if (numerology == null) return 1.0
        
        return try {
            val number = numerology.toIntOrNull() ?: return 1.0
            // Numbers 1-9 have different spiritual significance
            when (number) {
                1 -> 1.1 // New beginnings
                2 -> 1.05 // Balance
                3 -> 1.08 // Creativity
                4 -> 1.03 // Stability
                5 -> 1.06 // Change
                6 -> 1.04 // Harmony
                7 -> 1.09 // Spirituality
                8 -> 1.07 // Power
                9 -> 1.1 // Completion
                else -> 1.0
            }
        } catch (e: Exception) {
            1.0
        }
    }
    
    /**
     * Calculate elemental influence
     */
    private fun calculateElementalFactor(element: String?): Double {
        return when (element?.lowercase()) {
            "fire" -> 1.08 // Passion, action
            "water" -> 1.06 // Emotion, intuition
            "air" -> 1.07 // Intellect, communication
            "earth" -> 1.05 // Material, practical
            else -> 1.0
        }
    }
    
    /**
     * Calculate synergy between cards
     */
    private fun calculateCardInteractions(
        cardDrawings: List<CardDrawing>,
        cards: List<TarotCard>
    ): Double {
        if (cardDrawings.size < 2) return 0.0
        
        var interactionScore = 0.0
        var pairCount = 0
        
        // Check each pair of cards
        for (i in cardDrawings.indices) {
            for (j in i + 1 until cardDrawings.size) {
                val card1 = cards.find { it.id == cardDrawings[i].cardId }
                val card2 = cards.find { it.id == cardDrawings[j].cardId }
                
                if (card1 != null && card2 != null) {
                    // Same arcana bonus
                    if (card1.arcana == card2.arcana) {
                        interactionScore += 0.02
                    }
                    
                    // Same element bonus
                    if (card1.element != null && card1.element == card2.element) {
                        interactionScore += 0.03
                    }
                    
                    // Same suit bonus (for Minor Arcana)
                    if (card1.suit != null && card1.suit == card2.suit) {
                        interactionScore += 0.02
                    }
                    
                    pairCount++
                }
            }
        }
        
        return if (pairCount > 0) interactionScore / pairCount else 0.0
    }
}