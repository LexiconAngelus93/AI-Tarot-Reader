package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.DailyDraw
import com.tarotreader.domain.repository.DailyDrawRepository
import java.time.LocalDate

class GetDailyDrawUseCase(
    private val repository: DailyDrawRepository
) {
    suspend operator fun invoke(date: LocalDate): DailyDraw? {
        return repository.getDailyDrawByDate(date)
    }
}