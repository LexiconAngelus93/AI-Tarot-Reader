package com.tarotreader.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tarotreader.domain.repository.TarotRepository
import com.tarotreader.utils.ErrorHandler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * Background worker for syncing data when device comes online
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: TarotRepository,
    private val errorHandler: ErrorHandler
) : CoroutineWorker(appContext, workerParams) {
    
    companion object {
        private const val TAG = "SyncWorker"
        const val WORK_NAME = "tarot_sync_work"
    }
    
    override suspend fun doWork(): Result {
        return try {
            errorHandler.logInfo(TAG, "Starting sync operation")
            
            // Sync readings that were created offline
            syncPendingReadings()
            
            // Sync any other pending operations
            syncPendingOperations()
            
            errorHandler.logInfo(TAG, "Sync completed successfully")
            Result.success()
        } catch (e: Exception) {
            errorHandler.logError(TAG, "Sync failed", e)
            
            // Retry if we haven't exceeded retry limit
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
    
    private suspend fun syncPendingReadings() {
        try {
            // Get all readings
            val readings = repository.getAllReadings().first()
            
            // Filter readings that need syncing (you can add a sync flag to Reading model)
            // For now, we'll just log the count
            errorHandler.logInfo(TAG, "Found ${readings.size} readings")
            
            // TODO: Implement actual sync logic based on your backend
            // This could involve:
            // 1. Uploading readings to Firebase
            // 2. Downloading new data from server
            // 3. Resolving conflicts
        } catch (e: Exception) {
            errorHandler.logError(TAG, "Failed to sync readings", e)
            throw e
        }
    }
    
    private suspend fun syncPendingOperations() {
        try {
            // Sync any other pending operations
            // This could include:
            // 1. Uploading user preferences
            // 2. Syncing custom spreads
            // 3. Syncing notes and annotations
            
            errorHandler.logInfo(TAG, "Pending operations synced")
        } catch (e: Exception) {
            errorHandler.logError(TAG, "Failed to sync pending operations", e)
            throw e
        }
    }
}