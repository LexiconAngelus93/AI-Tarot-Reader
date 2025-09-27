package com.tarotreader.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LearningModuleScreen(navController: NavController) {
    var currentLesson by remember { mutableStateOf(0) }
    val lessons = listOf(
        Lesson(
            title = "Understanding Card Meanings",
            content = "Tarot cards have both upright and reversed meanings. Upright meanings represent the natural flow of the card's energy, while reversed meanings indicate blocked or internalized energy. When interpreting a card, consider both its traditional meaning and how it relates to your specific situation.",
            example = "The Fool upright represents new beginnings and faith in the future, while reversed it might indicate recklessness or taking advantage of others."
        ),
        Lesson(
            title = "The Importance of Context",
            content = "Context is crucial in Tarot readings. The same card can have different meanings depending on the question asked, the spread used, and the surrounding cards. Always consider the querent's situation and the specific position of the card in the spread.",
            example = "The Tower in a career reading might indicate a sudden job loss, while in a relationship reading it could represent a breakup or major conflict."
        ),
        Lesson(
            title = "Numerology in Tarot",
            content = "Many Tarot cards have numerological significance that adds depth to their meanings. The Major Arcana cards are numbered 0-21, each with unique numerological properties. Minor Arcana cards also have numerical meanings from Ace (1) through 10.",
            example = "The number 7 represents introspection and inner work, which is reflected in cards like The Chariot and Temperance."
        ),
        Lesson(
            title = "Elemental Associations",
            content = "Each suit in the Minor Arcana corresponds to an element: Cups to Water, Wands to Fire, Swords to Air, and Pentacles to Earth. Understanding these elements helps deepen your interpretation of the cards and their interactions.",
            example = "Water (Cups) represents emotions and relationships, Fire (Wands) represents creativity and passion, Air (Swords) represents thoughts and communication, Earth (Pentacles) represents material and financial matters."
        ),
        Lesson(
            title = "Astrological Connections",
            content = "Many Tarot cards have astrological associations that provide additional layers of meaning. The Major Arcana cards are often linked to specific planets or zodiac signs, while court cards are associated with the elements and signs.",
            example = "The Empress is associated with Venus, representing love, beauty, and harmony. The Hierophant is associated with Taurus, representing stability and tradition."
        )
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Tarot Learning Modules",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Enhance your understanding of Tarot card meanings and interpretations",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Lesson ${currentLesson + 1}: ${lessons[currentLesson].title}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = lessons[currentLesson].content,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "Example:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = lessons[currentLesson].example,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { 
                    if (currentLesson > 0) {
                        currentLesson--
                    }
                },
                enabled = currentLesson > 0
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Lesson")
            }
            
            Text(
                text = "${currentLesson + 1} of ${lessons.size}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            IconButton(
                onClick = { 
                    if (currentLesson < lessons.size - 1) {
                        currentLesson++
                    }
                },
                enabled = currentLesson < lessons.size - 1
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Lesson")
            }
        }
        
        Button(
            onClick = { navController.navigate(Screen.TarotDictionary.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Back to Dictionary")
        }
    }
}

data class Lesson(
    val title: String,
    val content: String,
    val example: String
)