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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tarotreader.presentation.viewmodel.TarotViewModel
import androidx.compose.animation.*
import coil.compose.AsyncImage

@Composable
fun ReadingResultScreen(navController: NavController, viewModel: TarotViewModel) {
    val reading by viewModel.currentReading.collectAsState()
    var isSaved by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Tarot Reading",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (reading != null) {
            // Display the reading interpretation
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Interpretation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = reading!!.interpretation,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // Display the eigenvalue if available
            if (reading!!.eigenvalue != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Reading Eigenvalue",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LinearProgressIndicator(
                            progress = reading!!.eigenvalue.toFloat(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Your reading's eigenvalue is ${String.format("%.2f", reading!!.eigenvalue)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "This unique calculation provides deeper insights into the spiritual energy of your reading.",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
            
            // Display the cards drawn
            Text(
                text = "Cards in Your Spread",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(reading!!.cardDrawings) { cardDrawing ->
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
                            // Card image with Coil
                               Card(
                                   modifier = Modifier
                                       .size(80.dp)
                                       .padding(end = 16.dp)
                               ) {
                                   AsyncImage(
                                       model = cardDrawing.card.cardImageUrl,
                                       contentDescription = cardDrawing.card.name,
                                       modifier = Modifier.fillMaxSize(),
                                       contentScale = ContentScale.Crop
                                   )
                               }
                            
                            Column {
                                Text(
                                    text = cardDrawing.card.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Position: ${cardDrawing.positionId}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = if (cardDrawing.isReversed) "Reversed" else "Upright",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Placeholder when no reading is available
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No reading available. Please complete a reading first.")
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
                    // Save the reading to database via ViewModel
                    reading?.let { viewModel.saveReading(it) }
                    reading?.let { viewModel.saveReading(it) }
                    isSaved = true
                },
                enabled = reading != null && !isSaved
            ) {
                if (isSaved) {
                    Icon(Icons.Default.Check, contentDescription = "Reading Saved")
                    Text("Saved!")
                } else {
                    Icon(Icons.Default.Save, contentDescription = "Save Reading")
                    Text("Save Reading")
                }
            }
            
            Button(onClick = { navController.navigate(Screen.Home.route) }) {
                Icon(Icons.Default.Home, contentDescription = "Home")
                Text("Home")
            }
        }
    }
}