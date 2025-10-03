package com.tarotreader.data.model

data class SpreadPosition(
    val id: String,
    val spreadId: String,
    val positionIndex: Int,
    val name: String,
    val meaning: String,
    val x: Float,
    val y: Float
)