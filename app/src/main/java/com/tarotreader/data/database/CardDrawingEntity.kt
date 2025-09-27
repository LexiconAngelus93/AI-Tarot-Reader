package com.tarotreader.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card_drawings")
data class CardDrawingEntity(
    @PrimaryKey
    val id: String,
    val readingId: String,
    val cardId: String,
    val positionId: String,
    val isReversed: Boolean
)