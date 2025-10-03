package com.tarotreader.di

import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import com.tarotreader.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AIModule {
    
    @Provides
    @Singleton
    fun provideGeminiModel(@ApplicationContext context: Context): GenerativeModel {
        // Get API key from BuildConfig or local.properties
        val apiKey = BuildConfig.GEMINI_API_KEY.ifEmpty {
            // Fallback: try to read from assets
            try {
                context.assets.open("gemini_api_key.txt").bufferedReader().use { it.readText().trim() }
            } catch (e: Exception) {
                "" // Return empty string if no API key found
            }
        }
        
        return GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey
        )
    }
}