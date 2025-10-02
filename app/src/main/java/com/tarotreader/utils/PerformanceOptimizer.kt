package com.tarotreader.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Performance optimization utilities for the Tarot Reader app
 * Handles image preloading, caching, and memory management
 */
@Singleton
class PerformanceOptimizer @Inject constructor(
    private val context: Context
) {
    // Image cache using LRU (Least Recently Used) strategy
    private val imageCache: LruCache<String, Bitmap>
    
    // Weak references to bitmaps for memory management
    private val bitmapReferences = mutableListOf<WeakReference<Bitmap>>()
    
    // Database query cache
    private val queryCache = LruCache<String, Any>(50)
    
    // Coil image loader for efficient image loading
    private val imageLoader: ImageLoader by lazy {
        ImageLoader.Builder(context)
            .crossfade(true)
            .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
            .diskCachePolicy(coil.request.CachePolicy.ENABLED)
            .build()
    }
    
    init {
        // Calculate cache size based on available memory
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8 // Use 1/8th of available memory
        
        imageCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // Size in KB
                return bitmap.byteCount / 1024
            }
            
            override fun entryRemoved(
                evicted: Boolean,
                key: String,
                oldValue: Bitmap,
                newValue: Bitmap?
            ) {
                // Clean up when bitmap is removed from cache
                if (evicted && !oldValue.isRecycled) {
                    bitmapReferences.add(WeakReference(oldValue))
                }
            }
        }
    }
    
    /**
     * Preload images using Coil for efficient loading and caching
     */
    suspend fun preloadImages(imageUrls: List<String>, context: Context) {
        withContext(Dispatchers.IO) {
            imageUrls.forEach { url ->
                try {
                    // Create image request
                    val request = ImageRequest.Builder(context)
                        .data(url)
                        .memoryCacheKey(url)
                        .diskCacheKey(url)
                        .build()
                    
                    // Execute request to preload image
                    val result = imageLoader.execute(request)
                    
                    // Cache the bitmap if successful
                    if (result is SuccessResult) {
                        val bitmap = (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                        bitmap?.let {
                            imageCache.put(url, it)
                            android.util.Log.d("PerformanceOptimizer", "Preloaded image: $url")
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("PerformanceOptimizer", "Failed to preload image: $url", e)
                }
            }
        }
    }
    
    /**
     * Get cached image or null if not in cache
     */
    fun getCachedImage(url: String): Bitmap? {
        return imageCache.get(url)
    }
    
    /**
     * Manually cache a bitmap
     */
    fun cacheImage(url: String, bitmap: Bitmap) {
        imageCache.put(url, bitmap)
    }
    
    /**
     * Clear image cache
     */
    fun clearImageCache() {
        imageCache.evictAll()
        android.util.Log.d("PerformanceOptimizer", "Image cache cleared")
    }
    
    /**
     * Optimize database queries by caching frequently accessed data
     */
    fun <T> cacheQuery(key: String, data: T) {
        queryCache.put(key, data as Any)
        android.util.Log.d("PerformanceOptimizer", "Cached query: $key")
    }
    
    /**
     * Get cached query result
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getCachedQuery(key: String): T? {
        return queryCache.get(key) as? T
    }
    
    /**
     * Clear query cache
     */
    fun clearQueryCache() {
        queryCache.evictAll()
        android.util.Log.d("PerformanceOptimizer", "Query cache cleared")
    }
    
    /**
     * Reduce memory usage by recycling bitmaps that are no longer needed
     */
    fun recycleBitmaps() {
        var recycledCount = 0
        
        // Iterate through weak references and recycle bitmaps
        val iterator = bitmapReferences.iterator()
        while (iterator.hasNext()) {
            val ref = iterator.next()
            val bitmap = ref.get()
            
            if (bitmap == null) {
                // Bitmap was already garbage collected
                iterator.remove()
            } else if (!bitmap.isRecycled) {
                // Recycle the bitmap
                bitmap.recycle()
                recycledCount++
                iterator.remove()
            }
        }
        
        // Suggest garbage collection
        if (recycledCount > 0) {
            System.gc()
            android.util.Log.d("PerformanceOptimizer", "Recycled $recycledCount bitmaps")
        }
    }
    
    /**
     * Optimize AI model loading by preloading models into memory
     */
    suspend fun optimizeAIModelLoading(modelPaths: List<String>) {
        withContext(Dispatchers.IO) {
            modelPaths.forEach { modelPath ->
                try {
                    // Check if model file exists
                    val modelFile = java.io.File(context.filesDir, modelPath)
                    if (modelFile.exists()) {
                        // Read model file to warm up file system cache
                        modelFile.readBytes()
                        android.util.Log.d("PerformanceOptimizer", "Preloaded AI model: $modelPath")
                    } else {
                        // Try loading from assets
                        context.assets.open(modelPath).use { inputStream ->
                            inputStream.readBytes()
                            android.util.Log.d("PerformanceOptimizer", "Preloaded AI model from assets: $modelPath")
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("PerformanceOptimizer", "Failed to preload model: $modelPath", e)
                }
            }
        }
    }
    
    /**
     * Get memory usage statistics
     */
    fun getMemoryStats(): MemoryStats {
        val runtime = Runtime.getRuntime()
        val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
        val maxMemory = runtime.maxMemory() / 1024 / 1024
        val availableMemory = maxMemory - usedMemory
        
        val imageCacheSize = imageCache.size()
        val queryCacheSize = queryCache.size()
        
        return MemoryStats(
            usedMemoryMB = usedMemory,
            maxMemoryMB = maxMemory,
            availableMemoryMB = availableMemory,
            imageCacheSize = imageCacheSize,
            queryCacheSize = queryCacheSize,
            bitmapReferencesCount = bitmapReferences.size
        )
    }
    
    /**
     * Perform full cleanup of all caches and resources
     */
    fun performFullCleanup() {
        clearImageCache()
        clearQueryCache()
        recycleBitmaps()
        System.gc()
        android.util.Log.d("PerformanceOptimizer", "Full cleanup performed")
    }
    
    /**
     * Check if memory is running low and perform cleanup if needed
     */
    fun checkAndOptimizeMemory(): Boolean {
        val stats = getMemoryStats()
        val memoryUsagePercent = (stats.usedMemoryMB.toFloat() / stats.maxMemoryMB) * 100
        
        return if (memoryUsagePercent > 80) {
            android.util.Log.w("PerformanceOptimizer", "Memory usage high: $memoryUsagePercent%. Performing cleanup...")
            
            // Clear half of the image cache
            imageCache.trimToSize(imageCache.size() / 2)
            
            // Clear query cache
            clearQueryCache()
            
            // Recycle bitmaps
            recycleBitmaps()
            
            true
        } else {
            false
        }
    }
}

data class MemoryStats(
    val usedMemoryMB: Long,
    val maxMemoryMB: Long,
    val availableMemoryMB: Long,
    val imageCacheSize: Int,
    val queryCacheSize: Int,
    val bitmapReferencesCount: Int
)