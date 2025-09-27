package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.Reading
import com.tarotreader.domain.repository.TarotRepository

class SaveReadingUseCase(private val repository: TarotRepository) {
    suspend operator fun invoke(reading: Reading) {
        repository.insertReading(reading)
    }
}