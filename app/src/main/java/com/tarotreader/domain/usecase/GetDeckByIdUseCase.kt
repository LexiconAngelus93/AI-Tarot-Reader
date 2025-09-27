package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.TarotDeck
import com.tarotreader.domain.repository.TarotRepository

class GetDeckByIdUseCase(private val repository: TarotRepository) {
    suspend operator fun invoke(deckId: String): TarotDeck? {
        return repository.getDeckById(deckId)
    }
}