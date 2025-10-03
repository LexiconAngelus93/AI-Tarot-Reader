# Hilt Implementation Guide - AI Tarot Reader

## Overview
This guide documents the complete Hilt dependency injection implementation for the AI Tarot Reader Android application.

## What is Hilt?
Hilt is a dependency injection library for Android that reduces the boilerplate of doing manual dependency injection in your project. It's built on top of Dagger and provides a standard way to incorporate Dagger dependency injection into an Android application.

## Implementation Summary

### ✅ Completed Changes

#### 1. Application Class
**File**: `TarotReaderApplication.kt`

**Changes**:
- Added `@HiltAndroidApp` annotation
- Removed manual dependency initialization
- Simplified to only initialize Firebase

```kotlin
@HiltAndroidApp
class TarotReaderApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
```

#### 2. MainActivity
**File**: `MainActivity.kt`

**Changes**:
- Added `@AndroidEntryPoint` annotation
- Enables Hilt injection in the activity

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Activity code
}
```

#### 3. ViewModel
**File**: `TarotViewModel.kt`

**Changes**:
- Added `@HiltViewModel` annotation
- Added `@Inject` constructor annotation
- Removed manual dependency passing

```kotlin
@HiltViewModel
class TarotViewModel @Inject constructor(
    private val getAllDecksUseCase: GetAllDecksUseCase,
    // ... other dependencies
) : ViewModel() {
    // ViewModel code
}
```

#### 4. Hilt Modules Created/Updated

##### DatabaseModule
**File**: `di/DatabaseModule.kt`

**Purpose**: Provides database-related dependencies

**Provides**:
- `TarotDatabase` - Room database instance
- `TarotDao` - Database access object
- `DatabaseInitializer` - Database initialization

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TarotDatabase
    
    @Provides
    @Singleton
    fun provideTarotDao(database: TarotDatabase): TarotDao
    
    @Provides
    @Singleton
    fun provideDatabaseInitializer(dao: TarotDao): DatabaseInitializer
}
```

##### RepositoryModule
**File**: `di/RepositoryModule.kt`

**Purpose**: Provides repository dependencies

**Provides**:
- `FirebaseFirestore` - Firebase Firestore instance
- `TarotRepository` - Local repository
- `FirebaseTarotRepository` - Firebase repository

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore
    
    @Provides
    @Singleton
    fun provideLocalRepository(dao: TarotDao): TarotRepository
    
    @Provides
    @Singleton
    fun provideFirebaseRepository(firestore: FirebaseFirestore): FirebaseTarotRepository
}
```

##### UseCaseModule
**File**: `di/UseCaseModule.kt`

**Purpose**: Provides use case dependencies

**Provides**:
- All 12 use cases for the application
- Each use case is a singleton

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides @Singleton
    fun provideGetAllDecksUseCase(repository: TarotRepository): GetAllDecksUseCase
    
    // ... 11 more use case providers
}
```

##### AIModule (NEW)
**File**: `di/AIModule.kt`

**Purpose**: Provides AI-related dependencies

**Provides**:
- `GenerativeModel` - Gemini AI model for tarot readings

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AIModule {
    @Provides
    @Singleton
    fun provideGeminiModel(@ApplicationContext context: Context): GenerativeModel
}
```

##### WorkManagerModule (NEW)
**File**: `di/WorkManagerModule.kt`

**Purpose**: Provides WorkManager for background tasks

**Provides**:
- `WorkManager` - For scheduling background sync tasks

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object WorkManagerModule {
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager
}
```

##### DataStoreModule (NEW)
**File**: `di/DataStoreModule.kt`

**Purpose**: Provides DataStore for preferences

**Provides**:
- `DataStore<Preferences>` - For storing user preferences

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences>
}
```

#### 5. Removed Files
- `ViewModelModule.kt` - No longer needed with `@HiltViewModel`

## Dependency Graph

```
Application (@HiltAndroidApp)
    ├── DatabaseModule
    │   ├── TarotDatabase
    │   ├── TarotDao
    │   └── DatabaseInitializer
    │
    ├── RepositoryModule
    │   ├── FirebaseFirestore
    │   ├── TarotRepository (depends on TarotDao)
    │   └── FirebaseTarotRepository (depends on FirebaseFirestore)
    │
    ├── UseCaseModule
    │   ├── GetAllDecksUseCase (depends on TarotRepository)
    │   ├── GetDeckByIdUseCase (depends on TarotRepository)
    │   ├── GetAllSpreadsUseCase (depends on TarotRepository)
    │   ├── GetSpreadByIdUseCase (depends on TarotRepository)
    │   ├── GetAllCardsUseCase (depends on TarotRepository)
    │   ├── GetCardsByDeckUseCase (depends on TarotRepository)
    │   ├── SaveReadingUseCase (depends on TarotRepository)
    │   ├── GetAllReadingsUseCase (depends on TarotRepository)
    │   ├── GetCardByIdUseCase (depends on TarotRepository)
    │   ├── AnalyzeSpreadFromImageUseCase (depends on TarotRepository)
    │   ├── GenerateReadingUseCase (depends on TarotRepository)
    │   └── CalculateEigenvalueUseCase
    │
    ├── AIModule
    │   └── GenerativeModel (Gemini AI)
    │
    ├── WorkManagerModule
    │   └── WorkManager
    │
    └── DataStoreModule
        └── DataStore<Preferences>

MainActivity (@AndroidEntryPoint)
    └── TarotViewModel (@HiltViewModel)
        ├── All 12 UseCases (injected)
        └── Automatically managed by Hilt
```

## Benefits of This Implementation

### 1. Compile-Time Safety
- All dependencies are verified at compile time
- No runtime crashes due to missing dependencies
- Type-safe dependency injection

### 2. Reduced Boilerplate
- No manual dependency passing
- No factory classes needed
- Automatic ViewModel creation

### 3. Better Testing
- Easy to mock dependencies
- Can inject test implementations
- Isolated unit testing

### 4. Lifecycle Awareness
- Automatic cleanup of resources
- Proper scoping of dependencies
- Memory leak prevention

### 5. Performance
- Singleton instances where appropriate
- Lazy initialization
- Efficient dependency graph

## Usage Examples

### In Composables
```kotlin
@Composable
fun HomeScreen() {
    val viewModel: TarotViewModel = hiltViewModel()
    
    // Use viewModel
    val decks by viewModel.decks.collectAsState()
}
```

### In Fragments (if needed)
```kotlin
@AndroidEntryPoint
class TarotFragment : Fragment() {
    private val viewModel: TarotViewModel by viewModels()
    
    // Use viewModel
}
```

### Injecting Dependencies Directly
```kotlin
@AndroidEntryPoint
class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    
    @Inject
    lateinit var repository: TarotRepository
    
    // Use repository
}
```

## Configuration

### Gemini API Key Setup

#### Option 1: local.properties (Recommended)
```properties
GEMINI_API_KEY=your_api_key_here
```

#### Option 2: Assets File
Create `app/src/main/assets/gemini_api_key.txt`:
```
your_api_key_here
```

### Build Configuration
The `app/build.gradle` already includes:
```groovy
buildConfigField "String", "GEMINI_API_KEY", "&quot;${project.findProperty("GEMINI_API_KEY") ?: ""}&quot;"
```

## Testing with Hilt

### Unit Tests
```kotlin
@HiltAndroidTest
class TarotViewModelTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var repository: TarotRepository
    
    @Before
    fun init() {
        hiltRule.inject()
    }
    
    @Test
    fun testViewModel() {
        // Test code
    }
}
```

### Providing Test Dependencies
```kotlin
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
object TestRepositoryModule {
    @Provides
    @Singleton
    fun provideTestRepository(): TarotRepository {
        return FakeTarotRepository()
    }
}
```

## Migration Checklist

### ✅ Completed
- [x] Add Hilt dependencies to build.gradle
- [x] Add @HiltAndroidApp to Application
- [x] Add @AndroidEntryPoint to MainActivity
- [x] Convert DatabaseModule to Hilt
- [x] Convert RepositoryModule to Hilt
- [x] Convert UseCaseModule to Hilt
- [x] Add @HiltViewModel to TarotViewModel
- [x] Create AIModule for Gemini AI
- [x] Create WorkManagerModule
- [x] Create DataStoreModule
- [x] Remove ViewModelModule

### 🔄 Next Steps (Optional)
- [ ] Add Hilt to other Activities/Fragments if created
- [ ] Implement WorkManager workers with Hilt
- [ ] Create PreferencesManager using DataStore
- [ ] Add Hilt testing setup
- [ ] Create custom scopes if needed

## Troubleshooting

### Common Issues

#### 1. "Hilt components not generated"
**Solution**: Clean and rebuild project
```bash
./gradlew clean
./gradlew build
```

#### 2. "Cannot find symbol: DaggerApplicationComponent"
**Solution**: Make sure KSP is properly configured and run build

#### 3. "No injector factory bound for Class"
**Solution**: Add @AndroidEntryPoint to the class

#### 4. "Dagger does not support injection into private fields"
**Solution**: Make injected fields public or internal

#### 5. ViewModel not created
**Solution**: Use `hiltViewModel()` in Composables or `by viewModels()` in Fragments

## Best Practices

### 1. Use Constructor Injection
```kotlin
class MyRepository @Inject constructor(
    private val dao: TarotDao
) {
    // Repository code
}
```

### 2. Prefer Singleton for Expensive Objects
```kotlin
@Provides
@Singleton
fun provideDatabase(): TarotDatabase {
    // Database creation
}
```

### 3. Use Qualifiers for Multiple Implementations
```kotlin
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalRepository

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RemoteRepository
```

### 4. Keep Modules Focused
- One module per layer (Database, Repository, UseCase, etc.)
- Clear separation of concerns
- Easy to understand and maintain

### 5. Document Dependencies
- Add comments explaining complex dependencies
- Document why certain scopes are used
- Explain any non-obvious configurations

## Performance Considerations

### 1. Singleton Scope
- Used for expensive objects (Database, Network clients)
- Shared across the entire application
- Created once and reused

### 2. ViewModelScoped
- Automatically handled by @HiltViewModel
- Survives configuration changes
- Cleared when ViewModel is cleared

### 3. ActivityScoped
- Lives as long as the Activity
- Useful for Activity-specific dependencies
- Automatically cleaned up

## Security Considerations

### 1. API Keys
- Never commit API keys to version control
- Use local.properties or environment variables
- Provide fallback mechanisms

### 2. Sensitive Data
- Don't inject sensitive data directly
- Use secure storage mechanisms
- Encrypt when necessary

## Conclusion

The Hilt implementation is now complete and provides:
- ✅ Clean dependency injection throughout the app
- ✅ Compile-time safety
- ✅ Easy testing
- ✅ Better code organization
- ✅ Reduced boilerplate
- ✅ Improved maintainability

All modules are properly configured and ready to use. The application now follows modern Android development best practices with Hilt dependency injection.

---

**Implementation Date**: January 2025  
**Hilt Version**: 2.57.2  
**Status**: Complete and Production-Ready