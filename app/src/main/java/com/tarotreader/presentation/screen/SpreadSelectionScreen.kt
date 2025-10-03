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
fun SpreadSelectionScreen(navController: NavController) {
    var selectedSpreadId by remember { mutableStateOf<String?>(null) }
    
    // Placeholder spread data
    val spreads = listOf(
        TarotSpreadItem(
            id = "three_card",
            name = "Three Card Spread",
            description = "A simple yet powerful spread representing the past, present, and future. Perfect for gaining insight into a situation's progression.",
            positionCount = 3,
            imageUrl = "https://images.unsplash.com/photo-1551269901-5c5e14c25df7?w=400&h=300&fit=crop"
        ),
        TarotSpreadItem(
            id = "celtic_cross",
            name = "Celtic Cross",
            description = "The most popular ten-card spread, offering comprehensive insight into any situation. Covers past, present, future, and various aspects of the querent's life.",
            positionCount = 10,
            imageUrl = "https://images.unsplash.com/photo-1518531933037-91b2f5f229cc?w=400&h=300&fit=crop"
        ),
        TarotSpreadItem(
            id = "horseshoe",
            name = "Horseshoe Spread",
            description = "A seven-card spread that provides insight into the querent's current situation and possible outcomes. Cards are laid out in a horseshoe shape.",
            positionCount = 7,
            imageUrl = "https://images.unsplash.com/photo-1509043759401-136742328bb3?w=400&h=300&fit=crop"
        ),
        TarotSpreadItem(
            id = "daily_draw",
            name = "Daily Draw",
            description = "A single card draw for daily guidance and insight. Simple but effective for quick spiritual direction.",
            positionCount = 1,
            imageUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=400&h=300&fit=crop"
        )
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select a Tarot Spread",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Choose a spread layout for your reading. Each spread offers different insights and perspectives.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyColumn {
            items(spreads) { spread ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (selectedSpreadId == spread.id) 8.dp else 2.dp
                    ),
                    onClick = { selectedSpreadId = spread.id }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Spread image placeholder
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .padding(end = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Layout")
                        }
                        
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = spread.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = spread.description,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = "${spread.positionCount} positions",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        
                        if (selectedSpreadId == spread.id) {
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
                if (selectedSpreadId != null) {
                    navController.navigate(Screen.CardSelection.route)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = selectedSpreadId != null
        ) {
            Text("Continue")
        }
    }
}

data class TarotSpreadItem(
    val id: String,
    val name: String,
    val description: String,
    val positionCount: Int,
    val imageUrl: String
)