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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReadingHistoryScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var dateFilter by remember { mutableStateOf<DateFilter>(DateFilter.AllTime) }
    var expandedFilter by remember { mutableStateOf(false) }
    
    // Placeholder readings data
    val readings = listOf(
        ReadingEntry(
            id = "1",
            date = Date(),
            deckName = "Rider-Waite Deck",
            spreadName = "Celtic Cross",
            eigenvalue = 0.75,
            cardCount = 10
        ),
        ReadingEntry(
            id = "2",
            date = Date(System.currentTimeMillis() - 86400000), // Yesterday
            deckName = "Morgan-Greer Deck",
            spreadName = "Three Card Spread",
            eigenvalue = 0.42,
            cardCount = 3
        ),
        ReadingEntry(
            id = "3",
            date = Date(System.currentTimeMillis() - 172800000), // Two days ago
            deckName = "Rider-Waite Deck",
            spreadName = "Daily Draw",
            eigenvalue = 0.88,
            cardCount = 1
        ),
        ReadingEntry(
            id = "4",
            date = Date(System.currentTimeMillis() - 259200000), // Three days ago
            deckName = "Thoth Deck",
            spreadName = "Horseshoe Spread",
            eigenvalue = 0.65,
            cardCount = 7
        )
    )
    
    val filteredReadings = readings.filter { reading ->
        (searchQuery.isEmpty() || 
         reading.deckName.contains(searchQuery, ignoreCase = true) || 
         reading.spreadName.contains(searchQuery, ignoreCase = true)) &&
        (dateFilter == DateFilter.AllTime || isWithinDateFilter(reading.date, dateFilter))
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Reading History",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search readings") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        
        // Date filter dropdown
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Filter by Date",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Button(onClick = { expandedFilter = true }) {
                Text(dateFilter.name)
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand Date Filter")
            }
            
            DropdownMenu(
                expanded = expandedFilter,
                onDismissRequest = { expandedFilter = false }
            ) {
                DateFilter.values().forEach { filter ->
                    DropdownMenuItem(
                        text = { Text(filter.name) },
                        onClick = { 
                            dateFilter = filter
                            expandedFilter = false
                        }
                    )
                }
            }
        }
        
        // Reading history list
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(filteredReadings) { reading ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = reading.deckName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(reading.date),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        
                        Text(
                            text = reading.spreadName,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${reading.cardCount} cards",
                                style = MaterialTheme.typography.bodySmall
                            )
                            
                            if (reading.eigenvalue != null) {
                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    LinearProgressIndicator(
                                        progress = reading.eigenvalue.toFloat(),
                                        modifier = Modifier
                                            .width(100.dp)
                                            .padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = "Eigenvalue: ${String.format("%.2f", reading.eigenvalue)}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        
                        // Note-taking section
                        var expanded by remember { mutableStateOf(false) }
                        var noteText by remember { mutableStateOf("") }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Notes",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(
                                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (expanded) "Collapse Notes" else "Expand Notes"
                                )
                            }
                        }
                        
                        if (expanded) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = noteText,
                                    onValueChange = { noteText = it },
                                    label = { Text("Add your reflections here") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    maxLines = 3
                                )
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(
                                        onClick = { 
                                            // In a real implementation, we would share the reading
                                        },
                                        enabled = noteText.isNotEmpty()
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Share Reading")
                                        Text("Share")
                                    }
                                    
                                    Button(
                                        onClick = { 
                                            // In a real implementation, we would save the note
                                            expanded = false
                                        },
                                        enabled = noteText.isNotEmpty()
                                    ) {
                                        Icon(Icons.Default.Save, contentDescription = "Save Note")
                                        Text("Save Note")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        Button(
            onClick = { navController.navigate(Screen.Home.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Back to Home")
        }
    }
}

data class ReadingEntry(
    val id: String,
    val date: Date,
    val deckName: String,
    val spreadName: String,
    val eigenvalue: Double?,
    val cardCount: Int
)

enum class DateFilter {
    AllTime,
    LastWeek,
    LastMonth,
    LastYear
}

fun isWithinDateFilter(date: Date, filter: DateFilter): Boolean {
    val now = Date().time
    val filterTime = when (filter) {
        DateFilter.LastWeek -> now - (7 * 24 * 60 * 60 * 1000)
        DateFilter.LastMonth -> now - (30 * 24 * 60 * 60 * 1000)
        DateFilter.LastYear -> now - (365 * 24 * 60 * 60 * 1000)
        else -> 0L
    }
    
    return date.time >= filterTime
}