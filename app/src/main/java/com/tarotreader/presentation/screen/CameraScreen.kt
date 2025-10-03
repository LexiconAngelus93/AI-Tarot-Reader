package com.tarotreader.presentation.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.tarotreader.presentation.viewmodel.TarotViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    navController: NavController,
    viewModel: TarotViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    
    // Use Accompanist permissions for proper permission handling
    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )
    
    var isProcessing by remember { mutableStateOf(false) }
    var cameraError by remember { mutableStateOf<String?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "AI-Powered Spread Analysis",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Take a photo of your physical Tarot spread and get an AI-powered interpretation.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (cameraPermissionState.status.isGranted) {
            // Real CameraX preview implementation
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        
                        cameraProviderFuture.addListener({
                            try {
                                val provider = cameraProviderFuture.get()
                                cameraProvider = provider
                                
                                // Set up preview
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                
                                // Set up image capture
                                imageCapture = ImageCapture.Builder()
                                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                                    .build()
                                
                                // Select back camera
                                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                
                                // Unbind all use cases before rebinding
                                provider.unbindAll()
                                
                                // Bind use cases to camera
                                provider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageCapture
                                )
                            } catch (e: Exception) {
                                Log.e("CameraScreen", "Camera initialization failed", e)
                                cameraError = "Failed to initialize camera: ${e.message}"
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                        
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            // Permission request UI
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Permission Required",
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "Camera permission is required for this feature",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = if (cameraPermissionState.status.shouldShowRationale) {
                            "Camera permission is needed to analyze your physical Tarot spreads. " +
                            "Please grant the permission in your device settings."
                        } else {
                            "Please grant camera permission to analyze your physical Tarot spreads"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
        
        if (cameraError != null) {
            Text(
                text = cameraError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (cameraPermissionState.status.isGranted) {
                Button(
                    onClick = { 
                        scope.launch {
                            isProcessing = true
                            cameraError = null
                            
                            try {
                                val bitmap = captureImage(context, imageCapture)
                                if (bitmap != null) {
                                    // Process the captured image
                                    processImage(context, bitmap, viewModel) { success ->
                                        isProcessing = false
                                        if (success) {
                                            navController.navigate(Screen.ReadingResult.route)
                                        } else {
                                            cameraError = "Failed to process image. Please try again."
                                        }
                                    }
                                } else {
                                    isProcessing = false
                                    cameraError = "Failed to capture image. Please try again."
                                }
                            } catch (e: Exception) {
                                isProcessing = false
                                cameraError = "Error: ${e.message}"
                                Log.e("CameraScreen", "Capture failed", e)
                            }
                        }
                    },
                    enabled = !isProcessing && imageCapture != null
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Processing...")
                    } else {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "Capture")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Capture")
                    }
                }
            } else {
                Button(
                    onClick = { 
                        // Request camera permission using Accompanist
                        cameraPermissionState.launchPermissionRequest()
                    }
                ) {
                    Icon(Icons.Default.Key, contentDescription = "Grant Permission")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (cameraPermissionState.status.shouldShowRationale) {
                            "Open Settings"
                        } else {
                            "Grant Permission"
                        }
                    )
                }
            }
            
            Button(onClick = { 
                cameraProvider?.unbindAll()
                navController.navigate(Screen.Home.route) 
            }) {
                Icon(Icons.Default.Cancel, contentDescription = "Cancel")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cancel")
            }
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.unbindAll()
        }
    }
}

/**
 * Capture image from camera using CameraX ImageCapture
 */
suspend fun captureImage(context: Context, imageCapture: ImageCapture?): Bitmap? {
    return suspendCoroutine { continuation ->
        if (imageCapture == null) {
            continuation.resume(null)
            return@suspendCoroutine
        }
        
        val photoFile = File(
            context.cacheDir,
            "tarot_spread_${System.currentTimeMillis()}.jpg"
        )
        
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        
        imageCapture.takePicture(
            outputOptions,
            Executors.newSingleThreadExecutor(),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    try {
                        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                        
                        // Rotate bitmap if needed based on EXIF data
                        val rotatedBitmap = rotateBitmapIfNeeded(bitmap, photoFile.absolutePath)
                        
                        // Clean up temp file
                        photoFile.delete()
                        
                        continuation.resume(rotatedBitmap)
                    } catch (e: Exception) {
                        Log.e("CameraScreen", "Failed to load captured image", e)
                        continuation.resume(null)
                    }
                }
                
                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraScreen", "Image capture failed", exception)
                    continuation.resume(null)
                }
            }
        )
    }
}

/**
 * Rotate bitmap based on EXIF orientation data
 */
fun rotateBitmapIfNeeded(bitmap: Bitmap, imagePath: String): Bitmap {
    try {
        val exif = androidx.exifinterface.media.ExifInterface(imagePath)
        val orientation = exif.getAttributeInt(
            androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
            androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
        )
        
        val matrix = Matrix()
        when (orientation) {
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            else -> return bitmap
        }
        
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } catch (e: Exception) {
        Log.e("CameraScreen", "Failed to rotate bitmap", e)
        return bitmap
    }
}

/**
 * Process captured image using AI card detection and spread analysis
 */
fun processImage(
    context: Context, 
    bitmap: Bitmap, 
    viewModel: TarotViewModel,
    onResult: (Boolean) -> Unit
) {
    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
        try {
            Log.d("CameraScreen", "Starting image processing for card detection")
            
            // Save bitmap to temporary file for processing
            val tempFile = File(context.cacheDir, "temp_spread_${System.currentTimeMillis()}.jpg")
            FileOutputStream(tempFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            
            // Use ViewModel to analyze the spread from image
            // This will use TarotCardDetector and SpreadLayoutRecognizer
            viewModel.analyzeSpreadFromImage(bitmap, "rider_waite") // Default deck
            
            // Clean up temp file
            tempFile.delete()
            
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                onResult(true)
            }
        } catch (e: Exception) {
            Log.e("CameraScreen", "Image processing failed", e)
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                onResult(false)
            }
        }
    }
}