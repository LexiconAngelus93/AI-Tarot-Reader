package com.tarotreader

import com.tarotreader.domain.model.UserPreferences
import com.tarotreader.domain.usecase.SaveUserPreferencesUseCase
import com.tarotreader.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class SaveUserPreferencesUseCaseTest {
    
    @Mock
    private lateinit var repository: UserPreferencesRepository
    
    private lateinit var useCase: SaveUserPreferencesUseCase
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = SaveUserPreferencesUseCase(repository)
    }
    
    @Test
    fun `invoke calls repository save with correct preferences`() = runBlocking {
        val preferences = UserPreferences(
            id = 1,
            preferredDeckId = "rider_waite",
            enableAnimations = true,
            enableHaptics = true,
            darkMode = false,
            highContrastMode = false,
            largeTextMode = false,
            dailyDrawNotificationTime = "09:00",
            dailyDrawEnabled = true
        )
        
        useCase.invoke(preferences)
        
        verify(repository).saveUserPreferences(preferences)
    }
}