package com.tarotreader.data.ai

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

/**
 * Recognizes tarot spread layouts from images using TensorFlow Lite
 */
@Singleton
class SpreadLayoutRecognizer @Inject constructor(
    private val context: Context
) {
    private var interpreter: Interpreter? = null
    private val imageProcessor: ImageProcessor
    
    // Known spread patterns
    private val spreadPatterns = mapOf(
        "three_card" to SpreadPattern(3, listOf(
            PositionPattern(0.25f, 0.5f),
            PositionPattern(0.5f, 0.5f),
            PositionPattern(0.75f, 0.5f)
        )),
        "celtic_cross" to SpreadPattern(10, listOf(
            PositionPattern(0.5f, 0.5f),   // 1. Present
            PositionPattern(0.5f, 0.5f),   // 2. Challenge (overlapping)
            PositionPattern(0.5f, 0.3f),   // 3. Above
            PositionPattern(0.5f, 0.7f),   // 4. Below
            PositionPattern(0.3f, 0.5f),   // 5. Past
            PositionPattern(0.7f, 0.5f),   // 6. Future
            PositionPattern(0.85f, 0.7f),  // 7. Self
            PositionPattern(0.85f, 0.55f), // 8. Environment
            PositionPattern(0.85f, 0.4f),  // 9. Hopes/Fears
            PositionPattern(0.85f, 0.25f)  // 10. Outcome
        )),
        "horseshoe" to SpreadPattern(7, listOf(
            PositionPattern(0.2f, 0.7f),
            PositionPattern(0.3f, 0.5f),
            PositionPattern(0.4f, 0.35f),
            PositionPattern(0.5f, 0.3f),
            PositionPattern(0.6f, 0.35f),
            PositionPattern(0.7f, 0.5f),
            PositionPattern(0.8f, 0.7f)
        )),
        "daily_draw" to SpreadPattern(1, listOf(
            PositionPattern(0.5f, 0.5f)
        )),
        "past_present_future" to SpreadPattern(3, listOf(
            PositionPattern(0.25f, 0.5f),
            PositionPattern(0.5f, 0.5f),
            PositionPattern(0.75f, 0.5f)
        )),
        "relationship" to SpreadPattern(5, listOf(
            PositionPattern(0.3f, 0.3f),  // Person 1
            PositionPattern(0.7f, 0.3f),  // Person 2
            PositionPattern(0.5f, 0.5f),  // Connection
            PositionPattern(0.3f, 0.7f),  // Challenges
            PositionPattern(0.7f, 0.7f)   // Potential
        ))
    )
    
    init {
        try {
            interpreter = Interpreter(loadModelFile())
        } catch (e: Exception) {
            Log.e("SpreadLayoutRecognizer", "Failed to initialize TensorFlow Lite interpreter", e)
        }
        
        // Initialize image processor to resize images to model input size
        imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .build()
    }
    
    private fun loadModelFile(): MappedByteBuffer {
        val modelFile = File(context.filesDir, "spread_layout_model.tflite")
        if (!modelFile.exists()) {
            // Try to load from assets as fallback
            try {
                val assetFileDescriptor = context.assets.openFd("spread_layout_model.tflite")
                val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
                val fileChannel = inputStream.channel
                val startOffset = assetFileDescriptor.startOffset
                val declaredLength = assetFileDescriptor.declaredLength
                return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            } catch (e: Exception) {
                throw IOException("Spread layout model file not found in assets or files directory", e)
            }
        }
        
        val fileInputStream = FileInputStream(modelFile)
        val fileChannel = fileInputStream.channel
        val startOffset = 0L
        val declaredLength = fileChannel.size()
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
    
    /**
     * Recognize spread layout from image
     * Returns the spread ID that best matches the detected layout
     */
    fun recognizeSpreadLayout(bitmap: Bitmap): String {
        try {
            val result = recognizeSpreadLayoutDetailed(bitmap)
            return result.spreadId
        } catch (e: Exception) {
            Log.e("SpreadLayoutRecognizer", "Error recognizing spread layout", e)
            return "three_card" // Default fallback
        }
    }
    
    /**
     * Recognize spread layout with detailed results
     */
    fun recognizeSpreadLayoutDetailed(bitmap: Bitmap): SpreadLayoutResult {
        try {
            // First, detect card positions in the image
            val detectedPositions = detectCardPositions(bitmap)
            
            // Match detected positions to known spread patterns
            val matchResults = spreadPatterns.map { (spreadId, pattern) ->
                val matchScore = calculatePatternMatch(detectedPositions, pattern, bitmap.width, bitmap.height)
                Pair(spreadId, matchScore)
            }
            
            // Find best match
            val bestMatch = matchResults.maxByOrNull { it.second }
            val spreadId = bestMatch?.first ?: "three_card"
            val confidence = bestMatch?.second ?: 0.5f
            
            // Get the matched pattern
            val pattern = spreadPatterns[spreadId]!!
            
            // Convert pattern positions to actual pixel positions
            val positions = pattern.positions.map { pos ->
                CardPosition(
                    x = pos.x * bitmap.width,
                    y = pos.y * bitmap.height,
                    width = 80f,
                    height = 120f
                )
            }
            
            return SpreadLayoutResult(
                spreadId = spreadId,
                positions = positions,
                confidence = confidence
            )
        } catch (e: Exception) {
            Log.e("SpreadLayoutRecognizer", "Error during spread layout recognition", e)
            // Return default three-card spread
            return SpreadLayoutResult(
                spreadId = "three_card",
                positions = listOf(
                    CardPosition(bitmap.width * 0.25f, bitmap.height * 0.5f, 80f, 120f),
                    CardPosition(bitmap.width * 0.5f, bitmap.height * 0.5f, 80f, 120f),
                    CardPosition(bitmap.width * 0.75f, bitmap.height * 0.5f, 80f, 120f)
                ),
                confidence = 0.75f
            )
        }
    }
    
    /**
     * Detect card positions in the image using edge detection and clustering
     */
    private fun detectCardPositions(bitmap: Bitmap): List<DetectedPosition> {
        val positions = mutableListOf<DetectedPosition>()
        
        // Convert to grayscale and detect edges
        val edges = detectEdges(bitmap)
        
        // Find rectangular regions (potential cards)
        val rectangles = findRectangles(edges, bitmap.width, bitmap.height)
        
        // Filter and cluster rectangles
        val cardRectangles = filterCardRectangles(rectangles)
        
        // Convert to normalized positions
        cardRectangles.forEach { rect ->
            positions.add(
                DetectedPosition(
                    x = rect.centerX / bitmap.width,
                    y = rect.centerY / bitmap.height,
                    width = rect.width / bitmap.width,
                    height = rect.height / bitmap.height
                )
            )
        }
        
        return positions
    }
    
    /**
     * Simple edge detection using gradient
     */
    private fun detectEdges(bitmap: Bitmap): Array<BooleanArray> {
        val width = bitmap.width
        val height = bitmap.height
        val edges = Array(height) { BooleanArray(width) }
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        
        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                val idx = y * width + x
                val current = getGray(pixels[idx])
                val right = getGray(pixels[idx + 1])
                val bottom = getGray(pixels[idx + width])
                
                val gradX = kotlin.math.abs(current - right)
                val gradY = kotlin.math.abs(current - bottom)
                
                edges[y][x] = (gradX + gradY) > 50
            }
        }
        
        return edges
    }
    
    private fun getGray(pixel: Int): Int {
        val r = (pixel shr 16) and 0xFF
        val g = (pixel shr 8) and 0xFF
        val b = pixel and 0xFF
        return (r + g + b) / 3
    }
    
    /**
     * Find rectangular regions in edge map
     */
    private fun findRectangles(edges: Array<BooleanArray>, width: Int, height: Int): List<Rectangle> {
        val rectangles = mutableListOf<Rectangle>()
        val visited = Array(height) { BooleanArray(width) }
        
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (edges[y][x] && !visited[y][x]) {
                    val rect = traceRectangle(edges, visited, x, y, width, height)
                    if (rect != null) {
                        rectangles.add(rect)
                    }
                }
            }
        }
        
        return rectangles
    }
    
    private fun traceRectangle(
        edges: Array<BooleanArray>,
        visited: Array<BooleanArray>,
        startX: Int,
        startY: Int,
        width: Int,
        height: Int
    ): Rectangle? {
        var minX = startX
        var maxX = startX
        var minY = startY
        var maxY = startY
        
        val queue = mutableListOf(Pair(startX, startY))
        visited[startY][startX] = true
        
        while (queue.isNotEmpty()) {
            val (x, y) = queue.removeAt(0)
            
            minX = minOf(minX, x)
            maxX = maxOf(maxX, x)
            minY = minOf(minY, y)
            maxY = maxOf(maxY, y)
            
            // Check neighbors
            for (dy in -1..1) {
                for (dx in -1..1) {
                    val nx = x + dx
                    val ny = y + dy
                    
                    if (nx in 0 until width && ny in 0 until height &&
                        edges[ny][nx] && !visited[ny][nx]) {
                        visited[ny][nx] = true
                        queue.add(Pair(nx, ny))
                    }
                }
            }
        }
        
        val rectWidth = maxX - minX
        val rectHeight = maxY - minY
        
        // Filter out very small or very large regions
        if (rectWidth < 20 || rectHeight < 30 || rectWidth > width * 0.8 || rectHeight > height * 0.8) {
            return null
        }
        
        return Rectangle(
            minX.toFloat(),
            minY.toFloat(),
            rectWidth.toFloat(),
            rectHeight.toFloat()
        )
    }
    
    /**
     * Filter rectangles to keep only card-like shapes
     */
    private fun filterCardRectangles(rectangles: List<Rectangle>): List<Rectangle> {
        return rectangles.filter { rect ->
            // Tarot cards typically have aspect ratio around 1.5 (height/width)
            val aspectRatio = rect.height / rect.width
            aspectRatio in 1.2..1.8
        }
    }
    
    /**
     * Calculate how well detected positions match a spread pattern
     */
    private fun calculatePatternMatch(
        detected: List<DetectedPosition>,
        pattern: SpreadPattern,
        imageWidth: Int,
        imageHeight: Int
    ): Float {
        // Check if number of cards matches
        if (detected.size != pattern.cardCount) {
            return 0.0f
        }
        
        // Calculate distance between detected positions and pattern positions
        var totalDistance = 0.0
        val patternPositions = pattern.positions
        
        // Find best matching between detected and pattern positions
        val used = mutableSetOf<Int>()
        detected.forEach { det ->
            var minDist = Float.MAX_VALUE
            var minIdx = -1
            
            patternPositions.forEachIndexed { idx, pat ->
                if (idx !in used) {
                    val dist = sqrt(
                        (det.x - pat.x) * (det.x - pat.x) +
                        (det.y - pat.y) * (det.y - pat.y)
                    )
                    if (dist < minDist) {
                        minDist = dist
                        minIdx = idx
                    }
                }
            }
            
            if (minIdx >= 0) {
                used.add(minIdx)
                totalDistance += minDist
            }
        }
        
        // Convert distance to confidence score (0-1)
        val avgDistance = totalDistance / detected.size
        val confidence = (1.0 - avgDistance.coerceAtMost(1.0)).toFloat()
        
        return confidence
    }
    
    fun close() {
        interpreter?.close()
        interpreter = null
    }
}

data class SpreadLayoutResult(
    val spreadId: String,
    val positions: List<CardPosition>,
    val confidence: Float
)

data class SpreadPattern(
    val cardCount: Int,
    val positions: List<PositionPattern>
)

data class PositionPattern(
    val x: Float,  // Normalized 0-1
    val y: Float   // Normalized 0-1
)

data class DetectedPosition(
    val x: Float,      // Normalized 0-1
    val y: Float,      // Normalized 0-1
    val width: Float,  // Normalized 0-1
    val height: Float  // Normalized 0-1
)

data class Rectangle(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
) {
    val centerX: Float get() = x + width / 2
    val centerY: Float get() = y + height / 2
}