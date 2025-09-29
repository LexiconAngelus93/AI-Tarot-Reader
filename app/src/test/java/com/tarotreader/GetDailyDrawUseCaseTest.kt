package com.tarotreader

import com.tarotreader.domain.model.CardOrientation
import com.tarotreader.domain.model.DailyDraw
import com.tarotreader.domain.model.TarotCard
import com.tarotreader.domain.model.TarotDeck
import com.tarotreader.domain.usecase.GetDailyDrawUseCase
import com.tarotreader.domain.repository.DailyDrawRepository
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class GetDailyDrawUseCaseTest {
    
    @Mock
    private lateinit var repository: DailyDrawRepository
    
    private lateinit var useCase: GetDailyDrawUseCase
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = GetDailyDrawUseCase(repository)
    }
    
    @Test
    fun `invoke calls repository with correct date`() = runBlocking {
        val testDate = LocalDate.of(2025, 1, 1)
        
        useCase.invoke(testDate)
        
        verify(repository).getDailyDrawByDate(testDate)
    }
    
    @Test
    fun `invoke returns daily draw from repository`() = runBlocking {
        val testDate = LocalDate.of(2025, 1, 1)
        val expectedDraw = DailyDraw(
            id = 1,
            date = testDate,
            card = TarotCard(
                id = "card_1",
                name = "The Fool",
                meaning = "New beginnings, innocence, spontaneity",
                description = "The Fool represents new beginnings, having faith in the future, and taking risks.",
                deckId = "deck_1"
            ),
            deck = TarotDeck(
                id = "deck_1",
                name = "Rider-Waite",
                description = "The classic Rider-Waite deck"
            ),
            orientation = CardOrientation.UPRIGHT,
            reflection = ""
        )
        
        `when`(repository.getDailyDrawByDate(testDate)).thenReturn(expectedDraw)
        
        val result = useCase.invoke(testDate)
        
        assertEquals(expectedDraw, result)
    }
    
    @Test
    fun `invoke returns null when repository returns null`() = runBlocking {
        val testDate = LocalDate.of(2025, 1, 1)
        
        `when`(repository.getDailyDrawByDate(testDate)).thenReturn(null)
        
        val result = useCase.invoke(testDate)
        
        assertNull(result)
    }
}