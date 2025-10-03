package com.tarotreader.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

object AccessibilityHelper {
    
    /**
     * Check if the user has enabled high contrast mode
     */
    @Composable
    fun isHighContrastModeEnabled(): Boolean {
        val context = LocalContext.current
        val theme = context.resources.configuration.uiMode and 
                android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return theme == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }
    
    /**
     * Get appropriate colors for accessibility
     */
    @Composable
    fun getAccessibleColors(): AccessibleColors {
        return if (isHighContrastModeEnabled()) {
            AccessibleColors(
                primary = Color.Black,
                onPrimary = Color.White,
                secondary = Color.DarkGray,
                onSecondary = Color.White,
                background = Color.White,
                onBackground = Color.Black,
                surface = Color.White,
                onSurface = Color.Black
            )
        } else {
            AccessibleColors(
                primary = MaterialTheme.colorScheme.primary,
                onPrimary = MaterialTheme.colorScheme.onPrimary,
                secondary = MaterialTheme.colorScheme.secondary,
                onSecondary = MaterialTheme.colorScheme.onSecondary,
                background = MaterialTheme.colorScheme.background,
                onBackground = MaterialTheme.colorScheme.onBackground,
                surface = MaterialTheme.colorScheme.surface,
                onSurface = MaterialTheme.colorScheme.onSurface
            )
        }
    }
    
    /**
     * Check if the user has enabled large text mode
     */
    @Composable
    fun isLargeTextEnabled(): Boolean {
        val context = LocalContext.current
        val resources = context.resources
        val configuration = resources.configuration
        return configuration.fontScale > 1.0f
    }
    
    /**
     * Get appropriate text sizes for accessibility
     */
    @Composable
    fun getAccessibleTextSizes(): AccessibleTextSizes {
        val scale = if (isLargeTextEnabled()) {
            LocalContext.current.resources.configuration.fontScale
        } else {
            1.0f
        }
        
        return AccessibleTextSizes(
            small = 12f * scale,
            medium = 16f * scale,
            large = 20f * scale,
            extraLarge = 24f * scale
        )
    }
}

data class AccessibleColors(
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color
)

data class AccessibleTextSizes(
    val small: Float,
    val medium: Float,
    val large: Float,
    val extraLarge: Float
)