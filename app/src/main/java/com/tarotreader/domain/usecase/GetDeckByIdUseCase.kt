package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.TarotDeck
import com.tarotreader.domain.repository.TarotRepository
import javax.inject.Inject

class GetDeckByIdUseCase @Inject constructor(
    private val repository: TarotRepository
) {
    suspend operator fun invoke(deckId: String): TarotDeck? {
        return repository.getDeckById(deckId)
    }
}