package com.tarotreader.data.model

import java.time.LocalDate

data class DailyDraw(
    val id: Long = 0,
    val date: LocalDate,
    val cardId: String,
    val deckId: String,
    val orientation: CardOrientation = CardOrientation.UPRIGHT,
    val reflection: String = ""
)

enum class CardOrientation {
    UPRIGHT,
    REVERSED
}