package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.TarotCard
import com.tarotreader.domain.repository.TarotRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCardsUseCase @Inject constructor(
    private val repository: TarotRepository
) {
    operator fun invoke(): Flow<List<TarotCard>> {
        return repository.getAllCards()
    }
}