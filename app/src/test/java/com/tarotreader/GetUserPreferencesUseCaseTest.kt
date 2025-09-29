package com.tarotreader

import com.tarotreader.domain.model.UserPreferences
import com.tarotreader.domain.usecase.GetUserPreferencesUseCase
import com.tarotreader.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class GetUserPreferencesUseCaseTest {
    
    @Mock
    private lateinit var repository: UserPreferencesRepository
    
    private lateinit var useCase: GetUserPreferencesUseCase
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = GetUserPreferencesUseCase(repository)
    }
    
    @Test
    fun `invoke calls repository with correct id`() = runBlocking {
        val testId = 1
        
        useCase.invoke(testId)
        
        verify(repository).getUserPreferences(testId)
    }
    
    @Test
    fun `invoke returns user preferences from repository`() = runBlocking {
        val testId = 1
        val expectedPreferences = UserPreferences(
            id = testId,
            preferredDeckId = "rider_waite",
            enableAnimations = true,
            enableHaptics = true,
            darkMode = false,
            highContrastMode = false,
            largeTextMode = false,
            dailyDrawNotificationTime = "09:00",
            dailyDrawEnabled = true
        )
        
        `when`(repository.getUserPreferences(testId)).thenReturn(expectedPreferences)
        
        val result = useCase.invoke(testId)
        
        assertEquals(expectedPreferences, result)
    }
    
    @Test
    fun `invoke returns null when repository returns null`() = runBlocking {
        val testId = 1
        
        `when`(repository.getUserPreferences(testId)).thenReturn(null)
        
        val result = useCase.invoke(testId)
        
        assertNull(result)
    }
}