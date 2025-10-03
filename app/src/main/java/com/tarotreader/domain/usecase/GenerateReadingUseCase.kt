package com.tarotreader.domain.usecase

import com.tarotreader.data.ai.GeminiAIService
import com.tarotreader.domain.model.CardDrawing
import com.tarotreader.domain.model.Reading
import com.tarotreader.domain.model.TarotCard
import com.tarotreader.domain.repository.TarotRepository
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

class GenerateReadingUseCase @Inject constructor(
    private val repository: TarotRepository,
    private val geminiAIService: GeminiAIService,
    private val calculateEigenvalueUseCase: CalculateEigenvalueUseCase
) {
    suspend operator fun invoke(
        deckId: String,
        spreadId: String,
        cardDrawings: List<CardDrawing>,
        question: String? = null
    ): Reading {
        // Fetch deck and spread information
        val deck = repository.getDeckById(deckId)
        val spread = repository.getSpreadById(spreadId)
        
        if (deck == null || spread == null) {
            throw IllegalArgumentException("Invalid deck or spread ID")
        }
        
        // Get all cards for the reading
        val allCards = repository.getCardsByDeck(deckId).first()
        val readingCards = cardDrawings.mapNotNull { drawing ->
            allCards.find { it.id == drawing.cardId }
        }
        
        // Generate AI-powered interpretation
        val interpretation = try {
            if (geminiAIService.isConfigured()) {
                geminiAIService.generateReadingInterpretation(
                    cards = readingCards,
                    cardDrawings = cardDrawings,
                    positions = spread.positions,
                    spreadName = spread.name,
                    question = question
                )
            } else {
                generateFallbackInterpretation(readingCards, cardDrawings, spread.positions)
            }
        } catch (e: Exception) {
            generateFallbackInterpretation(readingCards, cardDrawings, spread.positions)
        }
        
        // Calculate eigenvalue
        val eigenvalue = calculateEigenvalueUseCase(
            cardDrawings = cardDrawings,
            cards = readingCards,
            positions = spread.positions
        )
        
        return Reading(
            id = UUID.randomUUID().toString(),
            deckId = deckId,
            spreadId = spreadId,
            date = Date(),
            cardDrawings = cardDrawings,
            interpretation = interpretation,
            eigenvalue = eigenvalue,
            notes = null
        )
    }
    
    /**
     * Fallback interpretation when AI is unavailable
     */
    private fun generateFallbackInterpretation(
        cards: List<TarotCard>,
        cardDrawings: List<CardDrawing>,
        positions: List<com.tarotreader.domain.model.SpreadPosition>
    ): String {
        val interpretations = mutableListOf<String>()
        
        cardDrawings.forEachIndexed { index, drawing ->
            val card = cards.find { it.id == drawing.cardId }
            val position = positions.find { it.id == drawing.positionId }
            
            if (card != null && position != null) {
                val orientation = if (drawing.isReversed) "Reversed" else "Upright"
                val meaning = if (drawing.isReversed) card.reversedMeaning else card.uprightMeaning
                val keywords = if (drawing.isReversed) card.reversedKeywords else card.uprightKeywords
                
                interpretations.add("""
                    **${position.name}**: ${card.name} ($orientation)
                    
                    ${position.meaning}
                    
                    $meaning
                    
                    Key themes: ${keywords.joinToString(", ")}
                """.trimIndent())
            }
        }
        
        return """
            # Your Tarot Reading
            
            ${interpretations.joinToString("\n\n---\n\n")}
            
            ## Overall Guidance
            
            This reading reveals the energies and influences surrounding your current situation. Each card offers unique insights and guidance. Reflect on how these messages resonate with your life and what actions you might take moving forward. Remember, the Tarot is a tool for self-reflection and empowerment - the future is not fixed, and you have the power to shape your path.
        """.trimIndent()
    }
}