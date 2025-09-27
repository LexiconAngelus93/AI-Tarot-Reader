package com.tarotreader.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun DeckSelectionScreen(navController: NavController) {
    var selectedDeckId by remember { mutableStateOf<String?>(null) }
    
    // Placeholder deck data
    val decks = listOf(
        TarotDeckItem(
            id = "rider_waite",
            name = "Rider-Waite Deck",
            description = "The classic Tarot deck created by A.E. Waite and Pamela Colman Smith. Known for its detailed imagery and accessible symbolism, making it perfect for beginners and experienced readers alike.",
            coverImageUrl = "https://example.com/rider-waite-cover.jpg",
            cardBackImageUrl = "https://example.com/rider-waite-back.jpg",
            numberOfCards = 78
        ),
        TarotDeckItem(
            id = "thoth",
            name = "Thoth Deck",
            description = "Created by Aleister Crowley and Lady Frieda Harris, this deck is known for its complex symbolism and esoteric interpretations. Perfect for advanced readers interested in Qabalistic and astrological associations.",
            coverImageUrl = "https://example.com/thoth-cover.jpg",
            cardBackImageUrl = "https://example.com/thoth-back.jpg",
            numberOfCards = 78
        ),
        TarotDeckItem(
            id = "morgan_greer",
            name = "Morgan-Greer Deck",
            description = "Vivid and colorful deck with rich symbolism. Created by artist and mystic Florence Morgan Greer, this deck offers unique interpretations while maintaining traditional Rider-Waite associations.",
            coverImageUrl = "https://example.com/morgan-greer-cover.jpg",
            cardBackImageUrl = "https://example.com/morgan-greer-back.jpg",
            numberOfCards = 78
        )
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select a Tarot Deck",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Choose a deck for your reading. Each deck has its own unique artistic style and interpretations.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyColumn {
            items(decks) { deck ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (selectedDeckId == deck.id) 8.dp else 2.dp
                    ),
                    onClick = { selectedDeckId = deck.id }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Deck cover image placeholder
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .padding(end = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Cover")
                        }
                        
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = deck.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = deck.description,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = "${deck.numberOfCards} cards",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        
                        if (selectedDeckId == deck.id) {
                            Icon(
                                imageVector = androidx.compose.material.icons.filled.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = { 
                if (selectedDeckId != null) {
                    navController.navigate(Screen.SpreadSelection.route)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = selectedDeckId != null
        ) {
            Text("Continue")
        }
    }
}

data class TarotDeckItem(
    val id: String,
    val name: String,
    val description: String,
    val coverImageUrl: String,
    val cardBackImageUrl: String,
    val numberOfCards: Int
)