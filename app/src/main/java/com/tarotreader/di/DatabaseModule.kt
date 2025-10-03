package com.tarotreader.di

import android.content.Context
import androidx.room.Room
import com.tarotreader.data.database.TarotDao
import com.tarotreader.data.database.TarotDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTarotDatabase(
        @ApplicationContext context: Context
    ): TarotDatabase {
        return Room.databaseBuilder(
            context,
            TarotDatabase::class.java,
            "tarot_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideTarotDao(database: TarotDatabase): TarotDao {
        return database.tarotDao()
    }
}