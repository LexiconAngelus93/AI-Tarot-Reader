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
    
    private fun getCupsMeaning(number: Int, upright: Boolean): String {
        return when (number) {
            1 -> if (upright) "New love, emotional beginnings, creativity, intuition. A time of emotional fulfillment and spiritual connection." 
                 else "Emotional loss, blocked creativity, emptiness. Difficulty expressing or receiving love."
            2 -> if (upright) "Partnership, unity, attraction, connection. A harmonious relationship or meaningful collaboration."
                 else "Imbalance, broken communication, tension. Disharmony in relationships or partnerships."
            3 -> if (upright) "Celebration, friendship, creativity, community. Joy shared with others and creative expression."
                 else "Overindulgence, gossip, isolation. Excess in social situations or feeling left out."
            4 -> if (upright) "Meditation, contemplation, apathy, reevaluation. Taking time to reassess emotional situations."
                 else "Sudden awareness, choosing happiness, acceptance. Breaking free from emotional stagnation."
            5 -> if (upright) "Loss, grief, disappointment, pessimism. Focusing on what's lost rather than what remains."
                 else "Acceptance, moving on, finding peace. Beginning to heal from emotional pain."
            6 -> if (upright) "Nostalgia, childhood memories, innocence, joy. Reconnecting with the past or inner child."
                 else "Living in the past, unrealistic expectations, naivety. Stuck in old patterns or memories."
            7 -> if (upright) "Choices, illusion, fantasy, wishful thinking. Many options but unclear which is real."
                 else "Clarity, making choices, alignment. Seeing through illusions and making decisions."
            8 -> if (upright) "Walking away, disappointment, abandonment. Leaving behind what no longer serves you."
                 else "Aimless drifting, fear of change, stagnation. Difficulty moving forward or letting go."
            9 -> if (upright) "Contentment, satisfaction, gratitude, wish fulfillment. Emotional and material satisfaction."
                 else "Greed, dissatisfaction, materialism. Never feeling satisfied despite having much."
            10 -> if (upright) "Harmony, happiness, alignment, family. Emotional fulfillment and lasting happiness."
                  else "Disharmony, broken relationships, misalignment. Family or relationship difficulties."
            else -> if (upright) "Emotional energy of Cups" else "Blocked emotional energy"
        }
    }
    
    private fun getWandsMeaning(number: Int, upright: Boolean): String {
        return when (number) {
            1 -> if (upright) "Inspiration, new opportunities, growth, potential. A spark of creative energy and new beginnings."
                 else "Delays, lack of direction, poor timing. Creative blocks or missed opportunities."
            2 -> if (upright) "Planning, decisions, progress, discovery. Making plans and preparing for the future."
                 else "Fear of unknown, lack of planning, bad planning. Hesitation or poor preparation."
            3 -> if (upright) "Expansion, foresight, leadership, progress. Looking ahead and taking action on plans."
                 else "Obstacles, delays, frustration. Plans not working out as expected."
            4 -> if (upright) "Celebration, harmony, marriage, home. Stability and joyful celebration of achievements."
                 else "Lack of support, transience, home conflicts. Instability or lack of harmony."
            5 -> if (upright) "Competition, conflict, tension, diversity. Healthy competition or conflicting interests."
                 else "Avoiding conflict, respecting differences, resolution. Finding common ground."
            6 -> if (upright) "Victory, success, public recognition, progress. Achievement and acknowledgment of efforts."
                 else "Excess pride, lack of recognition, punishment. Ego issues or feeling unappreciated."
            7 -> if (upright) "Challenge, competition, perseverance, defense. Standing your ground against opposition."
                 else "Exhaustion, giving up, overwhelmed. Feeling unable to continue the fight."
            8 -> if (upright) "Speed, action, movement, swift change. Rapid progress and quick developments."
                 else "Delays, frustration, resisting change. Slowed progress or impatience."
            9 -> if (upright) "Resilience, persistence, last stand, boundaries. Nearly at the goal despite challenges."
                 else "Exhaustion, struggle, overwhelm. Feeling unable to continue or defend."
            10 -> if (upright) "Burden, responsibility, hard work, stress. Carrying heavy responsibilities alone."
                  else "Inability to delegate, breakdown, burden. Overwhelmed by responsibilities."
            else -> if (upright) "Creative fire energy" else "Blocked creative energy"
        }
    }
    
    private fun getSwordsMeaning(number: Int, upright: Boolean): String {
        return when (number) {
            1 -> if (upright) "Clarity, breakthrough, new ideas, mental clarity. A moment of truth and clear thinking."
                 else "Confusion, chaos, lack of clarity. Mental fog or poor judgment."
            2 -> if (upright) "Difficult choices, stalemate, avoidance, denial. Avoiding a difficult decision."
                 else "Indecision, confusion, information overload. Unable to make a choice."
            3 -> if (upright) "Heartbreak, grief, sorrow, pain. Emotional pain and difficult truths."
                 else "Recovery, forgiveness, moving on. Beginning to heal from heartbreak."
            4 -> if (upright) "Rest, restoration, contemplation, recovery. Taking time to recuperate and reflect."
                 else "Restlessness, burnout, stress. Inability to rest or recover properly."
            5 -> if (upright) "Conflict, defeat, loss, betrayal. Winning at all costs or unfair victory."
                 else "Reconciliation, making amends, past resentment. Moving past conflict."
            6 -> if (upright) "Transition, change, moving on, release. Moving away from difficulties toward calmer waters."
                 else "Resistance to change, unfinished business. Stuck in the past or unable to move forward."
            7 -> if (upright) "Deception, betrayal, strategy, sneakiness. Acting in secret or being strategic."
                 else "Coming clean, rethinking approach, self-deceit. Revealing truth or reconsidering tactics."
            8 -> if (upright) "Restriction, imprisonment, victim mentality. Feeling trapped by circumstances or thoughts."
                 else "Freedom, release, self-acceptance. Breaking free from mental constraints."
            9 -> if (upright) "Anxiety, worry, nightmares, fear. Mental anguish and overwhelming thoughts."
                 else "Hope, reaching out, recovery. Beginning to overcome anxiety and fear."
            10 -> if (upright) "Endings, loss, crisis, betrayal. A painful ending or rock bottom moment."
                  else "Recovery, regeneration, resisting end. Beginning to heal from trauma."
            else -> if (upright) "Mental air energy" else "Blocked mental energy"
        }
    }
    
    private fun getPentaclesMeaning(number: Int, upright: Boolean): String {
        return when (number) {
            1 -> if (upright) "Opportunity, prosperity, new venture, manifestation. A new financial or material beginning."
                 else "Lost opportunity, missed chance, bad investment. Financial setback or poor planning."
            2 -> if (upright) "Balance, adaptability, time management, priorities. Juggling multiple responsibilities."
                 else "Imbalance, disorganization, overwhelm. Difficulty managing multiple priorities."
            3 -> if (upright) "Teamwork, collaboration, learning, implementation. Working with others toward a goal."
                 else "Lack of teamwork, disharmony, misalignment. Poor collaboration or conflicting goals."
            4 -> if (upright) "Conservation, security, control, scarcity mindset. Holding onto resources tightly."
                 else "Generosity, giving, sharing. Releasing control and sharing abundance."
            5 -> if (upright) "Financial loss, poverty, insecurity, worry. Material or spiritual hardship."
                 else "Recovery, charity, improvement. Beginning to overcome financial difficulties."
            6 -> if (upright) "Generosity, charity, sharing, fairness. Giving and receiving in balance."
                 else "Strings attached, inequality, debt. Imbalanced giving or receiving."
            7 -> if (upright) "Perseverance, investment, effort, patience. Working hard toward long-term goals."
                 else "Impatience, lack of reward, distractions. Frustration with slow progress."
            8 -> if (upright) "Apprenticeship, education, skill development, hard work. Mastering a craft or skill."
                 else "Lack of focus, poor quality, uninspired. Not putting in necessary effort."
            9 -> if (upright) "Abundance, luxury, self-sufficiency, financial independence. Enjoying the fruits of labor."
                 else "Overwork, materialism, self-worth. Tying worth to material success."
            10 -> if (upright) "Legacy, inheritance, family, tradition, wealth. Long-term success and stability."
                  else "Family disputes, financial failure, debt. Problems with inheritance or family wealth."
            else -> if (upright) "Material earth energy" else "Blocked material energy"
        }
    }
}