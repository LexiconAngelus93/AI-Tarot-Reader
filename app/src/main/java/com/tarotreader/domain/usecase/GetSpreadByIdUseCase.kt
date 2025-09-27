package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.TarotSpread
import com.tarotreader.domain.repository.TarotRepository

class GetSpreadByIdUseCase(private val repository: TarotRepository) {
    suspend operator fun invoke(spreadId: String): TarotSpread? {
        return repository.getSpreadById(spreadId)
    }
}