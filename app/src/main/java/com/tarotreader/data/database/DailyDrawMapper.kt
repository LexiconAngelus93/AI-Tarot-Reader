package com.tarotreader.data.database

import com.tarotreader.domain.model.CardOrientation
import com.tarotreader.domain.model.DailyDraw
import com.tarotreader.domain.model.TarotCard
import com.tarotreader.domain.model.TarotDeck
import java.time.LocalDate

object DailyDrawMapper {
    fun toEntity(dailyDraw: DailyDraw): DailyDrawEntity {
        return DailyDrawEntity(
            id = dailyDraw.id,
            date = dailyDraw.date,
            cardId = dailyDraw.card.id,
            deckId = dailyDraw.deck.id,
            orientation = dailyDraw.orientation.name,
            reflection = dailyDraw.reflection
        )
    }
    
    fun fromEntity(entity: DailyDrawEntity, card: TarotCard, deck: TarotDeck): DailyDraw {
        return DailyDraw(
            id = entity.id,
            date = entity.date,
            card = card,
            deck = deck,
            orientation = try {
                CardOrientation.valueOf(entity.orientation)
            } catch (e: IllegalArgumentException) {
                CardOrientation.UPRIGHT
            },
            reflection = entity.reflection
        )
    }
}