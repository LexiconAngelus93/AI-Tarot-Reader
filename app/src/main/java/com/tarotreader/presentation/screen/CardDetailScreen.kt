package com.tarotreader.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CardDetailScreen(
    navController: NavController, 
    cardId: String,
    viewModel: com.tarotreader.presentation.viewmodel.TarotViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    // Fetch card details from ViewModel
    val cards by viewModel.cards.collectAsState()
    val actualCard = cards.find { it.id == cardId }
    
    // Fallback to placeholder data if card not found in database
    val card = if (actualCard != null) {
        TarotCardItem(
            id = actualCard.id,
            name = actualCard.name,
            uprightMeaning = actualCard.uprightMeaning,
            reversedMeaning = actualCard.reversedMeaning,
            description = actualCard.description,
            keywords = actualCard.uprightKeywords + actualCard.reversedKeywords,
            cardImageUrl = actualCard.cardImageUrl,
            category = if (actualCard.arcana == "Major") CardCategory.MAJOR_ARCANA else CardCategory.MINOR_ARCANA
        )
    } else when (cardId) {
        "0_rider_waite" -> TarotCardItem(
            id = cardId,
            name = "The Fool",
            uprightMeaning = "New beginnings, innocence, spontaneity",
            reversedMeaning = "Recklessness, taken advantage of, inconsideration",
            description = "The Fool represents new beginnings, having faith in the future, being inexperienced, not knowing what to expect, having beginner's luck, improvising, believing in yourself and your abilities, being carefree, and having a free spirit.",
            keywords = listOf("beginnings", "innocence", "spontaneity", "faith"),
            cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/38/RWS_Tarot_00_Fool.jpg/200px-RWS_Tarot_00_Fool.jpg",
            category = CardCategory.MAJOR_ARCANA
        )
        "1_rider_waite" -> TarotCardItem(
            id = cardId,
            name = "The Magician",
            uprightMeaning = "Manifestation, resourcefulness, power",
            reversedMeaning = "Manipulation, poor planning, untapped talents",
            description = "The Magician represents manifestation, resourcefulness, and power. The Magician shows you that you have all the tools and resources you need to make your dreams come true. It is a card of action, desires, and manifestation.",
            keywords = listOf("manifestation", "power", "resourcefulness", "action"),
            cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/RWS_Tarot_01_Magician.jpg/200px-RWS_Tarot_01_Magician.jpg",
            category = CardCategory.MAJOR_ARCANA
        )
        "2_rider_waite" -> TarotCardItem(
            id = cardId,
            name = "The High Priestess",
            uprightMeaning = "Intuition, unconscious knowledge, divine feminine",
            reversedMeaning = "Secrets, disconnected from intuition, withdrawal",
            description = "The High Priestess represents intuition, unconscious knowledge, and the divine feminine. She is the guardian of the unconscious mind and represents our ability to access hidden knowledge and intuition.",
            keywords = listOf("intuition", "mystery", "unconscious", "divine feminine"),
            cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/88/RWS_Tarot_02_High_Priestess.jpg/200px-RWS_Tarot_02_High_Priestess.jpg",
            category = CardCategory.MAJOR_ARCANA
        )
        "ace_cups_rider_waite" -> TarotCardItem(
            id = cardId,
            name = "Ace of Cups",
            uprightMeaning = "New feelings, spirituality, intuition",
            reversedMeaning = "Emotional loss, blocked creativity",
            description = "The Ace of Cups represents new feelings, spirituality, and intuition. This card deals with emotional and relational aspects of life.",
            keywords = listOf("cups", "1", "water"),
            cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/88/RWS_Tarot_01_Cups.jpg/200px-RWS_Tarot_01_Cups.jpg",
            category = CardCategory.CUPS
        )
        "ace_wands_rider_waite" -> TarotCardItem(
            id = cardId,
            name = "Ace of Wands",
            uprightMeaning = "Creation, willpower, inspiration, desire",
            reversedMeaning = "Lack of energy, lack of passion, boredom",
            description = "The Ace of Wands represents creation, willpower, inspiration, and desire. This card deals with creative and spiritual aspects of life.",
            keywords = listOf("wands", "1", "fire"),
            cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f1/RWS_Tarot_01_Wands.jpg/200px-RWS_Tarot_01_Wands.jpg",
            category = CardCategory.WANDS
        )
        else -> TarotCardItem(
            id = cardId,
            name = "Unknown Card",
            uprightMeaning = "Meaning not available",
            reversedMeaning = "Reversed meaning not available",
            description = "Description not available",
            keywords = listOf("unknown"),
            cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/38/RWS_Tarot_00_Fool.jpg/200px-RWS_Tarot_00_Fool.jpg",
            category = CardCategory.MAJOR_ARCANA
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = card.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Card image
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Card Image Placeholder")
            }
        }
        
        // Card meanings
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Upright Meaning",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = card.uprightMeaning,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text(
                    text = "Reversed Meaning",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                Text(
                    text = card.reversedMeaning,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Card description
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = card.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Card keywords
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Keywords",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FlowRow {
                    card.keywords.forEach { keyword ->
                        Chip(
                            onClick = { 
                                // Navigate to dictionary with keyword filter
                                navController.navigate("dictionary?keyword=${keyword}")
                            },
                            label = { Text(keyword) },
                            modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                        )
                    }
                }
            }
        }
        
        // Additional information
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Additional Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Numerology: ${getNumerologyForCard(card)}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Element: ${cardElementForCategory(card.category)}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Astrology: ${getAstrologyForCard(card)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = { navController.navigate(Screen.TarotDictionary.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Dictionary")
        }
    }
}

@Composable
fun Chip(onClick: () -> Unit, label: @Composable () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            label()
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

fun getNumerologyForCard(card: TarotCardItem): String {
    return when (card.name) {
        "The Fool" -> "0"
        "The Magician" -> "1"
        "The High Priestess" -> "2"
        "The Empress" -> "3"
        "The Emperor" -> "4"
        "The Hierophant" -> "5"
        "The Lovers" -> "6"
        "The Chariot" -> "7"
        "Strength" -> "8"
        "The Hermit" -> "9"
        "Wheel of Fortune" -> "10"
        "Justice" -> "11"
        "The Hanged Man" -> "12"
        "Death" -> "13"
        "Temperance" -> "14"
        "The Devil" -> "15"
        "The Tower" -> "16"
        "The Star" -> "17"
        "The Moon" -> "18"
        "The Sun" -> "19"
        "Judgement" -> "20"
        "The World" -> "21"
        else -> if (card.name.startsWith("Ace")) "1" else "N/A"
    }
}

fun cardElementForCategory(category: CardCategory): String {
    return when (category) {
        CardCategory.CUPS -> "Water"
        CardCategory.WANDS -> "Fire"
        CardCategory.SWORDS -> "Air"
        CardCategory.PENTACLES -> "Earth"
        else -> "N/A"
    }
}

fun getAstrologyForCard(card: TarotCardItem): String {
    return when (card.name) {
        "The Fool" -> "Uranus"
        "The Magician" -> "Mercury"
        "The High Priestess" -> "Moon"
        "The Empress" -> "Venus"
        "The Emperor" -> "Aries"
        "The Hierophant" -> "Taurus"
        "The Lovers" -> "Gemini"
        "The Chariot" -> "Cancer"
        "Strength" -> "Leo"
        "The Hermit" -> "Virgo"
        "Wheel of Fortune" -> "Jupiter"
        "Justice" -> "Libra"
        "The Hanged Man" -> "Neptune"
        "Death" -> "Scorpio"
        "Temperance" -> "Sagittarius"
        "The Devil" -> "Capricorn"
        "The Tower" -> "Mars"
        "The Star" -> "Aquarius"
        "The Moon" -> "Pisces"
        "The Sun" -> "Sun"
        "Judgement" -> "Pluto"
        "The World" -> "Saturn"
        else -> "N/A"
    }
}