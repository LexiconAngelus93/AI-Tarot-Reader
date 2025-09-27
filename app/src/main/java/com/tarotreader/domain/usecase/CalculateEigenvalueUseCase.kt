package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.TarotCard
import com.tarotreader.domain.model.CardDrawing
import com.tarotreader.domain.model.SpreadPosition
import com.tarotreader.data.algorithm.EigenvalueCalculator

class CalculateEigenvalueUseCase {
    operator fun invoke(
        cardDrawings: List<CardDrawing>,
        cards: List<TarotCard>,
        spreadPositions: List<SpreadPosition>
    ): Double {
        return EigenvalueCalculator.calculateEigenvalue(
            cardDrawings.map { it.toDataModel() },
            cards.map { it.toDataModel() },
            spreadPositions.map { it.toDataModel() }
        )
    }
}

// Extension functions to convert domain models to data models for the algorithm
fun com.tarotreader.domain.model.CardDrawing.toDataModel(): com.tarotreader.data.model.CardDrawing {
    return com.tarotreader.data.model.CardDrawing(
        cardId = this.cardId,
        positionId = this.positionId,
        isReversed = this.isReversed
    )
}

fun com.tarotreader.domain.model.TarotCard.toDataModel(): com.tarotreader.data.model.TarotCard {
    return com.tarotreader.data.model.TarotCard(
        id = this.id,
        name = this.name,
        uprightMeaning = this.uprightMeaning,
        reversedMeaning = this.reversedMeaning,
        description = this.description,
        keywords = this.keywords,
        numerology = this.numerology,
        element = this.element,
        astrology = this.astrology,
        cardImageUrl = this.cardImageUrl,
        deckId = this.deckId
    )
}

fun com.tarotreader.domain.model.SpreadPosition.toDataModel(): com.tarotreader.data.model.SpreadPosition {
    return com.tarotreader.data.model.SpreadPosition(
        id = this.id,
        spreadId = this.spreadId,
        positionIndex = this.positionIndex,
        name = this.name,
        meaning = this.meaning,
        x = this.x,
        y = this.y
    )
}