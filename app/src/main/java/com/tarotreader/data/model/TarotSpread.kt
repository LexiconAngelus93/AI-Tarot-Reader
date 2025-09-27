package com.tarotreader.data.model

data class TarotSpread(
    val id: String,
    val name: String,
    val description: String,
    val positions: List<SpreadPosition>,
    val imageUrl: String?,
    val isCustom: Boolean = false
)