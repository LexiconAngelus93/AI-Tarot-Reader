package com.tarotreader.domain.usecase

import android.graphics.Bitmap
import com.tarotreader.data.ai.TarotCardDetector
import com.tarotreader.data.ai.SpreadLayoutRecognizer
import com.tarotreader.data.ai.GeminiAIService
import com.tarotreader.domain.model.CardDrawing
import com.tarotreader.domain.model.Reading
import com.tarotreader.domain.model.TarotDeck
import com.tarotreader.domain.repository.TarotRepository
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

class AnalyzeSpreadFromImageUseCase @Inject constructor(
    private val repository: TarotRepository,
    private val cardDetector: TarotCardDetector,
    private val spreadLayoutRecognizer: SpreadLayoutRecognizer,
    private val geminiAIService: GeminiAIService,
    private val calculateEigenvalueUseCase: CalculateEigenvalueUseCase
) {
    suspend operator fun invoke(
        bitmap: Bitmap,
        selectedDeck: TarotDeck
    ): Reading {
        // Step 1: Recognize the spread layout from the image
        val detectedSpreadId = spreadLayoutRecognizer.recognizeSpreadLayout(bitmap)
        val spread = repository.getSpreadById(detectedSpreadId) 
            ?: throw IllegalStateException("Could not recognize spread layout")
        
        // Step 2: Detect cards in the image
        val detectedCards = cardDetector.detectCards(bitmap, selectedDeck.id)
        
        // Step 3: Map detected cards to spread positions
        val cardDrawings = mapCardsToPositions(detectedCards, spread.positions.map { it.id })
        
        // Step 4: Get card details for interpretation
        val allCards = repository.getCardsByDeck(selectedDeck.id).first()
        val readingCards = cardDrawings.mapNotNull { drawing ->
            allCards.find { it.id == drawing.cardId }
        }
        
        // Step 5: Generate AI interpretation
        val interpretation = try {
            if (geminiAIService.isConfigured()) {
                geminiAIService.generateReadingInterpretation(
                    cards = readingCards,
                    cardDrawings = cardDrawings,
                    positions = spread.positions,
                    spreadName = spread.name,
                    question = null
                )
            } else {
                generateFallbackInterpretation(readingCards, cardDrawings, spread.name)
            }
        } catch (e: Exception) {
            generateFallbackInterpretation(readingCards, cardDrawings, spread.name)
        }
        
        // Step 6: Calculate eigenvalue with AI confidence factor
        val eigenvalue = calculateEigenvalueUseCase(
            cardDrawings = cardDrawings,
            cards = readingCards,
            positions = spread.positions
        )
        
        // Step 7: Create and return the reading
        return Reading(
            id = UUID.randomUUID().toString(),
            deckId = selectedDeck.id,
            spreadId = spread.id,
            date = Date(),
            cardDrawings = cardDrawings,
            interpretation = interpretation,
            eigenvalue = eigenvalue,
            notes = "Reading generated from image analysis"
        )
    }
    
    /**
     * Map detected cards to spread positions
     */
    private fun mapCardsToPositions(
        detectedCards: List<Pair<String, Boolean>>, // cardId, isReversed
        positionIds: List<String>
    ): List<CardDrawing> {
        return detectedCards.mapIndexed { index, (cardId, isReversed) ->
            val positionId = positionIds.getOrNull(index) ?: positionIds.first()
            CardDrawing(
                cardId = cardId,
                positionId = positionId,
                isReversed = isReversed
            )
        }
    }
    
    /**
     * Fallback interpretation when AI is unavailable
     */
    private fun generateFallbackInterpretation(
        cards: List<com.tarotreader.domain.model.TarotCard>,
        cardDrawings: List<CardDrawing>,
        spreadName: String
    ): String {
        val cardDescriptions = cards.mapIndexed { index, card ->
            val drawing = cardDrawings.getOrNull(index)
            val orientation = if (drawing?.isReversed == true) "Reversed" else "Upright"
            "${card.name} ($orientation)"
        }.joinToString(", ")
        
        return """
            # Image Analysis Reading
            
            Detected spread: $spreadName
            Cards identified: $cardDescriptions
            
            This reading was generated from your physical tarot spread. The AI has identified the cards and their positions. 
            
            For a detailed interpretation, please review each card's meaning in the context of its position in the spread.
        """.trimIndent()
    }
}