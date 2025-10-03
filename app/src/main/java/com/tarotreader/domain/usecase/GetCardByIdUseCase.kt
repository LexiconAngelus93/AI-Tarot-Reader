package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.TarotCard
import com.tarotreader.domain.repository.TarotRepository
import javax.inject.Inject

class GetCardByIdUseCase @Inject constructor(
    private val repository: TarotRepository
) {
    suspend operator fun invoke(cardId: String): TarotCard? {
        return repository.getCardById(cardId)
    }
}