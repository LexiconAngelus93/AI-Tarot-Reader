package com.tarotreader.presentation.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tarotreader.domain.model.CardOrientation
import com.tarotreader.domain.model.DailyDraw
import com.tarotreader.presentation.viewmodel.TarotViewModel
import com.tarotreader.utils.SharingHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DailyDrawScreen(
    onBack: () -> Unit,
    viewModel: TarotViewModel = hiltViewModel()
) {
    val dailyDraw by viewModel.currentDailyDraw.collectAsState()
    val decks by viewModel.decks.collectAsState()
    
    var reflectionText by remember { mutableStateOf(dailyDraw?.reflection ?: "") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar with back button
        TopAppBar(
            title = { Text("Daily Card Draw") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { 
                    if (decks.isNotEmpty()) {
                        viewModel.generateDailyDraw(decks.first().id)
                    }
                }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "New Draw")
                }
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Date display
        Text(
            text = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Card display
        if (dailyDraw != null) {
            TarotCardDisplay(dailyDraw!!)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Reflection section
            Text(
                text = "Your Reflection",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextField(
                value = reflectionText,
                onValueChange = { reflectionText = it },
                label = { Text("Write your thoughts about today's card") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { 
                    viewModel.saveDailyDrawReflection(dailyDraw!!, reflectionText)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Reflection")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Recent draws section
            Text(
                text = "Recent Draws",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )
            
            val recentDraws by viewModel.recentDailyDraws.collectAsState()
            
            if (recentDraws.isNotEmpty()) {
                LazyColumn {
                    items(recentDraws) { draw ->
                        RecentDrawItem(draw)
                    }
                }
            } else {
                Text("No recent draws available")
            }
        } else {
            // Generate initial draw if none exists
            LaunchedEffect(decks) {
                if (decks.isNotEmpty()) {
                    viewModel.generateDailyDraw(decks.first().id)
                }
            }
            
            Text("Generating your daily card draw...")
        }
    }
}

@Composable
fun TarotCardDisplay(dailyDraw: DailyDraw) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dailyDraw.card.name,
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (dailyDraw.orientation == CardOrientation.UPRIGHT) 
                    "Upright" else "Reversed",
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = dailyDraw.card.meaning,
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
fun RecentDrawItem(dailyDraw: DailyDraw) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dailyDraw.date.format(DateTimeFormatter.ofPattern("MMM dd")),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = dailyDraw.card.name,
                    style = MaterialTheme.typography.body1
                )
            }
            
            Text(
                text = if (dailyDraw.orientation == CardOrientation.UPRIGHT) 
                    "U" else "R",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.primary
            )
        }
    }
}
        if (dailyDraw != null) {
            IconButton(onClick = { shareDailyDraw(context, dailyDraw!!) }) {
                Icon(Icons.Filled.Share, contentDescription = &quot;Share&quot;)
            }
        }

private fun shareDailyDraw(context: Context, dailyDraw: DailyDraw) {
    SharingHelper.shareDailyDraw(context, dailyDraw)
}

