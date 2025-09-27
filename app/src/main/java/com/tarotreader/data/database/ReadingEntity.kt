package com.tarotreader.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "readings")
data class ReadingEntity(
    @PrimaryKey
    val id: String,
    val deckId: String,
    val spreadId: String,
    val date: Date,
    val interpretation: String,
    val eigenvalue: Double?, // For our special calculation feature
    val notes: String?
)