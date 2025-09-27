package com.tarotreader.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spread_positions")
data class SpreadPositionEntity(
    @PrimaryKey
    val id: String,
    val spreadId: String,
    val positionIndex: Int,
    val name: String,
    val meaning: String,
    val x: Float,
    val y: Float
)