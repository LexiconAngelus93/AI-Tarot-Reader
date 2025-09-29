package com.tarotreader.domain.usecase

import com.tarotreader.domain.model.UserPreferences
import com.tarotreader.domain.repository.UserPreferencesRepository

class UpdateUserPreferencesUseCase(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(preferences: UserPreferences) {
        repository.updateUserPreferences(preferences)
    }
}