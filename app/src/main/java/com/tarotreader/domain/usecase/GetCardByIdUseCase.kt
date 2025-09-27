package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.TarotCard
import com.tarotreader.domain.repository.TarotRepository

class GetCardByIdUseCase(private val repository: TarotRepository) {
    suspend operator fun invoke(cardId: String): TarotCard? {
        return repository.getCardById(cardId)
    }
}