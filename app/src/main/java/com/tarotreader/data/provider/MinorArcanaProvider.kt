package com.tarotreader.data.provider

import com.tarotreader.data.model.TarotCard

object MinorArcanaProvider {
    
    fun getSuitsCards(deckId: String): List<TarotCard> {
        val suits = listOf("Cups", "Wands", "Swords", "Pentacles")
        val cards = mutableListOf<TarotCard>()
        
        suits.forEach { suit ->
            // Ace through 10
            for (i in 1..10) {
                cards.add(createNumberCard(i, suit, deckId))
            }
            
            // Court cards
            cards.add(createCourtCard("Page", suit, deckId))
            cards.add(createCourtCard("Knight", suit, deckId))
            cards.add(createCourtCard("Queen", suit, deckId))
            cards.add(createCourtCard("King", suit, deckId))
        }
        
        return cards
    }
    
    private fun createNumberCard(number: Int, suit: String, deckId: String): TarotCard {
        val suitSymbol = getSuitSymbol(suit)
        val cardName = when (number) {
            1 -> "Ace of $suit"
            else -> "$number of $suit"
        }
        
        return TarotCard(
            id = "${number}_${suit.lowercase()}_${deckId}",
            name = cardName,
            uprightMeaning = getUprightMeaning(number, suit),
            reversedMeaning = getReversedMeaning(number, suit),
            description = getDescription(number, suit),
            keywords = getKeywords(number, suit),
            numerology = if (number == 1) "1" else null,
            element = getElement(suit),
            astrology = getAstrology(suit),
            cardImageUrl = "https://example.com/${cardName.lowercase().replace(" ", "_")}.jpg",
            deckId = deckId
        )
    }
    
    private fun createCourtCard(type: String, suit: String, deckId: String): TarotCard {
        val suitSymbol = getSuitSymbol(suit)
        val cardName = "$type of $suit"
        
        return TarotCard(
            id = "${type.lowercase()}_${suit.lowercase()}_${deckId}",
            name = cardName,
            uprightMeaning = getCourtUprightMeaning(type, suit),
            reversedMeaning = getCourtReversedMeaning(type, suit),
            description = getCourtDescription(type, suit),
            keywords = getCourtKeywords(type, suit),
            numerology = null,
            element = getElement(suit),
            astrology = getCourtAstrology(type, suit),
            cardImageUrl = "https://example.com/${cardName.lowercase().replace(" ", "_")}.jpg",
            deckId = deckId
        )
    }
    
    private fun getSuitSymbol(suit: String): String {
        return when (suit) {
            "Cups" -> "🍷"
            "Wands" -> "🌿"
            "Swords" -> "⚔️"
            "Pentacles" -> "🪙"
            else -> ""
        }
    }
    
    private fun getUprightMeaning(number: Int, suit: String): String {
        return when (suit) {
            "Cups" -> getCupsMeaning(number, true)
            "Wands" -> getWandsMeaning(number, true)
            "Swords" -> getSwordsMeaning(number, true)
            "Pentacles" -> getPentaclesMeaning(number, true)
            else -> "General meaning for $number of $suit"
        }
    }
    
    private fun getReversedMeaning(number: Int, suit: String): String {
        return when (suit) {
            "Cups" -> getCupsMeaning(number, false)
            "Wands" -> getWandsMeaning(number, false)
            "Swords" -> getSwordsMeaning(number, false)
            "Pentacles" -> getPentaclesMeaning(number, false)
            else -> "Reversed meaning for $number of $suit"
        }
    }
    
    private fun getDescription(number: Int, suit: String): String {
        return "The $number of $suit represents ${getSuitElement(suit)} energy in the context of $suit. " +
                "This card deals with ${getSuitArea(suit)} aspects of life."
    }
    
    private fun getKeywords(number: Int, suit: String): List<String> {
        return listOf(suit.lowercase(), number.toString(), getSuitElement(suit).lowercase())
    }
    
    private fun getElement(suit: String): String {
        return when (suit) {
            "Cups" -> "Water"
            "Wands" -> "Fire"
            "Swords" -> "Air"
            "Pentacles" -> "Earth"
            else -> ""
        }
    }
    
    private fun getAstrology(suit: String): String {
        return when (suit) {
            "Cups" -> "Water Signs"
            "Wands" -> "Fire Signs"
            "Swords" -> "Air Signs"
            "Pentacles" -> "Earth Signs"
            else -> ""
        }
    }
    
    private fun getCourtUprightMeaning(type: String, suit: String): String {
        return "$type of $suit upright meaning"
    }
    
    private fun getCourtReversedMeaning(type: String, suit: String): String {
        return "$type of $suit reversed meaning"
    }
    
    private fun getCourtDescription(type: String, suit: String): String {
        return "The $type of $suit represents a person or energy related to ${getSuitArea(suit)}."
    }
    
    private fun getCourtKeywords(type: String, suit: String): List<String> {
        return listOf(suit.lowercase(), type.lowercase(), "person", "court")
    }
    
    private fun getCourtAstrology(type: String, suit: String): String {
        return when (type) {
            "Page" -> "Mercury in ${getSuitSign(suit)}"
            "Knight" -> "${getSuitSign(suit)} in Fire"
            "Queen" -> "${getSuitSign(suit)} in Water"
            "King" -> "${getSuitSign(suit)} in Cardinal"
            else -> ""
        }
    }
    
    private fun getSuitElement(suit: String): String {
        return when (suit) {
            "Cups" -> "Water"
            "Wands" -> "Fire"
            "Swords" -> "Air"
            "Pentacles" -> "Earth"
            else -> ""
        }
    }
    
    private fun getSuitArea(suit: String): String {
        return when (suit) {
            "Cups" -> "emotional and relational"
            "Wands" -> "creative and spiritual"
            "Swords" -> "mental and communication"
            "Pentacles" -> "material and financial"
            else -> ""
        }
    }
    
    private fun getSuitSign(suit: String): String {
        return when (suit) {
            "Cups" -> "Scorpio"
            "Wands" -> "Leo"
            "Swords" -> "Aquarius"
            "Pentacles" -> "Taurus"
            else -> ""
        }
    }
    
    // These would be more detailed in a real implementation
    private fun getCupsMeaning(number: Int, upright: Boolean): String {
        return if (upright) "Cups meaning for $number" else "Reversed Cups meaning for $number"
    }
    
    private fun getWandsMeaning(number: Int, upright: Boolean): String {
        return if (upright) "Wands meaning for $number" else "Reversed Wands meaning for $number"
    }
    
    private fun getSwordsMeaning(number: Int, upright: Boolean): String {
        return if (upright) "Swords meaning for $number" else "Reversed Swords meaning for $number"
    }
    
    private fun getPentaclesMeaning(number: Int, upright: Boolean): String {
        return if (upright) "Pentacles meaning for $number" else "Reversed Pentacles meaning for $number"
    }
}