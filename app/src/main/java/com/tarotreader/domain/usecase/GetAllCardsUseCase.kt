package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.TarotCard
import com.tarotreader.domain.repository.TarotRepository
import kotlinx.coroutines.flow.Flow

class GetAllCardsUseCase(private val repository: TarotRepository) {
    operator fun invoke(): Flow<List<TarotCard>> {
        return repository.getAllCards()
    }
}