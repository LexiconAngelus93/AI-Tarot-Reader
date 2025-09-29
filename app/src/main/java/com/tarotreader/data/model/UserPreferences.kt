package com.tarotreader.data.model

data class UserPreferences(
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