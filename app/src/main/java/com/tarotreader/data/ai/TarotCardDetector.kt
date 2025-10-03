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
import kotlin.math.max

/**
 * TensorFlow Lite-based tarot card detector
 * Detects and identifies tarot cards in images
 */
@Singleton
class TarotCardDetector @Inject constructor(
    private val context: Context
) {
    private var interpreter: Interpreter? = null
    private val imageProcessor: ImageProcessor
    
    // Model configuration
    private val inputSize = 224
    private val maxDetections = 10
    private val confidenceThreshold = 0.5f
    
    // Card database for identification
    private val cardDatabase = mutableMapOf<Int, String>()
    
    init {
        try {
            interpreter = Interpreter(loadModelFile())
            initializeCardDatabase()
        } catch (e: Exception) {
            Log.e("TarotCardDetector", "Failed to initialize TensorFlow Lite interpreter", e)
        }
        
        // Initialize image processor to resize images to model input size
        imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(inputSize, inputSize, ResizeOp.ResizeMethod.BILINEAR))
            .build()
    }
    
    private fun loadModelFile(): MappedByteBuffer {
        val modelFile = File(context.filesDir, "tarot_card_model.tflite")
        if (!modelFile.exists()) {
            // Try to load from assets as fallback
            try {
                val assetFileDescriptor = context.assets.openFd("tarot_card_model.tflite")
                val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
                val fileChannel = inputStream.channel
                val startOffset = assetFileDescriptor.startOffset
                val declaredLength = assetFileDescriptor.declaredLength
                return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            } catch (e: Exception) {
                throw IOException("Model file not found in assets or files directory", e)
            }
        }
        
        val fileInputStream = FileInputStream(modelFile)
        val fileChannel = fileInputStream.channel
        val startOffset = 0L
        val declaredLength = fileChannel.size()
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
    
    private fun initializeCardDatabase() {
        // Initialize mapping of model output indices to card IDs
        // Major Arcana (0-21)
        for (i in 0..21) {
            cardDatabase[i] = "major_$i"
        }
        
        // Minor Arcana - Wands (22-35)
        val wands = listOf("ace", "two", "three", "four", "five", "six", "seven", "eight", 
                          "nine", "ten", "page", "knight", "queen", "king")
        wands.forEachIndexed { index, name ->
            cardDatabase[22 + index] = "wands_$name"
        }
        
        // Minor Arcana - Cups (36-49)
        val cups = listOf("ace", "two", "three", "four", "five", "six", "seven", "eight",
                         "nine", "ten", "page", "knight", "queen", "king")
        cups.forEachIndexed { index, name ->
            cardDatabase[36 + index] = "cups_$name"
        }
        
        // Minor Arcana - Swords (50-63)
        val swords = listOf("ace", "two", "three", "four", "five", "six", "seven", "eight",
                           "nine", "ten", "page", "knight", "queen", "king")
        swords.forEachIndexed { index, name ->
            cardDatabase[50 + index] = "swords_$name"
        }
        
        // Minor Arcana - Pentacles (64-77)
        val pentacles = listOf("ace", "two", "three", "four", "five", "six", "seven", "eight",
                              "nine", "ten", "page", "knight", "queen", "king")
        pentacles.forEachIndexed { index, name ->
            cardDatabase[64 + index] = "pentacles_$name"
        }
    }
    
    /**
     * Detect cards in an image and return their positions and identities
     */
    fun detectCards(bitmap: Bitmap, deckId: String): List<Pair<String, Boolean>> {
        try {
            val detectionResults = detectCardsInImage(bitmap)
            
            // Convert detection results to card IDs with deck prefix and orientation
            return detectionResults.map { result ->
                val baseCardId = cardDatabase[result.classId] ?: "unknown"
                val fullCardId = "${baseCardId}_$deckId"
                val isReversed = result.rotation > 90 && result.rotation < 270
                Pair(fullCardId, isReversed)
            }
        } catch (e: Exception) {
            Log.e("TarotCardDetector", "Error detecting cards", e)
            return emptyList()
        }
    }
    
    /**
     * Detect cards in image and return detailed detection results
     */
    fun detectCardsInImage(bitmap: Bitmap): List<CardDetectionResult> {
        try {
            // Preprocess the image
            val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
            
            // Prepare input buffer
            val inputBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3).apply {
                order(ByteOrder.nativeOrder())
            }
            
            // Copy image data to input buffer
            val imageData = tensorImage.tensorBuffer.floatArray
            inputBuffer.rewind()
            for (pixel in imageData) {
                inputBuffer.putFloat(pixel)
            }
            
            // Prepare output buffers
            // Output format: [batch, num_detections, 6] where 6 = [x, y, w, h, confidence, class]
            val outputLocations = Array(1) { Array(maxDetections) { FloatArray(4) } }
            val outputClasses = Array(1) { FloatArray(maxDetections) }
            val outputScores = Array(1) { FloatArray(maxDetections) }
            val numDetections = FloatArray(1)
            
            val outputs = mapOf(
                0 to outputLocations,
                1 to outputClasses,
                2 to outputScores,
                3 to numDetections
            )
            
            // Run inference
            interpreter?.runForMultipleInputsOutputs(arrayOf(inputBuffer), outputs)
            
            // Postprocess results
            return postprocessResults(
                outputLocations[0],
                outputClasses[0],
                outputScores[0],
                numDetections[0].toInt(),
                bitmap.width,
                bitmap.height
            )
        } catch (e: Exception) {
            Log.e("TarotCardDetector", "Error during card detection", e)
            return emptyList()
        }
    }
    
    /**
     * Parse and filter model output to get valid card detections
     */
    private fun postprocessResults(
        locations: Array<FloatArray>,
        classes: FloatArray,
        scores: FloatArray,
        numDetections: Int,
        imageWidth: Int,
        imageHeight: Int
    ): List<CardDetectionResult> {
        val results = mutableListOf<CardDetectionResult>()
        
        for (i in 0 until minOf(numDetections, maxDetections)) {
            val confidence = scores[i]
            
            // Filter by confidence threshold
            if (confidence < confidenceThreshold) continue
            
            val location = locations[i]
            val classId = classes[i].toInt()
            
            // Convert normalized coordinates to pixel coordinates
            val x = location[0] * imageWidth
            val y = location[1] * imageHeight
            val width = location[2] * imageWidth
            val height = location[3] * imageHeight
            
            // Estimate rotation based on aspect ratio
            val rotation = estimateCardRotation(width, height)
            
            results.add(
                CardDetectionResult(
                    cardId = cardDatabase[classId] ?: "unknown",
                    classId = classId,
                    position = CardPosition(x, y, width, height),
                    confidence = confidence,
                    rotation = rotation
                )
            )
        }
        
        // Sort by confidence (highest first)
        return results.sortedByDescending { it.confidence }
    }
    
    /**
     * Estimate card rotation based on dimensions
     * Returns rotation in degrees (0-360)
     */
    private fun estimateCardRotation(width: Float, height: Float): Float {
        // Tarot cards are typically taller than wide
        // If width > height, card is likely rotated
        return if (width > height) {
            // Horizontal orientation suggests 90 or 270 degree rotation
            90f
        } else {
            // Vertical orientation suggests 0 or 180 degree rotation
            // We'll default to 0 (upright) and let other logic determine if reversed
            0f
        }
    }
    
    /**
     * Identify the deck used in the image
     * Returns deck ID and confidence score
     */
    fun identifyDeck(bitmap: Bitmap): Pair<String, Float> {
        try {
            // Analyze visual characteristics to identify deck
            val deckFeatures = extractDeckFeatures(bitmap)
            
            // Compare with known deck signatures
            val deckScores = mutableMapOf<String, Float>()
            
            // Rider-Waite deck characteristics
            deckScores["rider_waite"] = calculateDeckSimilarity(deckFeatures, RIDER_WAITE_FEATURES)
            
            // Thoth deck characteristics
            deckScores["thoth"] = calculateDeckSimilarity(deckFeatures, THOTH_FEATURES)
            
            // Marseille deck characteristics
            deckScores["marseille"] = calculateDeckSimilarity(deckFeatures, MARSEILLE_FEATURES)
            
            // Return deck with highest score
            val bestMatch = deckScores.maxByOrNull { it.value }
            return Pair(bestMatch?.key ?: "rider_waite", bestMatch?.value ?: 0.5f)
        } catch (e: Exception) {
            Log.e("TarotCardDetector", "Error identifying deck", e)
            return Pair("rider_waite", 0.5f)
        }
    }
    
    /**
     * Extract visual features from image for deck identification
     */
    private fun extractDeckFeatures(bitmap: Bitmap): DeckFeatures {
        // Analyze color palette
        val colorHistogram = analyzeColorHistogram(bitmap)
        
        // Analyze edge patterns
        val edgeDensity = analyzeEdgeDensity(bitmap)
        
        // Analyze brightness and contrast
        val brightness = analyzeBrightness(bitmap)
        val contrast = analyzeContrast(bitmap)
        
        return DeckFeatures(
            colorHistogram = colorHistogram,
            edgeDensity = edgeDensity,
            brightness = brightness,
            contrast = contrast
        )
    }
    
    private fun analyzeColorHistogram(bitmap: Bitmap): FloatArray {
        val histogram = FloatArray(256) { 0f }
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        
        pixels.forEach { pixel ->
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            val gray = (r + g + b) / 3
            histogram[gray]++
        }
        
        // Normalize
        val total = pixels.size.toFloat()
        for (i in histogram.indices) {
            histogram[i] /= total
        }
        
        return histogram
    }
    
    private fun analyzeEdgeDensity(bitmap: Bitmap): Float {
        // Simple edge detection using gradient
        var edgeCount = 0
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        
        for (y in 1 until bitmap.height - 1) {
            for (x in 1 until bitmap.width - 1) {
                val idx = y * bitmap.width + x
                val current = pixels[idx]
                val right = pixels[idx + 1]
                val bottom = pixels[idx + bitmap.width]
                
                val gradX = kotlin.math.abs(getGray(current) - getGray(right))
                val gradY = kotlin.math.abs(getGray(current) - getGray(bottom))
                
                if (gradX + gradY > 50) {
                    edgeCount++
                }
            }
        }
        
        return edgeCount.toFloat() / pixels.size
    }
    
    private fun analyzeBrightness(bitmap: Bitmap): Float {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        
        var totalBrightness = 0f
        pixels.forEach { pixel ->
            totalBrightness += getGray(pixel)
        }
        
        return totalBrightness / pixels.size / 255f
    }
    
    private fun analyzeContrast(bitmap: Bitmap): Float {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        
        val grayValues = pixels.map { getGray(it).toFloat() }
        val mean = grayValues.average().toFloat()
        
        var variance = 0f
        grayValues.forEach { gray ->
            variance += (gray - mean) * (gray - mean)
        }
        variance /= grayValues.size
        
        return kotlin.math.sqrt(variance) / 255f
    }
    
    private fun getGray(pixel: Int): Int {
        val r = (pixel shr 16) and 0xFF
        val g = (pixel shr 8) and 0xFF
        val b = pixel and 0xFF
        return (r + g + b) / 3
    }
    
    private fun calculateDeckSimilarity(features: DeckFeatures, reference: DeckFeatures): Float {
        // Calculate similarity score based on multiple features
        var similarity = 0f
        
        // Color histogram similarity (using histogram intersection)
        var histogramSimilarity = 0f
        for (i in features.colorHistogram.indices) {
            histogramSimilarity += minOf(features.colorHistogram[i], reference.colorHistogram[i])
        }
        similarity += histogramSimilarity * 0.4f
        
        // Edge density similarity
        val edgeSimilarity = 1f - kotlin.math.abs(features.edgeDensity - reference.edgeDensity)
        similarity += edgeSimilarity * 0.2f
        
        // Brightness similarity
        val brightnessSimilarity = 1f - kotlin.math.abs(features.brightness - reference.brightness)
        similarity += brightnessSimilarity * 0.2f
        
        // Contrast similarity
        val contrastSimilarity = 1f - kotlin.math.abs(features.contrast - reference.contrast)
        similarity += contrastSimilarity * 0.2f
        
        return similarity
    }
    
    fun close() {
        interpreter?.close()
        interpreter = null
    }
    
    companion object {
        // Reference features for known decks
        private val RIDER_WAITE_FEATURES = DeckFeatures(
            colorHistogram = FloatArray(256) { if (it in 100..200) 0.01f else 0.001f },
            edgeDensity = 0.15f,
            brightness = 0.6f,
            contrast = 0.4f
        )
        
        private val THOTH_FEATURES = DeckFeatures(
            colorHistogram = FloatArray(256) { if (it in 50..150) 0.01f else 0.001f },
            edgeDensity = 0.25f,
            brightness = 0.5f,
            contrast = 0.6f
        )
        
        private val MARSEILLE_FEATURES = DeckFeatures(
            colorHistogram = FloatArray(256) { if (it in 120..220) 0.01f else 0.001f },
            edgeDensity = 0.12f,
            brightness = 0.65f,
            contrast = 0.35f
        )
    }
}

data class CardDetectionResult(
    val cardId: String,
    val classId: Int,
    val position: CardPosition,
    val confidence: Float,
    val rotation: Float = 0f
)

data class CardPosition(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)

data class DeckFeatures(
    val colorHistogram: FloatArray,
    val edgeDensity: Float,
    val brightness: Float,
    val contrast: Float
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeckFeatures

        if (!colorHistogram.contentEquals(other.colorHistogram)) return false
        if (edgeDensity != other.edgeDensity) return false
        if (brightness != other.brightness) return false
        if (contrast != other.contrast) return false

        return true
    }

    override fun hashCode(): Int {
        var result = colorHistogram.contentHashCode()
        result = 31 * result + edgeDensity.hashCode()
        result = 31 * result + brightness.hashCode()
        result = 31 * result + contrast.hashCode()
        return result
    }
}