package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.UserPreferences
import com.tarotreader.domain.repository.UserPreferencesRepository

class GetUserPreferencesUseCase(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(id: Int = 1): UserPreferences? {
        return repository.getUserPreferences(id)
    }
}