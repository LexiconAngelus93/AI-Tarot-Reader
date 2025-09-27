package com.tarotreader.ui.animation

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.graphicsLayer

// Animation for card shuffling
@Composable
fun rememberCardShuffleTransition(isShuffling: Boolean): State<Float> {
    val transition = rememberInfiniteTransition()
    
    return transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
}

// Animation for card drawing
@Composable
fun rememberCardDrawTransition(isDrawn: Boolean): Transition<Boolean> {
    val transition = updateTransition(isDrawn, label = "cardDraw")
    
    return transition
}

// Animation for card reveal
@Composable
fun rememberCardRevealTransition(isRevealed: Boolean): Transition<Boolean> {
    val transition = updateTransition(isRevealed, label = "cardReveal")
    
    return transition
}

// Animation for reading result appearance
@Composable
fun rememberReadingResultTransition(isVisible: Boolean): Transition<Boolean> {
    val transition = updateTransition(isVisible, label = "readingResult")
    
    return transition
}