package com.tarotreader.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tarotreader.presentation.navigation.Screen
import com.tarotreader.utils.AccessibilityHelper

@Composable
fun HomeScreen(navController: NavController) {
    val accessibleColors = AccessibilityHelper.getAccessibleColors()
    val accessibleTextSizes = AccessibilityHelper.getAccessibleTextSizes()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "TarotReader",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = androidx.compose.ui.unit.sp(accessibleTextSizes.extraLarge)
                ),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 32.dp)
            )
        }
        
        item {
            Text(
                text = "Welcome to your digital Tarot reading companion. Choose an option below to begin your journey.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = androidx.compose.ui.unit.sp(accessibleTextSizes.large)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
        
        item {
            FeatureCard(
                icon = Icons.Default.AutoAwesome,
                title = "Guided Digital Reading",
                description = "Select from curated decks and spreads for a traditional digital Tarot experience",
                onClick = { navController.navigate(Screen.DeckSelection.route) },
                modifier = Modifier.padding(bottom = 16.dp),
                accessibleColors = accessibleColors,
                accessibleTextSizes = accessibleTextSizes
            )
        }
        
        item {
            FeatureCard(
                icon = Icons.Default.Camera,
                title = "AI-Powered Spread Analysis",
                description = "Take a photo of your physical spread and get an AI-generated interpretation",
                onClick = { navController.navigate(Screen.Camera.route) },
                modifier = Modifier.padding(bottom = 16.dp),
                accessibleColors = accessibleColors,
                accessibleTextSizes = accessibleTextSizes
            )
        }
        
        item {
            FeatureCard(
                icon = Icons.Default.Edit,
                title = "Create Custom Spread",
                description = "Design your own spreads with custom positions and meanings",
                onClick = { navController.navigate(Screen.SpreadCreator.route) },
                modifier = Modifier.padding(bottom = 16.dp),
                accessibleColors = accessibleColors,
                accessibleTextSizes = accessibleTextSizes
            )
        }
        
        item {
            FeatureCard(
                icon = Icons.Default.History,
                title = "Reading Diary & History",
                description = "Browse past readings, search by date or deck, and reflect on your Tarot journey",
                onClick = { navController.navigate(Screen.ReadingHistory.route) },
                modifier = Modifier.padding(bottom = 16.dp),
                accessibleColors = accessibleColors,
                accessibleTextSizes = accessibleTextSizes
            )
        }
        
        item {
            FeatureCard(
                icon = Icons.Default.Book,
                title = "Tarot Dictionary",
                description = "Explore card meanings, symbolism, and deepen your understanding",
                onClick = { navController.navigate(Screen.TarotDictionary.route) },
                accessibleColors = accessibleColors,
                accessibleTextSizes = accessibleTextSizes
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accessibleColors: com.tarotreader.utils.AccessibleColors,
    accessibleTextSizes: com.tarotreader.utils.AccessibleTextSizes
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accessibleColors.primary,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp)
            )
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = androidx.compose.ui.unit.sp(accessibleTextSizes.large)
                    ),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = androidx.compose.ui.unit.sp(accessibleTextSizes.medium)
                    ),
                    color = accessibleColors.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}