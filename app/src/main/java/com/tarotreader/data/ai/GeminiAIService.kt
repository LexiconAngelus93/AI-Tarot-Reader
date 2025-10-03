package com.tarotreader.data.ai

import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.tarotreader.BuildConfig
import com.tarotreader.domain.model.TarotCard
import com.tarotreader.domain.model.CardDrawing
import com.tarotreader.data.model.SpreadPosition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for interacting with Google's Gemini AI model
 * Provides AI-powered tarot reading interpretations and card meanings
 */
@Singleton
class GeminiAIService @Inject constructor(
    private val context: Context
) {
    private val apiKey: String
        get() = BuildConfig.GEMINI_API_KEY.ifEmpty {
            // Fallback to reading from local.properties or environment
            context.assets.open("gemini_api_key.txt").bufferedReader().use { it.readText().trim() }
        }

    private val generativeModel: GenerativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-pro",
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = 0.7f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 2048
            }
        )
    }

    /**
     * Generate a comprehensive tarot reading interpretation
     * @param cards List of cards drawn with their positions
     * @param positions List of spread positions with meanings
     * @param spreadName Name of the spread being used
     * @param question Optional question asked by the user
     * @return AI-generated reading interpretation
     */
    suspend fun generateReadingInterpretation(
        cards: List<TarotCard>,
        cardDrawings: List<CardDrawing>,
        positions: List<SpreadPosition>,
        spreadName: String,
        question: String? = null
    ): String = withContext(Dispatchers.IO) {
        try {
            val prompt = buildReadingPrompt(cards, cardDrawings, positions, spreadName, question)
            
            val response = generativeModel.generateContent(prompt)
            response.text ?: "Unable to generate interpretation. Please try again."
        } catch (e: Exception) {
            "Error generating interpretation: ${e.message}. Using fallback interpretation."
        }
    }

    /**
     * Generate detailed interpretation for a single card
     * @param card The tarot card
     * @param isReversed Whether the card is reversed
     * @param position The position in the spread
     * @param context Additional context for the reading
     * @return AI-generated card interpretation
     */
    suspend fun generateCardInterpretation(
        card: TarotCard,
        isReversed: Boolean,
        position: SpreadPosition,
        context: String? = null
    ): String = withContext(Dispatchers.IO) {
        try {
            val prompt = buildCardPrompt(card, isReversed, position, context)
            
            val response = generativeModel.generateContent(prompt)
            response.text ?: getFallbackCardInterpretation(card, isReversed)
        } catch (e: Exception) {
            getFallbackCardInterpretation(card, isReversed)
        }
    }

    /**
     * Generate a daily card reading with reflection
     * @param card The daily card drawn
     * @param isReversed Whether the card is reversed
     * @return AI-generated daily reading
     */
    suspend fun generateDailyReading(
        card: TarotCard,
        isReversed: Boolean
    ): String = withContext(Dispatchers.IO) {
        try {
            val prompt = buildDailyCardPrompt(card, isReversed)
            
            val response = generativeModel.generateContent(prompt)
            response.text ?: getFallbackCardInterpretation(card, isReversed)
        } catch (e: Exception) {
            getFallbackCardInterpretation(card, isReversed)
        }
    }

    /**
     * Generate insights based on reading history patterns
     * @param recentReadings List of recent readings
     * @return AI-generated insights
     */
    suspend fun generateReadingInsights(
        recentReadings: List<String>
    ): String = withContext(Dispatchers.IO) {
        try {
            val prompt = buildInsightsPrompt(recentReadings)
            
            val response = generativeModel.generateContent(prompt)
            response.text ?: "No patterns detected in recent readings."
        } catch (e: Exception) {
            "Unable to generate insights at this time."
        }
    }

    /**
     * Build prompt for comprehensive reading interpretation
     */
    private fun buildReadingPrompt(
        cards: List<TarotCard>,
        cardDrawings: List<CardDrawing>,
        positions: List<SpreadPosition>,
        spreadName: String,
        question: String?
    ): String {
        val questionContext = question?.let { "Question asked: $it\n\n" } ?: ""
        
        val cardsDescription = cardDrawings.mapIndexed { index, drawing ->
            val card = cards.find { it.id == drawing.cardId }
            val position = positions.find { it.id == drawing.positionId }
            val orientation = if (drawing.isReversed) "Reversed" else "Upright"
            
            """
            Position ${index + 1}: ${position?.name ?: "Unknown"}
            Meaning: ${position?.meaning ?: ""}
            Card: ${card?.name ?: "Unknown"} ($orientation)
            Keywords: ${if (drawing.isReversed) card?.reversedKeywords?.joinToString(", ") else card?.uprightKeywords?.joinToString(", ")}
            """.trimIndent()
        }.joinToString("\n\n")

        return """
        You are an expert tarot reader with deep knowledge of symbolism, intuition, and spiritual guidance.
        
        Spread: $spreadName
        $questionContext
        Cards drawn:
        
        $cardsDescription
        
        Please provide a comprehensive, insightful tarot reading that:
        1. Interprets each card in the context of its position
        2. Explores the relationships and connections between the cards
        3. Addresses the question (if provided) with wisdom and clarity
        4. Offers practical guidance and spiritual insights
        5. Maintains a compassionate, empowering tone
        6. Weaves the cards into a cohesive narrative
        
        Format the reading with clear sections and flowing prose. Be specific, insightful, and helpful.
        """.trimIndent()
    }

    /**
     * Build prompt for single card interpretation
     */
    private fun buildCardPrompt(
        card: TarotCard,
        isReversed: Boolean,
        position: SpreadPosition,
        context: String?
    ): String {
        val orientation = if (isReversed) "Reversed" else "Upright"
        val keywords = if (isReversed) card.reversedKeywords else card.uprightKeywords
        val meaning = if (isReversed) card.reversedMeaning else card.uprightMeaning
        val contextInfo = context?.let { "\n\nAdditional context: $it" } ?: ""
        
        return """
        You are an expert tarot reader providing interpretation for a single card.
        
        Card: ${card.name} ($orientation)
        Position: ${position.name}
        Position Meaning: ${position.meaning}
        Keywords: ${keywords.joinToString(", ")}
        Traditional Meaning: $meaning$contextInfo
        
        Provide a detailed, insightful interpretation of this card in this specific position.
        Focus on:
        1. How the card's energy manifests in this position
        2. Practical guidance and advice
        3. Spiritual and psychological insights
        4. Potential challenges and opportunities
        
        Keep the interpretation focused, meaningful, and empowering (2-3 paragraphs).
        """.trimIndent()
    }

    /**
     * Build prompt for daily card reading
     */
    private fun buildDailyCardPrompt(
        card: TarotCard,
        isReversed: Boolean
    ): String {
        val orientation = if (isReversed) "Reversed" else "Upright"
        val keywords = if (isReversed) card.reversedKeywords else card.uprightKeywords
        val meaning = if (isReversed) card.reversedMeaning else card.uprightMeaning
        
        return """
        You are an expert tarot reader providing a daily card reading.
        
        Today's Card: ${card.name} ($orientation)
        Keywords: ${keywords.joinToString(", ")}
        Meaning: $meaning
        
        Provide an inspiring daily reading that:
        1. Offers guidance for the day ahead
        2. Highlights key themes and energies to be aware of
        3. Suggests practical actions or mindset shifts
        4. Encourages reflection and personal growth
        
        Keep it concise, uplifting, and actionable (2-3 paragraphs).
        """.trimIndent()
    }

    /**
     * Build prompt for reading insights
     */
    private fun buildInsightsPrompt(recentReadings: List<String>): String {
        val readingsText = recentReadings.joinToString("\n\n---\n\n")
        
        return """
        You are an expert tarot reader analyzing patterns in recent readings.
        
        Recent readings:
        
        $readingsText
        
        Analyze these readings and provide insights about:
        1. Recurring themes or patterns
        2. Overall spiritual journey or life path
        3. Areas of growth or challenge
        4. Guidance for moving forward
        
        Be insightful, compassionate, and helpful (2-3 paragraphs).
        """.trimIndent()
    }

    /**
     * Fallback interpretation when AI is unavailable
     */
    private fun getFallbackCardInterpretation(card: TarotCard, isReversed: Boolean): String {
        val meaning = if (isReversed) card.reversedMeaning else card.uprightMeaning
        val keywords = if (isReversed) card.reversedKeywords else card.uprightKeywords
        
        return """
        ${card.name} ${if (isReversed) "(Reversed)" else "(Upright)"}
        
        $meaning
        
        Key themes: ${keywords.joinToString(", ")}
        
        Reflect on how these energies are manifesting in your life and what guidance they offer for your current situation.
        """.trimIndent()
    }

    /**
     * Check if API key is configured
     */
    fun isConfigured(): Boolean {
        return try {
            apiKey.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
}