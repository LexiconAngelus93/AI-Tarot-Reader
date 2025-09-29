package com.tarotreader.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tarotreader.domain.model.UserPreferences
import com.tarotreader.presentation.viewmodel.TarotViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: TarotViewModel = hiltViewModel()
) {
    val userPreferences by viewModel.userPreferences.collectAsState()
    val decks by viewModel.decks.collectAsState()
    
    var preferences by remember { mutableStateOf(userPreferences ?: UserPreferences()) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top app bar
        TopAppBar(
            title = { Text("Settings") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Preferred deck selection
        Text(
            text = "Preferred Deck",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (decks.isNotEmpty()) {
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = { }
            ) {
                TextField(
                    readOnly = true,
                    value = decks.find { it.id == preferences.preferredDeckId }?.name ?: "Rider-Waite",
                    onValueChange = { },
                    label = { Text("Select preferred deck") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = false,
                    onDismissRequest = { }
                ) {
                    decks.forEach { deck ->
                        DropdownMenuItem(
                            text = { Text(deck.name) },
                            onClick = { }
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Animations toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Enable Animations",
                style = MaterialTheme.typography.titleMedium
            )
            
            Switch(
                checked = preferences.enableAnimations,
                onCheckedChange = { checked ->
                    preferences = preferences.copy(enableAnimations = checked)
                    viewModel.updateUserPreferences(preferences)
                }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Haptics toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Enable Haptic Feedback",
                style = MaterialTheme.typography.titleMedium
            )
            
            Switch(
                checked = preferences.enableHaptics,
                onCheckedChange = { checked ->
                    preferences = preferences.copy(enableHaptics = checked)
                    viewModel.updateUserPreferences(preferences)
                }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Dark mode toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Dark Mode",
                style = MaterialTheme.typography.titleMedium
            )
            
            Switch(
                checked = preferences.darkMode,
                onCheckedChange = { checked ->
                    preferences = preferences.copy(darkMode = checked)
                    viewModel.updateUserPreferences(preferences)
                }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // High contrast mode toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "High Contrast Mode",
                style = MaterialTheme.typography.titleMedium
            )
            
            Switch(
                checked = preferences.highContrastMode,
                onCheckedChange = { checked ->
                    preferences = preferences.copy(highContrastMode = checked)
                    viewModel.updateUserPreferences(preferences)
                }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Large text mode toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Large Text Mode",
                style = MaterialTheme.typography.titleMedium
            )
            
            Switch(
                checked = preferences.largeTextMode,
                onCheckedChange = { checked ->
                    preferences = preferences.copy(largeTextMode = checked)
                    viewModel.updateUserPreferences(preferences)
                }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Daily draw notification settings
        Text(
            text = "Daily Card Draw",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Enable Daily Draws",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Switch(
                checked = preferences.dailyDrawEnabled,
                onCheckedChange = { checked ->
                    preferences = preferences.copy(dailyDrawEnabled = checked)
                    viewModel.updateUserPreferences(preferences)
                }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Notification time picker would go here in a full implementation
        Text(
            text = "Notification Time: ${preferences.dailyDrawNotificationTime}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}