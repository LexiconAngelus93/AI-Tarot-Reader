package com.tarotreader.ui.animation

import androidx.compose.animation.core.*
import androidx.compose.runtime.*

/**
 * Animation for card flipping
 */
@Composable
fun rememberCardFlipTransition(): Transition<Boolean> {
    val transition = updateTransition(false, label = "Card Flip Transition")
    
    return transition
}

/**
 * Animation for card drawing
 */
@Composable
fun rememberCardDrawAnimation(): InfiniteTransition {
    val infiniteTransition = rememberInfiniteTransition()
    
    return infiniteTransition
}

/**
 * Animation for reading reveal
 */
@Composable
fun rememberReadingRevealAnimation(): Transition<Boolean> {
    val transition = updateTransition(false, label = "Reading Reveal Transition")
    
    return transition
}

/**
 * Animation for deck selection
 */
@Composable
fun rememberDeckSelectionAnimation(): InfiniteTransition {
    val infiniteTransition = rememberInfiniteTransition()
    
    return infiniteTransition
}