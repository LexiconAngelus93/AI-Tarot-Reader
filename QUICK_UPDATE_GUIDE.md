# Quick Dependency Update Guide

## 🚨 Critical Action Items

### 1. Gemini AI SDK (0.1.2 → 0.9.0) - IMMEDIATE ATTENTION REQUIRED

**File to Review**: `app/src/main/java/com/tarotreader/data/ai/GeminiAIService.kt`

**What Changed**:
- Complete API redesign
- New initialization method
- Different response handling
- Enhanced error handling

**Action Required**:
```kotlin
// OLD API (0.1.2)
val generativeModel = GenerativeModel(
    modelName = "gemini-pro",
    apiKey = apiKey
)

// NEW API (0.9.0) - Verify this matches current implementation
val generativeModel = GenerativeModel(
    modelName = "gemini-1.5-flash",
    apiKey = apiKey,
    generationConfig = generationConfig {
        temperature = 0.7f
        topK = 40
        topP = 0.95f
    }
)
```

**Test These Functions**:
- ✅ `generateReading()`
- ✅ `generateDailyCardReading()`
- ✅ `analyzeSpreadFromImage()`
- ✅ Error handling and fallbacks

### 2. Build & Sync

```bash
# Navigate to project
cd AI-Tarot-Reader

# Clean build
./gradlew clean

# Refresh dependencies
./gradlew --refresh-dependencies

# Build project
./gradlew assembleDebug

# Run tests
./gradlew test
```

## 📊 Version Summary

| Category | Dependency | Old → New |
|----------|-----------|-----------|
| 🔴 **CRITICAL** | Gemini AI | 0.1.2 → **0.9.0** |
| 🟡 **HIGH** | Compose UI | 1.9.2 → **1.10.0-alpha04** |
| 🟡 **HIGH** | Material3 | 1.4.0 → **1.5.0-alpha04** |
| 🟡 **HIGH** | Lifecycle | 2.9.4 → **2.10.0-alpha04** |
| 🟢 **MEDIUM** | Hilt | 2.52 → **2.57.2** |
| 🟢 **MEDIUM** | Coroutines | 1.7.3 → **1.10.2** |
| 🟢 **MEDIUM** | Coil | 2.5.0 → **2.7.0** |
| 🟢 **MEDIUM** | WorkManager | 2.9.1 → **2.11.0-beta01** |

## ⚠️ Alpha/Beta Dependencies

**6 dependencies are NOT stable:**
1. Compose UI (alpha04)
2. Material3 (alpha04)
3. Lifecycle (alpha04)
4. Activity Compose (alpha09)
5. WorkManager (beta01)
6. DataStore (alpha02)

**Recommendation**: Test thoroughly before production release.

## 🧪 Quick Test Checklist

### Must Test Immediately
- [ ] App builds successfully
- [ ] App launches without crashes
- [ ] Gemini AI generates readings
- [ ] Camera functionality works
- [ ] Navigation flows correctly
- [ ] Database operations work
- [ ] Images load properly

### Test Before Production
- [ ] All screens render correctly
- [ ] Permissions flow works
- [ ] Background sync functions
- [ ] Offline mode works
- [ ] Settings save correctly
- [ ] Share functionality works
- [ ] All animations smooth

## 🔄 Quick Rollback

If critical issues occur:

**1. Edit `app/build.gradle.kts`**:
```kotlin
// Revert these key dependencies
implementation("androidx.compose.ui:ui:1.9.2")
implementation("com.google.dagger:hilt-android:2.52")
implementation("com.google.ai.client.generativeai:generativeai:0.1.2")
```

**2. Edit `build.gradle.kts`**:
```kotlin
set("compose_version", "1.9.2")
classpath("com.google.dagger:hilt-android-gradle-plugin:2.52")
```

**3. Sync & Build**:
```bash
./gradlew clean
./gradlew assembleDebug
```

## 📝 Files Modified

1. `app/build.gradle.kts` - All dependency versions
2. `build.gradle.kts` - Compose version, Hilt plugin

## 📚 Documentation

- `DEPENDENCY_UPDATES_2025.md` - Full details
- `DEPENDENCY_UPDATE_SUMMARY.md` - Complete summary
- `QUICK_UPDATE_GUIDE.md` - This file

## 🎯 Expected Benefits

- ✅ Faster build times (KSP optimizations)
- ✅ Better UI performance (Compose improvements)
- ✅ Enhanced AI capabilities (Gemini 0.9.0)
- ✅ Improved image loading (Coil 2.7.0)
- ✅ Better coroutines performance
- ✅ More reliable background tasks

## ⏱️ Time Estimates

- **Build & Sync**: 5-10 minutes
- **Gemini AI Review**: 30-60 minutes
- **Basic Testing**: 1-2 hours
- **Comprehensive Testing**: 4-8 hours

## 🆘 Troubleshooting

### Build Fails
```bash
./gradlew clean
./gradlew --refresh-dependencies
./gradlew assembleDebug --stacktrace
```

### KSP Issues
- Check all `@Inject` constructors
- Verify Hilt modules
- Rebuild project

### Compose Issues
- Check for deprecated APIs
- Update composable functions
- Test on different screen sizes

### Gemini AI Issues
- Verify API key
- Check network connectivity
- Review error logs
- Test fallback mechanisms

## 📞 Support

For issues or questions:
1. Check error logs
2. Review documentation
3. Test on clean build
4. Consider rollback if critical

---

**Last Updated**: January 2025  
**Status**: Ready for Testing  
**Priority**: HIGH - Gemini AI requires immediate review