package com.tarotreader.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object DeckSelection : Screen("deck_selection")
    object SpreadSelection : Screen("spread_selection")
    object CardSelection : Screen("card_selection")
    object ReadingResult : Screen("reading_result")
    object ReadingHistory : Screen("reading_history")
    object TarotDictionary : Screen("tarot_dictionary")
    object CardDetail : Screen("card_detail")
    object LearningModule : Screen("learning_module")
    object SpreadCreator : Screen("spread_creator")
    object Camera : Screen("camera")
}