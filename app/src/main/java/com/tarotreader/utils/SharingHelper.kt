package com.tarotreader.utils

import android.content.Context
import android.content.Intent
import androidx.core.app.ShareCompat
import com.tarotreader.domain.model.Reading

object SharingHelper {
    
    /**
     * Share a reading interpretation via Android's sharing mechanism
     */
    fun shareReading(context: Context, reading: Reading) {
        val shareText = buildReadingShareText(reading)
        
        ShareCompat.IntentBuilder(context)
            .setType("text/plain")
            .setText(shareText)
            .setChooserTitle("Share Tarot Reading")
            .startChooser()
    }
    
    /**
     * Share a daily draw via Android's sharing mechanism
     */
    fun shareDailyDraw(context: Context, card: com.tarotreader.domain.model.TarotCard, isReversed: Boolean, reflection: String?) {
        val shareText = buildDailyDrawShareText(card, isReversed, reflection)
        
        ShareCompat.IntentBuilder(context)
            .setType("text/plain")
            .setText(shareText)
            .setChooserTitle("Share Daily Draw")
            .startChooser()
    }
    
    /**
     * Build a formatted text string for sharing a reading
     */
    private fun buildReadingShareText(reading: Reading): String {
        return buildString {
            append("Tarot Reading\n\n")
            append("Deck: ${reading.deckId}\n")
            append("Spread: ${reading.spreadId}\n")
            append("Date: ${reading.date}\n\n")
            
            if (reading.eigenvalue != null) {
                append("Eigenvalue: ${String.format("%.2f", reading.eigenvalue)}\n\n")
            }
            
            append("Interpretation:\n")
            append(reading.interpretation)
            
            if (reading.notes != null) {
                append("\n\nNotes:\n")
                append(reading.notes)
            }
        }
    }
    
    /**
     * Build a formatted text string for sharing a daily draw
     */
    private fun buildDailyDrawShareText(card: com.tarotreader.domain.model.TarotCard, isReversed: Boolean, reflection: String?): String {
        return buildString {
            append("Daily Tarot Draw\n\n")
            append("Card: ${card.name}\n")
            append("Orientation: ${if (isReversed) "Reversed" else "Upright"}\n\n")
            
            append("Meaning: ")
            append(if (isReversed) card.reversedMeaning else card.uprightMeaning)
            append("\n\n")
            
            append("Description:\n")
            append(card.description)
            
            if (reflection != null) {
                append("\n\nMy Reflection:\n")
                append(reflection)
            }
        }
    }
}