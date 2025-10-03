package com.tarotreader.data.database

import com.tarotreader.domain.model.*

// Extension functions to convert between domain and entity models

// TarotCard mappers
fun TarotCard.toEntity(): TarotCardEntity {
    return TarotCardEntity(
        id = this.id,
        name = this.name,
        uprightMeaning = this.uprightMeaning,
        reversedMeaning = this.reversedMeaning,
        description = this.description,
        uprightKeywords = this.uprightKeywords.joinToString(","),
        reversedKeywords = this.reversedKeywords.joinToString(","),
        numerology = this.numerology,
        element = this.element,
        astrology = this.astrology,
        cardImageUrl = this.cardImageUrl,
        deckId = this.deckId,
        arcana = this.arcana,
        suit = this.suit
    )
}

fun TarotCardEntity.toDomain(): TarotCard {
    return TarotCard(
        id = this.id,
        name = this.name,
        uprightMeaning = this.uprightMeaning,
        reversedMeaning = this.reversedMeaning,
        description = this.description,
        uprightKeywords = this.uprightKeywords.split(",").filter { it.isNotEmpty() },
        reversedKeywords = this.reversedKeywords.split(",").filter { it.isNotEmpty() },
        numerology = this.numerology,
        element = this.element,
        astrology = this.astrology,
        cardImageUrl = this.cardImageUrl,
        deckId = this.deckId,
        arcana = this.arcana,
        suit = this.suit
    )
}

// TarotDeck mappers
fun TarotDeck.toEntity(): TarotDeckEntity {
    return TarotDeckEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        coverImageUrl = this.coverImageUrl,
        cardBackImageUrl = this.cardBackImageUrl,
        numberOfCards = this.numberOfCards,
        author = this.author,
        publisher = this.publisher
    )
}

fun TarotDeckEntity.toDomain(): TarotDeck {
    return TarotDeck(
        id = this.id,
        name = this.name,
        description = this.description,
        coverImageUrl = this.coverImageUrl,
        cardBackImageUrl = this.cardBackImageUrl,
        numberOfCards = this.numberOfCards,
        author = this.author,
        publisher = this.publisher
    )
}

// TarotSpread mappers
fun TarotSpread.toEntity(): TarotSpreadEntity {
    return TarotSpreadEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        imageUrl = this.imageUrl,
        isCustom = this.isCustom,
        numberOfPositions = this.positions.size
    )
}

fun TarotSpreadEntity.toDomain(positions: List<SpreadPosition>): TarotSpread {
    return TarotSpread(
        id = this.id,
        name = this.name,
        description = this.description,
        positions = positions,
        imageUrl = this.imageUrl,
        isCustom = this.isCustom
    )
}

// SpreadPosition mappers
fun SpreadPosition.toEntity(spreadId: String): SpreadPositionEntity {
    return SpreadPositionEntity(
        id = this.id,
        spreadId = spreadId,
        positionOrder = this.positionOrder,
        name = this.name,
        meaning = this.meaning,
        x = this.x,
        y = this.y
    )
}

fun SpreadPositionEntity.toDomain(): SpreadPosition {
    return SpreadPosition(
        id = this.id,
        positionOrder = this.positionOrder,
        name = this.name,
        meaning = this.meaning,
        x = this.x,
        y = this.y
    )
}

// Reading mappers
fun Reading.toEntity(): ReadingEntity {
    return ReadingEntity(
        id = this.id,
        deckId = this.deckId,
        spreadId = this.spreadId,
        date = this.date.time,
        interpretation = this.interpretation,
        eigenvalue = this.eigenvalue,
        notes = this.notes
    )
}

fun ReadingEntity.toDomain(cardDrawings: List<CardDrawing>): Reading {
    return Reading(
        id = this.id,
        deckId = this.deckId,
        spreadId = this.spreadId,
        date = java.util.Date(this.date),
        cardDrawings = cardDrawings,
        interpretation = this.interpretation,
        eigenvalue = this.eigenvalue,
        notes = this.notes
    )
}

// CardDrawing mappers
fun CardDrawing.toEntity(readingId: String): CardDrawingEntity {
    return CardDrawingEntity(
        id = "${readingId}_${this.cardId}_${this.positionId}",
        readingId = readingId,
        cardId = this.cardId,
        positionId = this.positionId,
        isReversed = this.isReversed
    )
}

fun CardDrawingEntity.toDomain(): CardDrawing {
    return CardDrawing(
        cardId = this.cardId,
        positionId = this.positionId,
        isReversed = this.isReversed
    )
}