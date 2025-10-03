package com.tarotreader.utils

import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retry policy for network operations and other retryable operations
 */
@Singleton
class RetryPolicy @Inject constructor(
    private val errorHandler: ErrorHandler
) {
    
    companion object {
        private const val TAG = "RetryPolicy"
        private const val DEFAULT_MAX_RETRIES = 3
        private const val DEFAULT_INITIAL_DELAY = 1000L // 1 second
        private const val DEFAULT_MAX_DELAY = 10000L // 10 seconds
        private const val DEFAULT_BACKOFF_FACTOR = 2.0
    }
    
    /**
     * Execute an operation with retry logic
     */
    suspend fun <T> executeWithRetry(
        maxRetries: Int = DEFAULT_MAX_RETRIES,
        initialDelay: Long = DEFAULT_INITIAL_DELAY,
        maxDelay: Long = DEFAULT_MAX_DELAY,
        backoffFactor: Double = DEFAULT_BACKOFF_FACTOR,
        shouldRetry: (Throwable) -> Boolean = { true },
        operation: suspend () -> T
    ): Result<T> {
        var currentDelay = initialDelay
        var lastException: Throwable? = null
        
        repeat(maxRetries) { attempt ->
            try {
                val result = operation()
                return Result.success(result)
            } catch (e: Exception) {
                lastException = e
                val errorResult = errorHandler.handleError(e)
                
                // Check if we should retry
                if (!errorResult.isRetryable || !shouldRetry(e)) {
                    errorHandler.logError(TAG, "Non-retryable error on attempt ${attempt + 1}", e)
                    return Result.failure(e)
                }
                
                // Don't delay on last attempt
                if (attempt < maxRetries - 1) {
                    errorHandler.logWarning(
                        TAG, 
                        "Attempt ${attempt + 1} failed, retrying in ${currentDelay}ms"
                    )
                    delay(currentDelay)
                    currentDelay = (currentDelay * backoffFactor).toLong().coerceAtMost(maxDelay)
                }
            }
        }
        
        // All retries exhausted
        errorHandler.logError(TAG, "All $maxRetries retry attempts exhausted", lastException)
        return Result.failure(lastException ?: Exception("Operation failed after $maxRetries retries"))
    }
    
    /**
     * Execute with exponential backoff
     */
    suspend fun <T> executeWithExponentialBackoff(
        operation: suspend () -> T
    ): Result<T> {
        return executeWithRetry(
            maxRetries = 3,
            initialDelay = 1000L,
            maxDelay = 10000L,
            backoffFactor = 2.0,
            operation = operation
        )
    }
    
    /**
     * Execute with linear backoff
     */
    suspend fun <T> executeWithLinearBackoff(
        operation: suspend () -> T
    ): Result<T> {
        return executeWithRetry(
            maxRetries = 3,
            initialDelay = 2000L,
            maxDelay = 6000L,
            backoffFactor = 1.0,
            operation = operation
        )
    }
    
    /**
     * Execute with immediate retry (no delay)
     */
    suspend fun <T> executeWithImmediateRetry(
        maxRetries: Int = 2,
        operation: suspend () -> T
    ): Result<T> {
        return executeWithRetry(
            maxRetries = maxRetries,
            initialDelay = 0L,
            maxDelay = 0L,
            backoffFactor = 1.0,
            operation = operation
        )
    }
}