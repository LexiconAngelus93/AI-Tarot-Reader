package com.tarotreader

import com.tarotreader.domain.model.UserPreferences
import com.tarotreader.domain.usecase.UpdateUserPreferencesUseCase
import com.tarotreader.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class UpdateUserPreferencesUseCaseTest {
    
    @Mock
    private lateinit var repository: UserPreferencesRepository
    
    private lateinit var useCase: UpdateUserPreferencesUseCase
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = UpdateUserPreferencesUseCase(repository)
    }
    
    @Test
    fun `invoke calls repository update with correct preferences`() = runBlocking {
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
        
        verify(repository).updateUserPreferences(preferences)
    }
}