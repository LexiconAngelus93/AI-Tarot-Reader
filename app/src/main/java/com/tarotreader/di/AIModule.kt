package com.tarotreader.di

import android.content.Context
import com.tarotreader.data.ai.GeminiAIService
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
    fun provideGeminiAIService(
        @ApplicationContext context: Context
    ): GeminiAIService {
        return GeminiAIService(context)
    }
}