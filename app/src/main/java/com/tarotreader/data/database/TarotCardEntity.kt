package com.tarotreader.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tarot_cards")
data class TarotCardEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val uprightMeaning: String,
    val reversedMeaning: String,
    val description: String,
    val keywords: String, // Stored as comma-separated values
    val numerology: String?,
    val element: String?,
    val astrology: String?,
    val cardImageUrl: String,
    val deckId: String
)