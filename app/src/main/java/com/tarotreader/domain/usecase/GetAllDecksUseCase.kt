package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.TarotDeck
import com.tarotreader.domain.repository.TarotRepository
import kotlinx.coroutines.flow.Flow

class GetAllDecksUseCase(private val repository: TarotRepository) {
    operator fun invoke(): Flow<List<TarotDeck>> {
        return repository.getAllDecks()
    }
}