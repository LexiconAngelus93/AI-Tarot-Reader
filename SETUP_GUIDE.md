# AI Tarot Reader - Setup Guide

This guide will help you set up and build the AI Tarot Reader Android application with Gemini AI integration.

## Prerequisites

- **Android Studio**: Latest version (Hedgehog or newer recommended)
- **JDK**: Java Development Kit 17 or higher
- **Android SDK**: API Level 36 (Android 14)
- **Gemini API Key**: Get from [Google AI Studio](https://makersuite.google.com/app/apikey)
- **Firebase Project**: (Optional) For cloud features

## Step 1: Clone the Repository

```bash
git clone https://github.com/LexiconAngelus93/AI-Tarot-Reader.git
cd AI-Tarot-Reader
```

## Step 2: Configure Gemini AI API Key

You have two options to configure your Gemini API key:

### Option A: Using local.properties (Recommended)

1. Copy the template file:
   ```bash
   cp local.properties.template local.properties
   ```

2. Edit `local.properties` and add your API key:
   ```properties
   sdk.dir=/path/to/your/Android/Sdk
   GEMINI_API_KEY=your_actual_gemini_api_key_here
   ```

### Option B: Using assets file

1. Create the assets directory if it doesn't exist:
   ```bash
   mkdir -p app/src/main/assets
   ```

2. Create the API key file:
   ```bash
   echo "your_actual_gemini_api_key_here" > app/src/main/assets/gemini_api_key.txt
   ```

## Step 3: Configure Firebase (Optional)

If you want to use Firebase features:

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or use an existing one
3. Add an Android app with package name: `com.tarotreader`
4. Download the `google-services.json` file
5. Place it in the `app/` directory:
   ```bash
   cp /path/to/downloaded/google-services.json app/google-services.json
   ```

If you don't want to use Firebase, you can use the template:
```bash
cp app/google-services.json.template app/google-services.json
```

## Step 4: Sync and Build

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Build the project:
   - Menu: Build → Make Project
   - Or press: `Ctrl+F9` (Windows/Linux) or `Cmd+F9` (Mac)

## Step 5: Run the Application

1. Connect an Android device or start an emulator
2. Click the "Run" button or press `Shift+F10`
3. Select your device and click OK

## Project Structure

```
AI-Tarot-Reader/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/tarotreader/
│   │   │   │   ├── data/           # Data layer
│   │   │   │   │   ├── ai/         # AI services (Gemini, TensorFlow)
│   │   │   │   │   ├── database/   # Room database
│   │   │   │   │   └── repository/ # Data repositories
│   │   │   │   ├── domain/         # Domain layer
│   │   │   │   │   ├── model/      # Domain models
│   │   │   │   │   ├── repository/ # Repository interfaces
│   │   │   │   │   └── usecase/    # Business logic
│   │   │   │   ├── presentation/   # Presentation layer
│   │   │   │   │   ├── screen/     # Compose UI screens
│   │   │   │   │   ├── viewmodel/  # ViewModels
│   │   │   │   │   └── navigation/ # Navigation
│   │   │   │   ├── di/             # Dependency injection
│   │   │   │   └── ui/             # UI theme
│   │   │   ├── res/                # Resources
│   │   │   └── AndroidManifest.xml
│   ├── build.gradle                # App-level build config
│   └── proguard-rules.pro          # ProGuard rules
├── build.gradle                    # Project-level build config
├── settings.gradle                 # Project settings
└── gradle/                         # Gradle wrapper
```

## Key Features Implemented

### 1. Gemini AI Integration
- **GeminiAIService**: Handles all AI interactions
- **AI-Powered Readings**: Generates comprehensive tarot interpretations
- **Fallback Mechanism**: Works without API key using traditional meanings

### 2. Architecture
- **Clean Architecture**: Separation of concerns with data, domain, and presentation layers
- **MVVM Pattern**: ViewModels manage UI state
- **Hilt Dependency Injection**: Automatic dependency management

### 3. Database
- **Room Database**: Local storage for cards, decks, spreads, and readings
- **Flow-based**: Reactive data updates
- **Type Converters**: Handles complex data types

### 4. AI Image Recognition
- **TensorFlow Lite**: On-device card detection
- **Spread Recognition**: Identifies spread layouts
- **Card Mapping**: Maps detected cards to positions

## Configuration Files

### build.gradle (Project)
- Kotlin version: 1.9.22
- Gradle plugin: 8.2.2
- Compose version: 1.6.0

### build.gradle (App)
- compileSdk: 36
- targetSdk: 36
- minSdk: 24

### Key Dependencies
- Jetpack Compose: UI toolkit
- Room: Database
- Hilt: Dependency injection
- Firebase: Cloud services
- TensorFlow Lite: AI image recognition
- Gemini AI: Natural language generation
- CameraX: Camera functionality

## Troubleshooting

### Build Errors

**Error: "SDK location not found"**
- Solution: Make sure `local.properties` has the correct `sdk.dir` path

**Error: "google-services.json is missing"**
- Solution: Either add a real Firebase config or use the template file

**Error: "GEMINI_API_KEY not found"**
- Solution: Add your API key to `local.properties` or create `gemini_api_key.txt`

### Runtime Errors

**Error: "AI service not configured"**
- Solution: The app will work with fallback interpretations. Add API key for AI features.

**Error: "Database initialization failed"**
- Solution: Clear app data and restart, or reinstall the app

### Gradle Sync Issues

**Error: "Failed to resolve dependencies"**
- Solution: Check your internet connection and try "File → Invalidate Caches / Restart"

**Error: "Unsupported Gradle version"**
- Solution: Update Android Studio to the latest version

## Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

## Building Release APK

1. Generate a signing key:
   ```bash
   keytool -genkey -v -keystore tarot-reader.keystore -alias tarot-reader -keyalg RSA -keysize 2048 -validity 10000
   ```

2. Add signing config to `app/build.gradle`:
   ```gradle
   android {
       signingConfigs {
           release {
               storeFile file("tarot-reader.keystore")
               storePassword "your_password"
               keyAlias "tarot-reader"
               keyPassword "your_password"
           }
       }
       buildTypes {
           release {
               signingConfig signingConfigs.release
               minifyEnabled true
               proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
           }
       }
   }
   ```

3. Build the release APK:
   ```bash
   ./gradlew assembleRelease
   ```

4. Find the APK at: `app/build/outputs/apk/release/app-release.apk`

## Getting Gemini API Key

1. Visit [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Sign in with your Google account
3. Click "Create API Key"
4. Copy the generated key
5. Add it to your `local.properties` file

**Note**: Keep your API key secure and never commit it to version control!

## Firebase Setup (Optional)

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Add an Android app with package name: `com.tarotreader`
3. Download `google-services.json`
4. Enable these services in Firebase Console:
   - **Firestore**: For cloud storage
   - **Authentication**: For user accounts
   - **Storage**: For images

## Support

For issues and questions:
- GitHub Issues: [Create an issue](https://github.com/LexiconAngelus93/AI-Tarot-Reader/issues)
- Documentation: Check the project README and code comments

## License

This project is licensed under the MIT License - see the LICENSE file for details.