package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.TarotCard
import com.tarotreader.domain.repository.TarotRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCardsByDeckUseCase @Inject constructor(
    private val repository: TarotRepository
) {
    operator fun invoke(deckId: String): Flow<List<TarotCard>> {
        return repository.getCardsByDeck(deckId)
    }
}