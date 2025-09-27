package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.TarotCard
import com.tarotreader.domain.repository.TarotRepository
import kotlinx.coroutines.flow.Flow

class GetCardsByDeckUseCase(private val repository: TarotRepository) {
    operator fun invoke(deckId: String): Flow<List<TarotCard>> {
        return repository.getCardsByDeck(deckId)
    }
}