package com.tarotreader.domain.repository

import com.tarotreader.domain.model.UserPreferences

interface UserPreferencesRepository {
    suspend fun getUserPreferences(id: Int = 1): UserPreferences?
    suspend fun saveUserPreferences(preferences: UserPreferences)
    suspend fun updateUserPreferences(preferences: UserPreferences)
}