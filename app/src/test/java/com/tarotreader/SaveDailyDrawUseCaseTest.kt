package com.tarotreader

import com.tarotreader.domain.model.CardOrientation
import com.tarotreader.domain.model.DailyDraw
import com.tarotreader.domain.model.TarotCard
import com.tarotreader.domain.model.TarotDeck
import com.tarotreader.domain.usecase.SaveDailyDrawUseCase
import com.tarotreader.domain.repository.DailyDrawRepository
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class SaveDailyDrawUseCaseTest {
    
    @Mock
    private lateinit var repository: DailyDrawRepository
    
    private lateinit var useCase: SaveDailyDrawUseCase
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = SaveDailyDrawUseCase(repository)
    }
    
    @Test
    fun `invoke calls repository save with correct daily draw`() = runBlocking {
        val dailyDraw = DailyDraw(
            id = 1,
            date = LocalDate.of(2025, 1, 1),
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
            orientation = CardOrientation.REVERSED,
            reflection = "Today I feel like taking a new path"
        )
        
        useCase.invoke(dailyDraw)
        
        verify(repository).saveDailyDraw(dailyDraw)
    }
}