# AI-Powered Tarot Card Recognition System

## Overview

The AI-powered image recognition system will allow users to take photos of physical Tarot card spreads and receive digital interpretations. This document outlines the technical implementation of this feature.

## System Architecture

### High-Level Components

1. **Image Capture & Processing Module**
   - Camera interface for capturing images
   - Image preprocessing and enhancement
   - Perspective correction for angled shots

2. **Card Detection & Recognition Module**
   - Card boundary detection
   - Card orientation detection
   - Card content recognition

3. **Spread Layout Recognition Module**
   - Spatial relationship analysis
   - Spread pattern matching
   - Position mapping

4. **Interpretation Generation Module**
   - Card meaning extraction
   - Position-specific interpretation
   - Holistic reading synthesis

### Technical Stack

- **TensorFlow Lite**: Core ML framework for on-device inference
- **CameraX**: Camera API for image capture
- **ML Kit**: Google's ML toolkit for mobile developers
- **OpenCV**: Computer vision library for image processing
- **Custom TensorFlow Models**: For Tarot-specific recognition tasks

## Implementation Details

### 1. Image Capture & Processing

#### Camera Interface
```kotlin
class TarotCameraFragment : Fragment() {
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture: ImageCapture
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize CameraX
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
        
        // Set up capture button
        captureButton.setOnClickListener {
            captureImage()
        }
    }
    
    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
        // Camera selector
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
            
        // Preview use case
        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(viewFinder.surfaceProvider)
        
        // ImageCapture use case
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
            
        // Bind use cases to camera
        cameraProvider.bindToLifecycle(
            this as LifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
    }
    
    private fun captureImage() {
        // Create output file
        val photoFile = createFile(requireContext(), "jpg")
        
        // Create output options object
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        
        // Capture the image
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Process the captured image
                    processImage(photoFile.absolutePath)
                }
                
                override fun onError(exception: ImageCaptureException) {
                    // Handle error
                }
            }
        )
    }
    
    private fun processImage(imagePath: String) {
        viewModel.processImage(imagePath)
    }
}
```

#### Image Preprocessing
```kotlin
class ImagePreprocessor {
    fun preprocessImage(imagePath: String): Bitmap {
        // Load image
        val originalBitmap = BitmapFactory.decodeFile(imagePath)
        
        // Resize image to standard dimensions for ML model
        val resizedBitmap = Bitmap.createScaledBitmap(
            originalBitmap,
            INPUT_IMAGE_WIDTH,
            INPUT_IMAGE_HEIGHT,
            true
        )
        
        // Enhance image contrast
        val enhancedBitmap = enhanceContrast(resizedBitmap)
        
        // Correct perspective if needed
        val correctedBitmap = correctPerspective(enhancedBitmap)
        
        return correctedBitmap
    }
    
    private fun enhanceContrast(bitmap: Bitmap): Bitmap {
        // Convert to Mat for OpenCV processing
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)
        
        // Apply CLAHE (Contrast Limited Adaptive Histogram Equalization)
        val lab = Mat()
        Imgproc.cvtColor(mat, lab, Imgproc.COLOR_BGR2Lab)
        
        val planes = ArrayList<Mat>()
        Core.split(lab, planes)
        
        val clahe = Imgproc.createCLAHE(2.0, Size(8.0, 8.0))
        val enhancedL = Mat()
        clahe.apply(planes[0], enhancedL)
        
        planes[0] = enhancedL
        val enhancedLab = Mat()
        Core.merge(planes, enhancedLab)
        
        val result = Mat()
        Imgproc.cvtColor(enhancedLab, result, Imgproc.COLOR_Lab2BGR)
        
        // Convert back to Bitmap
        val resultBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        Utils.matToBitmap(result, resultBitmap)
        
        return resultBitmap
    }
    
    private fun correctPerspective(bitmap: Bitmap): Bitmap {
        // Convert to Mat for OpenCV processing
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)
        
        // Convert to grayscale
        val gray = Mat()
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY)
        
        // Apply Gaussian blur
        val blurred = Mat()
        Imgproc.GaussianBlur(gray, blurred, Size(5.0, 5.0), 0.0)
        
        // Apply Canny edge detection
        val edges = Mat()
        Imgproc.Canny(blurred, edges, 75.0, 200.0)
        
        // Find contours
        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
        
        // Find the largest contour (assuming it's the table/surface)
        var maxContourArea = 0.0
        var maxContourIdx = -1
        
        for (i in contours.indices) {
            val area = Imgproc.contourArea(contours[i])
            if (area > maxContourArea) {
                maxContourArea = area
                maxContourIdx = i
            }
        }
        
        if (maxContourIdx >= 0) {
            // Approximate the contour to get a quadrilateral
            val contour = contours[maxContourIdx]
            val peri = Imgproc.arcLength(MatOfPoint2f(*contour.toArray()), true)
            val approx = MatOfPoint2f()
            Imgproc.approxPolyDP(MatOfPoint2f(*contour.toArray()), approx, 0.02 * peri, true)
            
            // If we have a quadrilateral, perform perspective transform
            if (approx.total() == 4L) {
                val points = approx.toArray()
                
                // Sort the points to get top-left, top-right, bottom-right, bottom-left
                val sortedPoints = sortPoints(points)
                
                // Create source and destination points for perspective transform
                val src = MatOfPoint2f(*sortedPoints)
                val dst = MatOfPoint2f(
                    Point(0.0, 0.0),
                    Point(bitmap.width.toDouble(), 0.0),
                    Point(bitmap.width.toDouble(), bitmap.height.toDouble()),
                    Point(0.0, bitmap.height.toDouble())
                )
                
                // Get perspective transform matrix and apply it
                val transformMatrix = Imgproc.getPerspectiveTransform(src, dst)
                val warped = Mat()
                Imgproc.warpPerspective(mat, warped, transformMatrix, Size(bitmap.width.toDouble(), bitmap.height.toDouble()))
                
                // Convert back to Bitmap
                val resultBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
                Utils.matToBitmap(warped, resultBitmap)
                
                return resultBitmap
            }
        }
        
        // If perspective correction fails, return the original bitmap
        return bitmap
    }
    
    private fun sortPoints(points: Array<Point>): Array<Point> {
        // Sort points to get [top-left, top-right, bottom-right, bottom-left]
        val result = arrayOfNulls<Point>(4)
        
        // Calculate sum and difference of coordinates
        val sumCoords = DoubleArray(points.size)
        val diffCoords = DoubleArray(points.size)
        
        for (i in points.indices) {
            sumCoords[i] = points[i].x + points[i].y
            diffCoords[i] = points[i].x - points[i].y
        }
        
        // Top-left has smallest sum
        result[0] = points[sumCoords.indexOf(sumCoords.min())]
        // Bottom-right has largest sum
        result[2] = points[sumCoords.indexOf(sumCoords.max())]
        // Top-right has largest difference
        result[1] = points[diffCoords.indexOf(diffCoords.max())]
        // Bottom-left has smallest difference
        result[3] = points[diffCoords.indexOf(diffCoords.min())]
        
        return result.requireNoNulls()
    }
    
    companion object {
        private const val INPUT_IMAGE_WIDTH = 1024
        private const val INPUT_IMAGE_HEIGHT = 1024
    }
}
```

### 2. Card Detection & Recognition

#### Card Detection
```kotlin
class CardDetector {
    private val objectDetector: ObjectDetector
    
    init {
        // Set up TensorFlow Lite object detector
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(MAX_RESULTS)
            .setScoreThreshold(SCORE_THRESHOLD)
            .build()
        objectDetector = ObjectDetector.createFromFileAndOptions(
            context,
            "tarot_card_detector.tflite",
            options
        )
    }
    
    fun detectCards(bitmap: Bitmap): List<DetectedCard> {
        // Create TensorImage from bitmap
        val image = TensorImage.fromBitmap(bitmap)
        
        // Run inference
        val results = objectDetector.detect(image)
        
        // Process results
        return results.map { detection ->
            val boundingBox = detection.boundingBox
            
            // Crop the card from the original image
            val cardBitmap = Bitmap.createBitmap(
                bitmap,
                boundingBox.left.toInt(),
                boundingBox.top.toInt(),
                boundingBox.width().toInt(),
                boundingBox.height().toInt()
            )
            
            DetectedCard(
                cardBitmap = cardBitmap,
                boundingBox = boundingBox,
                confidence = detection.categories[0].score,
                centerX = boundingBox.centerX(),
                centerY = boundingBox.centerY()
            )
        }
    }
    
    companion object {
        private const val MAX_RESULTS = 30
        private const val SCORE_THRESHOLD = 0.5f
    }
}

data class DetectedCard(
    val cardBitmap: Bitmap,
    val boundingBox: RectF,
    val confidence: Float,
    val centerX: Float,
    val centerY: Float,
    var identifiedCard: TarotCard? = null,
    var isReversed: Boolean = false
)
```

#### Card Recognition
```kotlin
class CardRecognizer {
    private val cardClassifier: ImageClassifier
    
    init {
        // Set up TensorFlow Lite image classifier
        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setMaxResults(TOP_K_RESULTS)
            .setScoreThreshold(SCORE_THRESHOLD)
            .build()
        cardClassifier = ImageClassifier.createFromFileAndOptions(
            context,
            "tarot_card_classifier.tflite",
            options
        )
    }
    
    fun recognizeCard(detectedCard: DetectedCard): DetectedCard {
        // Create TensorImage from bitmap
        val image = TensorImage.fromBitmap(detectedCard.cardBitmap)
        
        // Run inference
        val results = cardClassifier.classify(image)
        
        if (results.isNotEmpty() && results[0].categories.isNotEmpty()) {
            val topResult = results[0].categories[0]
            
            // Parse card ID from label
            val cardId = parseCardId(topResult.label)
            
            // Get card details from database
            val card = cardRepository.getCardById(cardId)
            
            // Determine if card is reversed
            val isReversed = detectCardOrientation(detectedCard.cardBitmap)
            
            // Update detected card with identification
            detectedCard.identifiedCard = card
            detectedCard.isReversed = isReversed
        }
        
        return detectedCard
    }
    
    private fun parseCardId(label: String): Long {
        // Example label format: "deck_name/card_name (card_id)"
        val regex = Regex(".*\\((\\d+)\\)$")
        val matchResult = regex.find(label)
        return matchResult?.groupValues?.get(1)?.toLongOrNull() ?: -1L
    }
    
    private fun detectCardOrientation(cardBitmap: Bitmap): Boolean {
        // Convert to Mat for OpenCV processing
        val mat = Mat()
        Utils.bitmapToMat(cardBitmap, mat)
        
        // Convert to HSV color space
        val hsv = Mat()
        Imgproc.cvtColor(mat, hsv, Imgproc.COLOR_BGR2HSV)
        
        // Split into channels
        val channels = ArrayList<Mat>()
        Core.split(hsv, channels)
        
        // Use the saturation channel for feature detection
        val saturation = channels[1]
        
        // Detect features in top and bottom halves
        val topHalf = saturation.submat(0, saturation.rows() / 2, 0, saturation.cols())
        val bottomHalf = saturation.submat(saturation.rows() / 2, saturation.rows(), 0, saturation.cols())
        
        val featureDetector = ORB.create()
        
        val topKeypoints = MatOfKeyPoint()
        featureDetector.detect(topHalf, topKeypoints)
        
        val bottomKeypoints = MatOfKeyPoint()
        featureDetector.detect(bottomHalf, bottomKeypoints)
        
        // Compare feature counts to determine orientation
        // More features in bottom half typically indicates reversed card
        return bottomKeypoints.toArray().size > topKeypoints.toArray().size * 1.2
    }
    
    companion object {
        private const val TOP_K_RESULTS = 3
        private const val SCORE_THRESHOLD = 0.3f
    }
}
```

### 3. Spread Layout Recognition

```kotlin
class SpreadRecognizer {
    private val spreadRepository: SpreadRepository
    
    fun recognizeSpread(detectedCards: List<DetectedCard>): RecognizedSpread {
        // Get all available spreads
        val allSpreads = spreadRepository.getAllSpreads()
        
        // Find best matching spread based on card count and positions
        val bestMatch = findBestMatchingSpread(detectedCards, allSpreads)
        
        // Map cards to positions
        val cardPositionMap = mapCardsToPositions(detectedCards, bestMatch)
        
        return RecognizedSpread(
            spread = bestMatch,
            cardPositionMap = cardPositionMap
        )
    }
    
    private fun findBestMatchingSpread(detectedCards: List<DetectedCard>, spreads: List<Spread>): Spread {
        // Filter spreads by card count
        val cardCount = detectedCards.size
        val candidateSpreads = spreads.filter { it.positions.size == cardCount }
        
        if (candidateSpreads.isEmpty()) {
            // If no exact match, find closest match
            return spreads.minByOrNull { abs(it.positions.size - cardCount) }
                ?: spreads.first() // Fallback to first spread if no close match
        }
        
        // If only one candidate, return it
        if (candidateSpreads.size == 1) {
            return candidateSpreads.first()
        }
        
        // For multiple candidates, analyze spatial arrangement
        var bestSpread = candidateSpreads.first()
        var bestScore = Double.MAX_VALUE
        
        for (spread in candidateSpreads) {
            val score = calculateSpatialMatchScore(detectedCards, spread)
            if (score < bestScore) {
                bestScore = score
                bestSpread = spread
            }
        }
        
        return bestSpread
    }
    
    private fun calculateSpatialMatchScore(detectedCards: List<DetectedCard>, spread: Spread): Double {
        // Normalize card positions
        val normalizedCardPositions = normalizePositions(detectedCards.map { Point(it.centerX.toDouble(), it.centerY.toDouble()) })
        
        // Get normalized spread positions
        val spreadPositions = spread.positions.map { Point(it.xPosition, it.yPosition) }
        val normalizedSpreadPositions = normalizePositions(spreadPositions)
        
        // Calculate minimum distance sum using Hungarian algorithm
        return calculateMinimumDistanceSum(normalizedCardPositions, normalizedSpreadPositions)
    }
    
    private fun normalizePositions(points: List<Point>): List<Point> {
        // Find bounding box
        var minX = Double.MAX_VALUE
        var minY = Double.MAX_VALUE
        var maxX = Double.MIN_VALUE
        var maxY = Double.MIN_VALUE
        
        for (point in points) {
            minX = min(minX, point.x)
            minY = min(minY, point.y)
            maxX = max(maxX, point.x)
            maxY = max(maxY, point.y)
        }
        
        val width = maxX - minX
        val height = maxY - minY
        
        // Normalize to [0,1] range
        return points.map { 
            Point(
                (it.x - minX) / width,
                (it.y - minY) / height
            )
        }
    }
    
    private fun calculateMinimumDistanceSum(points1: List<Point>, points2: List<Point>): Double {
        // Create cost matrix for Hungarian algorithm
        val n = points1.size
        val costMatrix = Array(n) { DoubleArray(n) }
        
        for (i in 0 until n) {
            for (j in 0 until n) {
                costMatrix[i][j] = calculateDistance(points1[i], points2[j])
            }
        }
        
        // Apply Hungarian algorithm
        val hungarian = HungarianAlgorithm(costMatrix)
        val assignment = hungarian.execute()
        
        // Calculate total distance
        var totalDistance = 0.0
        for (i in 0 until n) {
            totalDistance += costMatrix[i][assignment[i]]
        }
        
        return totalDistance
    }
    
    private fun calculateDistance(p1: Point, p2: Point): Double {
        val dx = p1.x - p2.x
        val dy = p1.y - p2.y
        return sqrt(dx * dx + dy * dy)
    }
    
    private fun mapCardsToPositions(detectedCards: List<DetectedCard>, spread: Spread): Map<SpreadPosition, DetectedCard> {
        // Normalize card positions
        val cardPositions = detectedCards.map { Point(it.centerX.toDouble(), it.centerY.toDouble()) }
        val normalizedCardPositions = normalizePositions(cardPositions)
        
        // Get normalized spread positions
        val spreadPositions = spread.positions.map { Point(it.xPosition, it.yPosition) }
        val normalizedSpreadPositions = normalizePositions(spreadPositions)
        
        // Create cost matrix for Hungarian algorithm
        val n = detectedCards.size
        val costMatrix = Array(n) { DoubleArray(n) }
        
        for (i in 0 until n) {
            for (j in 0 until n) {
                costMatrix[i][j] = calculateDistance(normalizedCardPositions[i], normalizedSpreadPositions[j])
            }
        }
        
        // Apply Hungarian algorithm
        val hungarian = HungarianAlgorithm(costMatrix)
        val assignment = hungarian.execute()
        
        // Create mapping
        val result = mutableMapOf<SpreadPosition, DetectedCard>()
        for (i in 0 until n) {
            result[spread.positions[assignment[i]]] = detectedCards[i]
        }
        
        return result
    }
}

data class RecognizedSpread(
    val spread: Spread,
    val cardPositionMap: Map<SpreadPosition, DetectedCard>
)
```

### 4. Interpretation Generation

```kotlin
class InterpretationGenerator {
    fun generateInterpretation(recognizedSpread: RecognizedSpread): ReadingInterpretation {
        // Generate individual card interpretations
        val cardInterpretations = generateCardInterpretations(recognizedSpread)
        
        // Generate holistic reading
        val holisticReading = generateHolisticReading(recognizedSpread, cardInterpretations)
        
        // Calculate eigenvalue
        val eigenvalue = calculateEigenvalue(recognizedSpread)
        
        return ReadingInterpretation(
            cardInterpretations = cardInterpretations,
            holisticReading = holisticReading,
            eigenvalue = eigenvalue
        )
    }
    
    private fun generateCardInterpretations(recognizedSpread: RecognizedSpread): List<CardInterpretation> {
        val result = mutableListOf<CardInterpretation>()
        
        for ((position, detectedCard) in recognizedSpread.cardPositionMap) {
            val card = detectedCard.identifiedCard ?: continue
            val isReversed = detectedCard.isReversed
            
            // Get meaning based on orientation
            val meaning = if (isReversed) card.reversedMeaning else card.uprightMeaning
            
            // Generate position-specific interpretation
            val positionInterpretation = generatePositionSpecificInterpretation(card, position, isReversed)
            
            result.add(
                CardInterpretation(
                    card = card,
                    position = position,
                    isReversed = isReversed,
                    generalMeaning = meaning,
                    positionSpecificMeaning = positionInterpretation
                )
            )
        }
        
        return result
    }
    
    private fun generatePositionSpecificInterpretation(card: TarotCard, position: SpreadPosition, isReversed: Boolean): String {
        // Get card keywords
        val keywords = if (isReversed) {
            card.keywords.filter { it.isReversed }.map { it.keyword }
        } else {
            card.keywords.filter { !it.isReversed }.map { it.keyword }
        }
        
        // Get position meaning
        val positionMeaning = position.description
        
        // Use NLP to generate position-specific interpretation
        return interpretationNlpModel.generatePositionInterpretation(
            cardName = card.name,
            cardMeaning = if (isReversed) card.reversedMeaning else card.uprightMeaning,
            keywords = keywords,
            positionName = position.name,
            positionMeaning = positionMeaning,
            isReversed = isReversed
        )
    }
    
    private fun generateHolisticReading(
        recognizedSpread: RecognizedSpread,
        cardInterpretations: List<CardInterpretation>
    ): String {
        // Extract key elements from individual interpretations
        val elements = cardInterpretations.map { interpretation ->
            HolisticElement(
                cardName = interpretation.card.name,
                positionName = interpretation.position.name,
                isReversed = interpretation.isReversed,
                keywords = interpretation.card.keywords.filter { it.isReversed == interpretation.isReversed }.map { it.keyword },
                positionMeaning = interpretation.position.description
            )
        }
        
        // Generate holistic reading using NLP model
        return interpretationNlpModel.generateHolisticReading(
            spreadName = recognizedSpread.spread.name,
            spreadDescription = recognizedSpread.spread.description,
            elements = elements
        )
    }
    
    private fun calculateEigenvalue(recognizedSpread: RecognizedSpread): EigenvalueResult {
        var sum = 0
        
        // Sum all card numbers
        for ((_, detectedCard) in recognizedSpread.cardPositionMap) {
            val card = detectedCard.identifiedCard ?: continue
            sum += card.number
        }
        
        // Reduce to a number between 1 and 22 (Major Arcana range)
        var reducedValue = sum
        while (reducedValue > 22) {
            reducedValue = reduceNumber(reducedValue)
        }
        
        // Find corresponding Major Arcana card
        val eigenvalueCard = cardRepository.getMajorArcanaCardByNumber(reducedValue)
        
        return EigenvalueResult(
            value = reducedValue,
            card = eigenvalueCard
        )
    }
    
    private fun reduceNumber(number: Int): Int {
        // Sum the digits
        var sum = 0
        var n = number
        
        while (n > 0) {
            sum += n % 10
            n /= 10
        }
        
        return sum
    }
}

data class CardInterpretation(
    val card: TarotCard,
    val position: SpreadPosition,
    val isReversed: Boolean,
    val generalMeaning: String,
    val positionSpecificMeaning: String
)

data class HolisticElement(
    val cardName: String,
    val positionName: String,
    val isReversed: Boolean,
    val keywords: List<String>,
    val positionMeaning: String
)

data class EigenvalueResult(
    val value: Int,
    val card: TarotCard
)

data class ReadingInterpretation(
    val cardInterpretations: List<CardInterpretation>,
    val holisticReading: String,
    val eigenvalue: EigenvalueResult
)
```

## Machine Learning Models

### 1. Card Detection Model

- **Model Type**: Object Detection (SSD MobileNet or YOLOv5)
- **Input**: 1024x1024 RGB image
- **Output**: Bounding boxes for Tarot cards in the image
- **Training Data**: 
  - 5,000+ images of Tarot cards in various arrangements
  - Multiple lighting conditions
  - Different backgrounds
  - Various card orientations
- **Augmentation**:
  - Rotation
  - Scaling
  - Color shifts
  - Brightness/contrast variations
  - Partial occlusion

### 2. Card Recognition Model

- **Model Type**: Image Classification (EfficientNet or MobileNetV3)
- **Input**: 224x224 RGB image of a single card
- **Output**: Card identity (78 classes per deck)
- **Training Data**:
  - 100+ images per card
  - Multiple decks
  - Various lighting conditions
  - Different orientations
- **Augmentation**:
  - Rotation
  - Brightness/contrast variations
  - Noise addition
  - Perspective warping

### 3. Interpretation NLP Model

- **Model Type**: Sequence-to-Sequence Text Generation
- **Input**: Card and position information
- **Output**: Contextual interpretation text
- **Training Data**:
  - Professional Tarot readings
  - Card meanings from multiple sources
  - Position-specific interpretations
- **Fine-tuning**:
  - Fine-tuned on Tarot-specific language
  - Optimized for coherent narrative generation

## Performance Optimization

### On-Device Processing

- **Model Quantization**: 8-bit integer quantization for all TensorFlow Lite models
- **Hardware Acceleration**: NNAPI on supported devices
- **Batch Processing**: Process multiple cards in parallel where possible
- **Caching**: Cache recognition results for recently seen cards

### Fallback Mechanisms

1. **Card Detection Fallback**:
   - If automatic detection fails, allow manual card selection
   - Provide UI for manual boundary adjustment

2. **Card Recognition Fallback**:
   - If recognition confidence is low, show top 3 candidates for user selection
   - Allow manual card selection from deck

3. **Spread Recognition Fallback**:
   - If spread matching fails, suggest closest matches
   - Allow manual spread selection

## User Experience Considerations

1. **Real-time Feedback**:
   - Show detection boxes as they're identified
   - Provide confidence indicators

2. **Guided Capture**:
   - Overlay guides for optimal card placement
   - Real-time feedback on image quality

3. **Progressive Enhancement**:
   - Start with basic functionality on all devices
   - Enable advanced features on capable hardware

4. **Error Handling**:
   - Clear error messages for recognition failures
   - Helpful suggestions for improving capture quality

## Future Enhancements

1. **Multi-deck Support**:
   - Expand recognition to 50+ popular Tarot decks
   - Allow custom deck training

2. **Advanced Spread Analysis**:
   - Detect and interpret card clusters and patterns
   - Recognize non-standard spread layouts

3. **Temporal Analysis**:
   - Compare readings over time
   - Track recurring cards and themes

4. **AR Overlay**:
   - Real-time AR overlay of card information
   - Interactive 3D visualization of spread meanings