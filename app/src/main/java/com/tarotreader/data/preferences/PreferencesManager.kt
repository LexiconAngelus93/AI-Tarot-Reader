package com.tarotreader.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tarot_preferences")

/**
 * Manager for app preferences using DataStore
 */
@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {
    
    private val dataStore = context.dataStore
    
    companion object {
        // Preference keys
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val ENABLE_HAPTIC_FEEDBACK = booleanPreferencesKey("enable_haptic_feedback")
        private val ENABLE_SOUND_EFFECTS = booleanPreferencesKey("enable_sound_effects")
        private val AUTO_SAVE_READINGS = booleanPreferencesKey("auto_save_readings")
        private val SHOW_CARD_DESCRIPTIONS = booleanPreferencesKey("show_card_descriptions")
        private val DEFAULT_DECK_ID = stringPreferencesKey("default_deck_id")
        private val ENABLE_AI_INTERPRETATIONS = booleanPreferencesKey("enable_ai_interpretations")
        private val OFFLINE_MODE = booleanPreferencesKey("offline_mode")
        private val SHOW_ONBOARDING = booleanPreferencesKey("show_onboarding")
        private val LAST_SYNC_TIMESTAMP = longPreferencesKey("last_sync_timestamp")
        
        // Default values
        const val DEFAULT_THEME = "system"
        const val DEFAULT_HAPTIC_FEEDBACK = true
        const val DEFAULT_SOUND_EFFECTS = false
        const val DEFAULT_AUTO_SAVE = true
        const val DEFAULT_SHOW_DESCRIPTIONS = true
        const val DEFAULT_AI_ENABLED = true
        const val DEFAULT_OFFLINE_MODE = false
        const val DEFAULT_SHOW_ONBOARDING = true
    }
    
    /**
     * Theme mode flow
     */
    val themeModeFlow: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[THEME_MODE] ?: DEFAULT_THEME
        }
    
    /**
     * Haptic feedback flow
     */
    val hapticFeedbackFlow: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[ENABLE_HAPTIC_FEEDBACK] ?: DEFAULT_HAPTIC_FEEDBACK
        }
    
    /**
     * Sound effects flow
     */
    val soundEffectsFlow: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[ENABLE_SOUND_EFFECTS] ?: DEFAULT_SOUND_EFFECTS
        }
    
    /**
     * Auto save readings flow
     */
    val autoSaveReadingsFlow: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[AUTO_SAVE_READINGS] ?: DEFAULT_AUTO_SAVE
        }
    
    /**
     * Show card descriptions flow
     */
    val showCardDescriptionsFlow: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[SHOW_CARD_DESCRIPTIONS] ?: DEFAULT_SHOW_DESCRIPTIONS
        }
    
    /**
     * Default deck ID flow
     */
    val defaultDeckIdFlow: Flow<String?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[DEFAULT_DECK_ID]
        }
    
    /**
     * AI interpretations enabled flow
     */
    val aiInterpretationsEnabledFlow: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[ENABLE_AI_INTERPRETATIONS] ?: DEFAULT_AI_ENABLED
        }
    
    /**
     * Offline mode flow
     */
    val offlineModeFlow: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[OFFLINE_MODE] ?: DEFAULT_OFFLINE_MODE
        }
    
    /**
     * Show onboarding flow
     */
    val showOnboardingFlow: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[SHOW_ONBOARDING] ?: DEFAULT_SHOW_ONBOARDING
        }
    
    /**
     * Last sync timestamp flow
     */
    val lastSyncTimestampFlow: Flow<Long> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[LAST_SYNC_TIMESTAMP] ?: 0L
        }
    
    // Setter methods
    
    suspend fun setThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }
    
    suspend fun setHapticFeedback(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[ENABLE_HAPTIC_FEEDBACK] = enabled
        }
    }
    
    suspend fun setSoundEffects(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[ENABLE_SOUND_EFFECTS] = enabled
        }
    }
    
    suspend fun setAutoSaveReadings(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUTO_SAVE_READINGS] = enabled
        }
    }
    
    suspend fun setShowCardDescriptions(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_CARD_DESCRIPTIONS] = enabled
        }
    }
    
    suspend fun setDefaultDeckId(deckId: String?) {
        dataStore.edit { preferences ->
            if (deckId != null) {
                preferences[DEFAULT_DECK_ID] = deckId
            } else {
                preferences.remove(DEFAULT_DECK_ID)
            }
        }
    }
    
    suspend fun setAiInterpretationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[ENABLE_AI_INTERPRETATIONS] = enabled
        }
    }
    
    suspend fun setOfflineMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[OFFLINE_MODE] = enabled
        }
    }
    
    suspend fun setShowOnboarding(show: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_ONBOARDING] = show
        }
    }
    
    suspend fun setLastSyncTimestamp(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_SYNC_TIMESTAMP] = timestamp
        }
    }
    
    /**
     * Clear all preferences
     */
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}