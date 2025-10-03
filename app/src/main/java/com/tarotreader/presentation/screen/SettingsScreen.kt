package com.tarotreader.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tarotreader.data.preferences.PreferencesManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    preferencesManager: PreferencesManager
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    // Collect preferences
    val themeMode by preferencesManager.themeModeFlow.collectAsState(initial = PreferencesManager.DEFAULT_THEME)
    val hapticFeedback by preferencesManager.hapticFeedbackFlow.collectAsState(initial = PreferencesManager.DEFAULT_HAPTIC_FEEDBACK)
    val soundEffects by preferencesManager.soundEffectsFlow.collectAsState(initial = PreferencesManager.DEFAULT_SOUND_EFFECTS)
    val autoSave by preferencesManager.autoSaveReadingsFlow.collectAsState(initial = PreferencesManager.DEFAULT_AUTO_SAVE)
    val showDescriptions by preferencesManager.showCardDescriptionsFlow.collectAsState(initial = PreferencesManager.DEFAULT_SHOW_DESCRIPTIONS)
    val aiEnabled by preferencesManager.aiInterpretationsEnabledFlow.collectAsState(initial = PreferencesManager.DEFAULT_AI_ENABLED)
    val offlineMode by preferencesManager.offlineModeFlow.collectAsState(initial = PreferencesManager.DEFAULT_OFFLINE_MODE)
    
    var showThemeDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Appearance Section
            SettingsSection(title = "Appearance") {
                SettingsItem(
                    icon = Icons.Default.Palette,
                    title = "Theme",
                    subtitle = themeMode.replaceFirstChar { it.uppercase() },
                    onClick = { showThemeDialog = true }
                )
            }
            
            Divider()
            
            // Reading Settings Section
            SettingsSection(title = "Reading Settings") {
                SwitchSettingsItem(
                    icon = Icons.Default.AutoAwesome,
                    title = "AI Interpretations",
                    subtitle = "Use AI for enhanced card interpretations",
                    checked = aiEnabled,
                    onCheckedChange = { 
                        scope.launch { preferencesManager.setAiInterpretationsEnabled(it) }
                    }
                )
                
                SwitchSettingsItem(
                    icon = Icons.Default.Save,
                    title = "Auto-save Readings",
                    subtitle = "Automatically save readings to history",
                    checked = autoSave,
                    onCheckedChange = { 
                        scope.launch { preferencesManager.setAutoSaveReadings(it) }
                    }
                )
                
                SwitchSettingsItem(
                    icon = Icons.Default.Description,
                    title = "Show Card Descriptions",
                    subtitle = "Display detailed card descriptions",
                    checked = showDescriptions,
                    onCheckedChange = { 
                        scope.launch { preferencesManager.setShowCardDescriptions(it) }
                    }
                )
            }
            
            Divider()
            
            // Feedback Section
            SettingsSection(title = "Feedback") {
                SwitchSettingsItem(
                    icon = Icons.Default.Vibration,
                    title = "Haptic Feedback",
                    subtitle = "Vibrate on interactions",
                    checked = hapticFeedback,
                    onCheckedChange = { 
                        scope.launch { preferencesManager.setHapticFeedback(it) }
                    }
                )
                
                SwitchSettingsItem(
                    icon = Icons.Default.VolumeUp,
                    title = "Sound Effects",
                    subtitle = "Play sounds during readings",
                    checked = soundEffects,
                    onCheckedChange = { 
                        scope.launch { preferencesManager.setSoundEffects(it) }
                    }
                )
            }
            
            Divider()
            
            // Data & Sync Section
            SettingsSection(title = "Data & Sync") {
                SwitchSettingsItem(
                    icon = Icons.Default.CloudOff,
                    title = "Offline Mode",
                    subtitle = "Disable network features",
                    checked = offlineMode,
                    onCheckedChange = { 
                        scope.launch { preferencesManager.setOfflineMode(it) }
                    }
                )
                
                SettingsItem(
                    icon = Icons.Default.Sync,
                    title = "Sync Data",
                    subtitle = "Sync readings with cloud",
                    onClick = { /* TODO: Implement sync */ }
                )
                
                SettingsItem(
                    icon = Icons.Default.Delete,
                    title = "Clear Cache",
                    subtitle = "Free up storage space",
                    onClick = { /* TODO: Implement cache clearing */ }
                )
            }
            
            Divider()
            
            // About Section
            SettingsSection(title = "About") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Version",
                    subtitle = "1.0.0",
                    onClick = { }
                )
                
                SettingsItem(
                    icon = Icons.Default.PrivacyTip,
                    title = "Privacy Policy",
                    subtitle = "View our privacy policy",
                    onClick = { /* TODO: Open privacy policy */ }
                )
                
                SettingsItem(
                    icon = Icons.Default.Gavel,
                    title = "Terms of Service",
                    subtitle = "View terms of service",
                    onClick = { /* TODO: Open terms */ }
                )
            }
        }
    }
    
    // Theme selection dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Select Theme") },
            text = {
                Column {
                    ThemeOption("System", themeMode == "system") {
                        scope.launch { 
                            preferencesManager.setThemeMode("system")
                            showThemeDialog = false
                        }
                    }
                    ThemeOption("Light", themeMode == "light") {
                        scope.launch { 
                            preferencesManager.setThemeMode("light")
                            showThemeDialog = false
                        }
                    }
                    ThemeOption("Dark", themeMode == "dark") {
                        scope.launch { 
                            preferencesManager.setThemeMode("dark")
                            showThemeDialog = false
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp)
        )
        content()
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SwitchSettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
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
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun ThemeOption(
    name: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = name)
        }
    }
}