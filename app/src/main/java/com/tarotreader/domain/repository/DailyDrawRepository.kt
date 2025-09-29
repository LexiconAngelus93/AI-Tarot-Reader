package com.tarotreader.domain.repository

import com.tarotreader.domain.model.DailyDraw
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface DailyDrawRepository {
    suspend fun getDailyDrawByDate(date: LocalDate): DailyDraw?
    fun getRecentDailyDraws(): Flow<List<DailyDraw>>
    suspend fun saveDailyDraw(dailyDraw: DailyDraw)
    suspend fun updateDailyDraw(dailyDraw: DailyDraw)
    suspend fun deleteDailyDraw(dailyDraw: DailyDraw)
}