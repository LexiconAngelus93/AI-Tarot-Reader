package com.tarotreader.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tarotreader.data.model.*

@Database(
    entities = [
        TarotCardEntity::class,
        TarotDeckEntity::class,
        TarotSpreadEntity::class,
        SpreadPositionEntity::class,
        ReadingEntity::class,
        CardDrawingEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TarotDatabase : RoomDatabase() {
    abstract fun tarotDao(): TarotDao
    
    companion object {
        const val DATABASE_NAME = "tarot_database"
    }
}