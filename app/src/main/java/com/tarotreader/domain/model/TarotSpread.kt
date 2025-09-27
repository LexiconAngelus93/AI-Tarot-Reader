package com.tarotreader.domain.model

data class TarotSpread(
    val id: String,
    val name: String,
    val description: String,
    val positions: List<SpreadPosition>,
    val imageUrl: String?,
    val isCustom: Boolean = false
)

data class SpreadPosition(
    val id: String,
    val spreadId: String,
    val positionIndex: Int,
    val name: String,
    val meaning: String,
    val x: Float,
    val y: Float
)