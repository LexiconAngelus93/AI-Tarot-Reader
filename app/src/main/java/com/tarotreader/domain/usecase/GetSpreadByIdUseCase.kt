package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.TarotSpread
import com.tarotreader.domain.repository.TarotRepository
import javax.inject.Inject

class GetSpreadByIdUseCase @Inject constructor(
    private val repository: TarotRepository
) {
    suspend operator fun invoke(spreadId: String): TarotSpread? {
        return repository.getSpreadById(spreadId)
    }
}