package com.tarotreader.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tarotreader.presentation.screen.*
import com.tarotreader.presentation.viewmodel.TarotViewModel
import com.tarotreader.di.UseCaseModule
import com.tarotreader.di.RepositoryModule
import com.tarotreader.data.database.TarotDatabase
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.tarotreader.domain.usecase.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    // Initialize use cases
    val getAllDecksUseCase = UseCaseModule.provideGetAllDecksUseCase(
        RepositoryModule.provideLocalRepository(
            TarotDatabase.getInstance(context).tarotDao()
        )
    )
    
    val getDeckByIdUseCase = UseCaseModule.provideGetDeckByIdUseCase(
        RepositoryModule.provideLocalRepository(
            TarotDatabase.getInstance(context).tarotDao()
        )
    )
    
    val getAllSpreadsUseCase = UseCaseModule.provideGetAllSpreadsUseCase(
        RepositoryModule.provideLocalRepository(
            TarotDatabase.getInstance(context).tarotDao()
        )
    )
    
    val getSpreadByIdUseCase = UseCaseModule.provideGetSpreadByIdUseCase(
        RepositoryModule.provideLocalRepository(
            TarotDatabase.getInstance(context).tarotDao()
        )
    )
    
    val getAllCardsUseCase = UseCaseModule.provideGetAllCardsUseCase(
        RepositoryModule.provideLocalRepository(
            TarotDatabase.getInstance(context).tarotDao()
        )
    )
    
    val getCardsByDeckUseCase = UseCaseModule.provideGetCardsByDeckUseCase(
        RepositoryModule.provideLocalRepository(
            TarotDatabase.getInstance(context).tarotDao()
        )
    )
    
    val saveReadingUseCase = UseCaseModule.provideSaveReadingUseCase(
        RepositoryModule.provideLocalRepository(
            TarotDatabase.getInstance(context).tarotDao()
        )
    )
    
    val analyzeSpreadFromImageUseCase = UseCaseModule.provideAnalyzeSpreadFromImageUseCase(
        RepositoryModule.provideLocalRepository(
            TarotDatabase.getInstance(context).tarotDao()
        )
    )
    
    val generateReadingUseCase = UseCaseModule.provideGenerateReadingUseCase(
        RepositoryModule.provideLocalRepository(
            TarotDatabase.getInstance(context).tarotDao()
        )
    )
    
    val calculateEigenvalueUseCase = UseCaseModule.provideCalculateEigenvalueUseCase()
    
    // Initialize ViewModel
    val viewModel = remember {
        TarotViewModel(
            getAllDecksUseCase,
            getDeckByIdUseCase,
            getAllSpreadsUseCase,
            getSpreadByIdUseCase,
            getAllCardsUseCase,
            getCardsByDeckUseCase,
            saveReadingUseCase,
            analyzeSpreadFromImageUseCase,
            generateReadingUseCase,
            calculateEigenvalueUseCase
        )
    }
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        
        composable(Screen.DeckSelection.route) {
            DeckSelectionScreen(navController)
        }
        
        composable(Screen.SpreadSelection.route) {
            SpreadSelectionScreen(navController)
        }
        
        composable(Screen.CardSelection.route) {
            CardSelectionScreen(navController, viewModel)
        }
        
        composable(Screen.ReadingResult.route) {
            ReadingResultScreen(navController, viewModel)
        }
        
        composable(Screen.ReadingHistory.route) {
            ReadingHistoryScreen(navController)
        }
        
        composable(Screen.TarotDictionary.route) {
            TarotDictionaryScreen(navController)
        }
        
        composable("${Screen.CardDetail.route}/{cardId}") { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId") ?: ""
            CardDetailScreen(navController, cardId)
        }
        
        composable(Screen.LearningModule.route) {
            LearningModuleScreen(navController)
        }
        
        composable(Screen.SpreadCreator.route) {
            SpreadCreatorScreen(navController)
        }
        
        composable(Screen.Camera.route) {
            CameraScreen(navController)
        }
    }
}