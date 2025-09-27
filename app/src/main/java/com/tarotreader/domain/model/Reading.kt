package com.tarotreader.domain.model

import java.util.Date

data class Reading(
    val id: String,
    val deckId: String,
    val spreadId: String,
    val date: Date,
    val cardDrawings: List<CardDrawing>,
    val interpretation: String,
    val eigenvalue: Double?, // For our special calculation feature
    val notes: String?
)

data class CardDrawing(
    val cardId: String,
    val positionId: String,
    val isReversed: Boolean
)