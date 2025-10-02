package com.tarotreader.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tarot_decks")
data class TarotDeckEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val coverImageUrl: String,
    val cardBackImageUrl: String,
    val numberOfCards: Int,
    val author: String?,
    val publisher: String?
)