package com.tarotreader.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey
    val id: Int = 1,
    val preferredDeckId: String = "rider_waite",
    val enableAnimations: Boolean = true,
    val enableHaptics: Boolean = true,
    val darkMode: Boolean = false,
    val highContrastMode: Boolean = false,
    val largeTextMode: Boolean = false,
    val dailyDrawNotificationTime: String = "09:00",
    val dailyDrawEnabled: Boolean = true
)