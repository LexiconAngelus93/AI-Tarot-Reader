package com.tarotreader.data.repository

import com.tarotreader.data.database.TarotDao
import com.tarotreader.data.database.UserPreferencesMapper
import com.tarotreader.domain.model.UserPreferences
import com.tarotreader.domain.repository.UserPreferencesRepository

class UserPreferencesRepositoryImpl(
    private val dao: TarotDao
) : UserPreferencesRepository {
    
    override suspend fun getUserPreferences(id: Int): UserPreferences? {
        val entity = dao.getUserPreferences(id)
        return entity?.let { UserPreferencesMapper.fromEntity(it) }
    }
    
    override suspend fun saveUserPreferences(preferences: UserPreferences) {
        val entity = UserPreferencesMapper.toEntity(preferences)
        dao.insertUserPreferences(entity)
    }
    
    override suspend fun updateUserPreferences(preferences: UserPreferences) {
        val entity = UserPreferencesMapper.toEntity(preferences)
        dao.updateUserPreferences(entity)
    }
}