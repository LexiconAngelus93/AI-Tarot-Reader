package com.tarotreader.utils

import android.content.Context
import android.content.Intent
import com.tarotreader.domain.model.Reading

object SharingHelper {
    fun shareReading(context: Context, reading: Reading) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "My Tarot Reading")
        }
        
        // Create a formatted string for the reading
        val readingText = buildString {
            append("Tarot Reading\n\n")
            append("Deck: ${reading.deck.name}\n")
            append("Spread: ${reading.spread.name}\n")
            append("Date: ${reading.date}\n\n")
            
            append("Interpretation:\n")
            append(reading.interpretation)
            
            if (reading.reflection.isNotEmpty()) {
                append("\n\nMy Reflection:\n")
                append(reading.reflection)
            }
        }
        
        shareIntent.putExtra(Intent.EXTRA_TEXT, readingText)
        
        val chooserIntent = Intent.createChooser(shareIntent, "Share your Tarot reading")
        context.startActivity(chooserIntent)
    }
    
    fun shareDailyDraw(context: Context, dailyDraw: com.tarotreader.domain.model.DailyDraw) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "My Daily Tarot Card")
        }
        
        // Create a formatted string for the daily draw
        val dailyDrawText = buildString {
            append("Daily Tarot Card\n\n")
            append("Date: ${dailyDraw.date}\n")
            append("Card: ${dailyDraw.card.name}\n")
            append("Orientation: ${dailyDraw.orientation}\n\n")
            
            append("Meaning: ${dailyDraw.card.meaning}\n\n")
            
            if (dailyDraw.reflection.isNotEmpty()) {
                append("My Reflection:\n")
                append(dailyDraw.reflection)
            }
        }
        
        shareIntent.putExtra(Intent.EXTRA_TEXT, dailyDrawText)
        
        val chooserIntent = Intent.createChooser(shareIntent, "Share your daily Tarot card")
        context.startActivity(chooserIntent)
    }
}