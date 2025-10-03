package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.Reading
import com.tarotreader.domain.repository.TarotRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllReadingsUseCase @Inject constructor(
    private val repository: TarotRepository
) {
    operator fun invoke(): Flow<List<Reading>> {
        return repository.getAllReadings()
    }
}