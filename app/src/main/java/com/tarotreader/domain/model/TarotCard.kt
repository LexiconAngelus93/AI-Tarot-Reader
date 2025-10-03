package com.tarotreader.domain.model

data class TarotCard(
    val id: String,
    val name: String,
    val uprightMeaning: String,
    val reversedMeaning: String,
    val description: String,
    val uprightKeywords: List<String>,
    val reversedKeywords: List<String>,
    val numerology: String?,
    val element: String?,
    val astrology: String?,
    val cardImageUrl: String,
    val deckId: String,
    val arcana: String, // "Major" or "Minor"
    val suit: String? = null // For Minor Arcana: "Wands", "Cups", "Swords", "Pentacles"
)