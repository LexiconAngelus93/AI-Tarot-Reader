package com.tarotreader.data.model

data class TarotDeck(
    val id: String,
    val name: String,
    val description: String,
    val coverImageUrl: String,
    val cardBackImageUrl: String,
    val numberOfCards: Int,
    val deckType: DeckType
)

enum class DeckType {
    MAJOR_ARCANA_ONLY,
    FULL_DECK,
    CUSTOM
}