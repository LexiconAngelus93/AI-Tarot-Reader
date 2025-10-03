package com.tarotreader.domain.model

data class TarotDeck(
    val id: String,
    val name: String,
    val description: String,
    val coverImageUrl: String,
    val cardBackImageUrl: String,
    val numberOfCards: Int,
    val author: String?,
    val publisher: String?
)