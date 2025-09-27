package com.tarotreader.di

import android.content.Context
import androidx.room.Room
import com.tarotreader.data.database.DatabaseInitializer
import com.tarotreader.data.database.TarotDao
import com.tarotreader.data.database.TarotDatabase

object DatabaseModule {
    private var database: TarotDatabase? = null
    private var dao: TarotDao? = null
    private var initializer: DatabaseInitializer? = null
    
    fun provideDatabase(context: Context): TarotDatabase {
        if (database == null) {
            database = Room.databaseBuilder(
                context,
                TarotDatabase::class.java,
                TarotDatabase.DATABASE_NAME
            ).build()
        }
        return database!!
    }
    
    fun provideTarotDao(context: Context): TarotDao {
        if (dao == null) {
            dao = provideDatabase(context).tarotDao()
        }
        return dao!!
    }
    
    fun provideDatabaseInitializer(context: Context): DatabaseInitializer {
        if (initializer == null) {
            initializer = DatabaseInitializer(provideTarotDao(context))
        }
        return initializer!!
    }
}