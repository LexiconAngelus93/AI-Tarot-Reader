package com.tarotreader.data.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "readings",
    indices = [
        Index(value = ["deckId"]),
        Index(value = ["spreadId"]),
        Index(value = ["date"])
    ]
)
data class ReadingEntity(
    @PrimaryKey
    val id: String,
    val deckId: String,
    val spreadId: String,
    val date: Long, // Stored as timestamp
    val interpretation: String,
    val eigenvalue: Double?, // For our special calculation feature
    val notes: String?
)