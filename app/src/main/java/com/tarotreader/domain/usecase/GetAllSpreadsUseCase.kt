package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.TarotSpread
import com.tarotreader.domain.repository.TarotRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSpreadsUseCase @Inject constructor(
    private val repository: TarotRepository
) {
    operator fun invoke(): Flow<List<TarotSpread>> {
        return repository.getAllSpreads()
    }
}