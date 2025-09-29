package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.DailyDraw
import com.tarotreader.domain.repository.DailyDrawRepository

class SaveDailyDrawUseCase(
    private val repository: DailyDrawRepository
) {
    suspend operator fun invoke(dailyDraw: DailyDraw) {
        repository.saveDailyDraw(dailyDraw)
    }
}