package com.tarotreader.data.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tarot_cards",
    indices = [
        Index(value = ["deckId"]),
        Index(value = ["arcana"]),
        Index(value = ["suit"]),
        Index(value = ["name"])
    ]
)
data class TarotCardEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val uprightMeaning: String,
    val reversedMeaning: String,
    val description: String,
    val uprightKeywords: String, // Stored as comma-separated values
    val reversedKeywords: String, // Stored as comma-separated values
    val numerology: String?,
    val element: String?,
    val astrology: String?,
    val cardImageUrl: String,
    val deckId: String,
    val arcana: String, // "Major" or "Minor"
    val suit: String? // For Minor Arcana: "Wands", "Cups", "Swords", "Pentacles"
)