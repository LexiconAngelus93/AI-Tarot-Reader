package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.DailyDraw
import com.tarotreader.domain.repository.DailyDrawRepository
import kotlinx.coroutines.flow.Flow

class GetRecentDailyDrawsUseCase(
    private val repository: DailyDrawRepository
) {
    operator fun invoke(): Flow<List<DailyDraw>> {
        return repository.getRecentDailyDraws()
    }
}