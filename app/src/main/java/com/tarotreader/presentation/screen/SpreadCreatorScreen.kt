package com.tarotreader.presentation.screen

import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SpreadCreatorScreen(navController: NavController) {
    var spreadName by remember { mutableStateOf("") }
    var spreadDescription by remember { mutableStateOf("") }
    var positions by remember { mutableStateOf<List<SpreadPosition>>(emptyList()) }
    var isCreatingPosition by remember { mutableStateOf(false) }
    var newPositionName by remember { mutableStateOf("") }
    var newPositionMeaning by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Create Custom Spread",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = spreadName,
            onValueChange = { spreadName = it },
            label = { Text("Spread Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = spreadDescription,
            onValueChange = { spreadDescription = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            maxLines = 3
        )
        
        Text(
            text = "Spread Positions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Spread canvas with drag-and-drop
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(bottom = 16.dp)
                .border(1.dp, Color.Gray),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (positions.isEmpty()) {
                    Text("Drag positions onto the canvas to create your spread")
                } else {
                    // In a real implementation, this would be a canvas with draggable positions
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Spread Canvas Placeholder")
                        Text("${positions.size} positions placed")
                    }
                }
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Position Library",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(onClick = { 
                isCreatingPosition = true
                newPositionName = ""
                newPositionMeaning = ""
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Position")
            }
        }
        
        // Position creation dialog
        if (isCreatingPosition) {
            AlertDialog(
                onDismissRequest = { isCreatingPosition = false },
                title = { Text("Create New Position") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newPositionName,
                            onValueChange = { newPositionName = it },
                            label = { Text("Position Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        
                        OutlinedTextField(
                            value = newPositionMeaning,
                            onValueChange = { newPositionMeaning = it },
                            label = { Text("Position Meaning") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { 
                            if (newPositionName.isNotEmpty() && newPositionMeaning.isNotEmpty()) {
                                positions = positions + SpreadPosition(
                                    id = "position_${positions.size}",
                                    spreadId = "",
                                    positionIndex = positions.size,
                                    name = newPositionName,
                                    meaning = newPositionMeaning,
                                    x = 0f,
                                    y = 0f
                                )
                                isCreatingPosition = false
                            }
                        },
                        enabled = newPositionName.isNotEmpty() && newPositionMeaning.isNotEmpty()
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    Button(onClick = { isCreatingPosition = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        // Position library list
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(positions) { position ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DragHandle,
                            contentDescription = "Drag Handle",
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = position.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = position.meaning,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        IconButton(onClick = { 
                            // Remove position
                            positions = positions.filter { it.id != position.id }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Position")
                        }
                    }
                }
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { 
                    // In a real implementation, we would save the spread here
                    navController.navigate(Screen.Home.route)
                },
                enabled = spreadName.isNotEmpty() && positions.isNotEmpty()
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save Spread")
                Text("Save Spread")
            }
            
            Button(onClick = { navController.navigate(Screen.Home.route) }) {
                Icon(Icons.Default.Cancel, contentDescription = "Cancel")
                Text("Cancel")
            }
        }
    }
}

data class SpreadPosition(
    val id: String,
    val spreadId: String,
    val positionIndex: Int,
    val name: String,
    val meaning: String,
    val x: Float,
    val y: Float
)