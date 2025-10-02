package com.tarotreader.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tarotreader.presentation.viewmodel.TarotViewModel
import kotlinx.coroutines.delay
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun CardSelectionScreen(navController: NavController, viewModel: TarotViewModel) {
    var isShuffling by remember { mutableStateOf(false) }
    var drawnCards by remember { mutableStateOf<List<DrawnCard>>(emptyList()) }
    var remainingDraws by remember { mutableStateOf(3) } // For three card spread
    
    // Animation for shuffling
    val shuffleRotation by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    // Placeholder card data
    val deckCards = listOf(
        "The Fool", "The Magician", "The High Priestess", "The Empress", "The Emperor",
        "The Hierophant", "The Lovers", "The Chariot", "Strength", "The Hermit"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Draw Your Cards",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Tap the deck to shuffle and draw cards for your reading",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Deck visualization with animation
        Card(
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 16.dp)
                .graphicsLayer {
                    rotationZ = if (isShuffling) shuffleRotation.value else 0f
                },
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            onClick = { 
                if (!isShuffling && remainingDraws > 0) {
                    isShuffling = true
                    // Simulate shuffling and drawing
                    shuffleAndDrawCards(deckCards, remainingDraws) { cardName, isReversed ->
                        drawnCards = drawnCards + DrawnCard(cardName, isReversed)
                        remainingDraws--
                        isShuffling = false
                    }
                }
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle Deck",
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = if (isShuffling) "Shuffling..." else "Tarot Deck",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "$remainingDraws cards remaining",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
        
        // Drawn cards display
        if (drawnCards.isNotEmpty()) {
            Text(
                text = "Drawn Cards",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(drawnCards) { card ->
                    // Animated card appearance
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Card image placeholder
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(end = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Card\nImage")
                            }
                            
                            Column {
                                Text(
                                    text = card.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (card.isReversed) "Reversed" else "Upright",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = { 
                // Generate a reading based on the drawn cards
                val cardDrawings = drawnCards.mapIndexed { index, card ->
                    com.tarotreader.domain.model.CardDrawing(
                        cardId = "card_$index",
                        positionId = "position_${index + 1}",
                        isReversed = card.isReversed
                    )
                }
                
                viewModel.generateReading("rider_waite", "three_card", cardDrawings)
                navController.navigate(Screen.ReadingResult.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = drawnCards.size == 3 // Enable when all cards are drawn
        ) {
            Text("Reveal Reading")
        }
    }
}

data class DrawnCard(
    val name: String,
    val isReversed: Boolean
)

fun shuffleAndDrawCards(
    deck: List<String>,
    remainingDraws: Int,
    onCardDrawn: (String, Boolean) -> Unit
) {
    // Sophisticated card shuffling with animation delay
    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
        // Shuffle animation time - gives visual feedback to user
        delay(1000)
        val randomIndex = (0 until deck.size).random()
        val isReversed = (0..1).random() == 1
        onCardDrawn(deck[randomIndex], isReversed)
    }
}