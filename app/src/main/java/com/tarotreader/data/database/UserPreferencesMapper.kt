package com.tarotreader.data.database

import com.tarotreader.domain.model.UserPreferences

object UserPreferencesMapper {
    fun toEntity(preferences: UserPreferences): UserPreferencesEntity {
        return UserPreferencesEntity(
            id = preferences.id,
            preferredDeckId = preferences.preferredDeckId,
            enableAnimations = preferences.enableAnimations,
            enableHaptics = preferences.enableHaptics,
            darkMode = preferences.darkMode,
            highContrastMode = preferences.highContrastMode,
            largeTextMode = preferences.largeTextMode,
            dailyDrawNotificationTime = preferences.dailyDrawNotificationTime,
            dailyDrawEnabled = preferences.dailyDrawEnabled
        )
    }
    
    fun fromEntity(entity: UserPreferencesEntity): UserPreferences {
        return UserPreferences(
            id = entity.id,
            preferredDeckId = entity.preferredDeckId,
            enableAnimations = entity.enableAnimations,
            enableHaptics = entity.enableHaptics,
            darkMode = entity.darkMode,
            highContrastMode = entity.highContrastMode,
            largeTextMode = entity.largeTextMode,
            dailyDrawNotificationTime = entity.dailyDrawNotificationTime,
            dailyDrawEnabled = entity.dailyDrawEnabled
        )
    }
}