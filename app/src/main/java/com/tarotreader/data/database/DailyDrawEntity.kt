package com.tarotreader.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "daily_draws")
data class DailyDrawEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: LocalDate,
    val cardId: String,
    val deckId: String,
    val orientation: String, // Stored as string to match database requirements
    val reflection: String
)