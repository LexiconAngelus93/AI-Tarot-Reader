package com.tarotreader.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tarot_spreads")
data class TarotSpreadEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val isCustom: Boolean,
    val numberOfPositions: Int
)