package com.tarotreader.utils

import android.util.Log
import kotlinx.coroutines.TimeoutCancellationException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized error handling utility
 * Provides consistent error messages and logging across the app
 */
@Singleton
class ErrorHandler @Inject constructor() {
    
    companion object {
        private const val TAG = "ErrorHandler"
    }
    
    /**
     * Handle exceptions and return user-friendly error messages
     */
    fun handleError(throwable: Throwable): ErrorResult {
        Log.e(TAG, "Error occurred", throwable)
        
        return when (throwable) {
            is UnknownHostException -> ErrorResult(
                message = "No internet connection. Please check your network settings.",
                type = ErrorType.NETWORK,
                isRetryable = true
            )
            
            is SocketTimeoutException, is TimeoutCancellationException -> ErrorResult(
                message = "Request timed out. Please try again.",
                type = ErrorType.TIMEOUT,
                isRetryable = true
            )
            
            is IOException -> ErrorResult(
                message = "Network error occurred. Please check your connection.",
                type = ErrorType.NETWORK,
                isRetryable = true
            )
            
            is IllegalStateException -> ErrorResult(
                message = throwable.message ?: "Invalid state. Please try again.",
                type = ErrorType.VALIDATION,
                isRetryable = false
            )
            
            is IllegalArgumentException -> ErrorResult(
                message = throwable.message ?: "Invalid input. Please check your data.",
                type = ErrorType.VALIDATION,
                isRetryable = false
            )
            
            is SecurityException -> ErrorResult(
                message = "Permission denied. Please grant necessary permissions.",
                type = ErrorType.PERMISSION,
                isRetryable = false
            )
            
            else -> ErrorResult(
                message = throwable.message ?: "An unexpected error occurred. Please try again.",
                type = ErrorType.UNKNOWN,
                isRetryable = true
            )
        }
    }
    
    /**
     * Log error for debugging
     */
    fun logError(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }
    
    /**
     * Log warning
     */
    fun logWarning(tag: String, message: String) {
        Log.w(tag, message)
    }
    
    /**
     * Log info
     */
    fun logInfo(tag: String, message: String) {
        Log.i(tag, message)
    }
}

/**
 * Error result data class
 */
data class ErrorResult(
    val message: String,
    val type: ErrorType,
    val isRetryable: Boolean
)

/**
 * Error types for categorization
 */
enum class ErrorType {
    NETWORK,
    TIMEOUT,
    VALIDATION,
    PERMISSION,
    DATABASE,
    AI_SERVICE,
    UNKNOWN
}