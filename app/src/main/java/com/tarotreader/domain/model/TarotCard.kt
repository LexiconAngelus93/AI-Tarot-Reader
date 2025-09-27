package com.tarotreader.domain.model

data class TarotCard(
    val id: String,
    val name: String,
    val uprightMeaning: String,
    val reversedMeaning: String,
    val description: String,
    val keywords: List<String>,
    val numerology: String?,
    val element: String?,
    val astrology: String?,
    val cardImageUrl: String,
    val deckId: String
)