package com.tarotreader.data.database

import com.tarotreader.data.model.*

// TarotCard mappers
fun TarotCard.toTarotCardEntity(): TarotCardEntity {
    return TarotCardEntity(
        id = this.id,
        name = this.name,
        uprightMeaning = this.uprightMeaning,
        reversedMeaning = this.reversedMeaning,
        description = this.description,
        keywords = this.keywords.joinToString(","),
        numerology = this.numerology,
        element = this.element,
        astrology = this.astrology,
        cardImageUrl = this.cardImageUrl,
        deckId = this.deckId
    )
}

fun TarotCardEntity.toTarotCard(): TarotCard {
    return TarotCard(
        id = this.id,
        name = this.name,
        uprightMeaning = this.uprightMeaning,
        reversedMeaning = this.reversedMeaning,
        description = this.description,
        keywords = this.keywords.split(",").filter { it.isNotEmpty() },
        numerology = this.numerology,
        element = this.element,
        astrology = this.astrology,
        cardImageUrl = this.cardImageUrl,
        deckId = this.deckId
    )
}

// TarotDeck mappers
fun TarotDeck.toTarotDeckEntity(): TarotDeckEntity {
    return TarotDeckEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        coverImageUrl = this.coverImageUrl,
        cardBackImageUrl = this.cardBackImageUrl,
        numberOfCards = this.numberOfCards,
        deckType = this.deckType.name
    )
}

fun TarotDeckEntity.toTarotDeck(): TarotDeck {
    return TarotDeck(
        id = this.id,
        name = this.name,
        description = this.description,
        coverImageUrl = this.coverImageUrl,
        cardBackImageUrl = this.cardBackImageUrl,
        numberOfCards = this.numberOfCards,
        deckType = DeckType.valueOf(this.deckType)
    )
}

// TarotSpread mappers
fun TarotSpread.toTarotSpreadEntity(): TarotSpreadEntity {
    return TarotSpreadEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        imageUrl = this.imageUrl,
        isCustom = this.isCustom
    )
}

fun TarotSpreadEntity.toTarotSpread(): TarotSpread {
    return TarotSpread(
        id = this.id,
        name = this.name,
        description = this.description,
        positions = emptyList(), // Positions will be loaded separately
        imageUrl = this.imageUrl,
        isCustom = this.isCustom
    )
}

// SpreadPosition mappers
fun SpreadPosition.toSpreadPositionEntity(): SpreadPositionEntity {
    return SpreadPositionEntity(
        id = this.id,
        spreadId = this.spreadId,
        positionIndex = this.positionIndex,
        name = this.name,
        meaning = this.meaning,
        x = this.x,
        y = this.y
    )
}

fun SpreadPositionEntity.toSpreadPosition(): SpreadPosition {
    return SpreadPosition(
        id = this.id,
        spreadId = this.spreadId,
        positionIndex = this.positionIndex,
        name = this.name,
        meaning = this.meaning,
        x = this.x,
        y = this.y
    )
}

// Reading mappers
fun Reading.toReadingEntity(): ReadingEntity {
    return ReadingEntity(
        id = this.id,
        deckId = this.deckId,
        spreadId = this.spreadId,
        date = this.date,
        interpretation = this.interpretation,
        eigenvalue = this.eigenvalue,
        notes = this.notes
    )
}

fun ReadingEntity.toReading(): Reading {
    return Reading(
        id = this.id,
        deckId = this.deckId,
        spreadId = this.spreadId,
        date = this.date,
        cardDrawings = emptyList(), // Card drawings will be loaded separately
        interpretation = this.interpretation,
        eigenvalue = this.eigenvalue,
        notes = this.notes
    )
}

// CardDrawing mappers
fun CardDrawing.toCardDrawingEntity(readingId: String): CardDrawingEntity {
    return CardDrawingEntity(
        id = "${readingId}_${this.cardId}_${this.positionId}",
        readingId = readingId,
        cardId = this.cardId,
        positionId = this.positionId,
        isReversed = this.isReversed
    )
}

fun CardDrawingEntity.toCardDrawing(): CardDrawing {
    return CardDrawing(
        cardId = this.cardId,
        positionId = this.positionId,
        isReversed = this.isReversed
    )
}