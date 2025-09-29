package com.tarotreader.domain.model

import java.time.LocalDate

data class DailyDraw(
    val id: Long = 0,
    val date: LocalDate,
    val card: TarotCard,
    val deck: TarotDeck,
    val orientation: CardOrientation = CardOrientation.UPRIGHT,
    val reflection: String = ""
)

enum class CardOrientation {
    UPRIGHT,
    REVERSED
}