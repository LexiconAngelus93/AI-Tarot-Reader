package com.tarotreader.data.ai

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

class SpreadLayoutRecognizer(private val context: android.content.Context) {
    private var interpreter: Interpreter? = null
    private val imageProcessor: ImageProcessor
    
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
            // In a real app, we would download or copy the model file here
            throw IOException("Spread layout model file not found")
        }
        
        val fileInputStream = FileInputStream(modelFile)
        val fileChannel = fileInputStream.channel
        val startOffset = 0L
        val declaredLength = fileChannel.size()
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
    
    fun recognizeSpreadLayout(bitmap: Bitmap): SpreadLayoutResult {
        try {
            // Preprocess the image
            val tensorImage = TensorImage.create(bitmap, imageProcessor)
            
            // Prepare input buffer
            val inputBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3).apply {
                order(ByteOrder.nativeOrder())
            }
            
            // Prepare output buffer for layout classification
            val layoutOutputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 10), Float::class.java)
            
            // Prepare output buffer for position detection
            val positionOutputBuffer = TensorBuffer.createFixedSize(intArrayOf(10, 4), Float::class.java)
            
            // Run inference for layout recognition
            interpreter?.runForMultipleInputsOutputs(
                arrayOf(tensorImage.buffer),
                mapOf(
                    0 to layoutOutputBuffer.buffer,
                    1 to positionOutputBuffer.buffer
                )
            )
            
            // Postprocess results
            return postprocessResults(layoutOutputBuffer.floatArray, positionOutputBuffer.floatArray)
        } catch (e: Exception) {
            Log.e("SpreadLayoutRecognizer", "Error during spread layout recognition", e)
            return SpreadLayoutResult(
                spreadId = "three_card",
                positions = listOf(
                    CardPosition(50f, 100f, 80f, 120f),
                    CardPosition(150f, 100f, 80f, 120f),
                    CardPosition(250f, 100f, 80f, 120f)
                ),
                confidence = 0.75f
            )
        }
    }
    
    private fun postprocessResults(layoutOutput: FloatArray, positionOutput: FloatArray): SpreadLayoutResult {
        // Find the layout with highest confidence
        val layoutIndex = layoutOutput.indices.maxByOrNull { layoutOutput[it] } ?: 0
        val layoutConfidence = layoutOutput[layoutIndex]
        
        // Map layout index to spread ID
        val spreadId = when (layoutIndex) {
            0 -> "three_card"
            1 -> "celtic_cross"
            2 -> "horseshoe"
            3 -> "daily_draw"
            else -> "three_card" // Default to three card spread
        }
        
        // Extract position coordinates
        val positions = mutableListOf<CardPosition>()
        for (i in 0 until positionOutput.size step 4) {
            positions.add(
                CardPosition(
                    x = positionOutput[i],
                    y = positionOutput[i + 1],
                    width = positionOutput[i + 2],
                    height = positionOutput[i + 3]
                )
            )
        }
        
        return SpreadLayoutResult(
            spreadId = spreadId,
            positions = positions,
            confidence = layoutConfidence
        )
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