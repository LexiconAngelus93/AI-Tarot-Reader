package com.tarotreader.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun TarotDictionaryScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<CardCategory?>(null) }
    
    // Placeholder card data
    val allCards = listOf(
        TarotCardItem(
            id = "0_rider_waite",
            name = "The Fool",
            uprightMeaning = "New beginnings, innocence, spontaneity",
            reversedMeaning = "Recklessness, taken advantage of, inconsideration",
            description = "The Fool represents new beginnings, having faith in the future, being inexperienced, not knowing what to expect, having beginner's luck, improvising, believing in yourself and your abilities, being carefree, and having a free spirit.",
            keywords = listOf("beginnings", "innocence", "spontaneity", "faith"),
            cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/38/RWS_Tarot_00_Fool.jpg/200px-RWS_Tarot_00_Fool.jpg",
            category = CardCategory.MAJOR_ARCANA
        ),
        TarotCardItem(
            id = "1_rider_waite",
            name = "The Magician",
            uprightMeaning = "Manifestation, resourcefulness, power",
            reversedMeaning = "Manipulation, poor planning, untapped talents",
            description = "The Magician represents manifestation, resourcefulness, and power. The Magician shows you that you have all the tools and resources you need to make your dreams come true. It is a card of action, desires, and manifestation.",
            keywords = listOf("manifestation", "power", "resourcefulness", "action"),
            cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/RWS_Tarot_01_Magician.jpg/200px-RWS_Tarot_01_Magician.jpg",
            category = CardCategory.MAJOR_ARCANA
        ),
        TarotCardItem(
            id = "2_rider_waite",
            name = "The High Priestess",
            uprightMeaning = "Intuition, unconscious knowledge, divine feminine",
            reversedMeaning = "Secrets, disconnected from intuition, withdrawal",
            description = "The High Priestess represents intuition, unconscious knowledge, and the divine feminine. She is the guardian of the unconscious mind and represents our ability to access hidden knowledge and intuition.",
            keywords = listOf("intuition", "mystery", "unconscious", "divine feminine"),
            cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/88/RWS_Tarot_02_High_Priestess.jpg/200px-RWS_Tarot_02_High_Priestess.jpg",
            category = CardCategory.MAJOR_ARCANA
        ),
        TarotCardItem(
            id = "ace_cups_rider_waite",
            name = "Ace of Cups",
            uprightMeaning = "New feelings, spirituality, intuition",
            reversedMeaning = "Emotional loss, blocked creativity",
            description = "The Ace of Cups represents new feelings, spirituality, and intuition. This card deals with emotional and relational aspects of life.",
            keywords = listOf("cups", "1", "water"),
            cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/88/RWS_Tarot_01_Cups.jpg/200px-RWS_Tarot_01_Cups.jpg",
            category = CardCategory.CUPS
        ),
        TarotCardItem(
            id = "ace_wands_rider_waite",
            name = "Ace of Wands",
            uprightMeaning = "Creation, willpower, inspiration, desire",
            reversedMeaning = "Lack of energy, lack of passion, boredom",
            description = "The Ace of Wands represents creation, willpower, inspiration, and desire. This card deals with creative and spiritual aspects of life.",
            keywords = listOf("wands", "1", "fire"),
            cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f1/RWS_Tarot_01_Wands.jpg/200px-RWS_Tarot_01_Wands.jpg",
            category = CardCategory.WANDS
        )
    )
    
    val filteredCards = allCards.filter { card ->
        (searchQuery.isEmpty() || card.name.contains(searchQuery, ignoreCase = true) || 
         card.keywords.any { it.contains(searchQuery, ignoreCase = true) }) &&
        (selectedCategory == null || card.category == selectedCategory)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Tarot Dictionary",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search cards") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        
        // Category filter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Filter by Category",
                style = MaterialTheme.typography.bodyMedium
            )
            
            // In a real implementation, this would be a proper dropdown or filter chips
            Button(onClick = { 
                // Cycle through categories
                selectedCategory = when (selectedCategory) {
                    null -> CardCategory.MAJOR_ARCANA
                    CardCategory.MAJOR_ARCANA -> CardCategory.CUPS
                    CardCategory.CUPS -> CardCategory.WANDS
                    CardCategory.WANDS -> CardCategory.SWORDS
                    CardCategory.SWORDS -> CardCategory.PENTACLES
                    CardCategory.PENTACLES -> null
                }
            }) {
                Text(selectedCategory?.name ?: "All Categories")
            }
        }
        
        // Card browser
        if (searchQuery.isNotEmpty() || selectedCategory != null) {
            Text(
                text = "Search Results",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        LazyVerticalGrid(
            columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredCards) { card ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    onClick = { navController.navigate("${Screen.CardDetail.route}/${card.id}") }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Card image placeholder
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .padding(bottom = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Card\nImage")
                        }
                        
                        Text(
                            text = card.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        
        Button(
            onClick = { navController.navigate(Screen.LearningModule.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Tarot Learning Modules")
        }
        
        Button(
            onClick = { navController.navigate(Screen.Home.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Back to Home")
        }
    }
}

data class TarotCardItem(
    val id: String,
    val name: String,
    val uprightMeaning: String,
    val reversedMeaning: String,
    val description: String,
    val keywords: List<String>,
    val cardImageUrl: String,
    val category: CardCategory = CardCategory.MAJOR_ARCANA
)

enum class CardCategory(val name: String) {
    MAJOR_ARCANA("Major Arcana"),
    CUPS("Cups"),
    WANDS("Wands"),
    SWORDS("Swords"),
    PENTACLES("Pentacles")
}