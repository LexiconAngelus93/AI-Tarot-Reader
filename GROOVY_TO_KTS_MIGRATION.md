# Gradle Groovy to Kotlin DSL (KTS) Migration

## Overview
This document details the migration from Gradle Groovy scripts to Kotlin DSL (KTS) for improved type safety, better IDE support, and modern Gradle practices.

---

## Files Migrated

### 1. Root Build File
- **Before**: `build.gradle`
- **After**: `build.gradle.kts`

### 2. App Build File
- **Before**: `app/build.gradle`
- **After**: `app/build.gradle.kts`

### 3. Settings File
- **Before**: `settings.gradle`
- **After**: `settings.gradle.kts`

---

## Key Changes

### Syntax Differences

#### String Literals
```groovy
// Groovy
implementation 'androidx.core:core-ktx:1.17.0'

// Kotlin DSL
implementation("androidx.core:core-ktx:1.17.0")
```

#### Property Assignment
```groovy
// Groovy
compileSdk 36
minSdk 24

// Kotlin DSL
compileSdk = 36
minSdk = 24
```

#### Boolean Properties
```groovy
// Groovy
minifyEnabled true
useSupportLibrary true

// Kotlin DSL
isMinifyEnabled = true
useSupportLibrary = true
```

#### Method Calls
```groovy
// Groovy
proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'

// Kotlin DSL
proguardFiles(
    getDefaultProguardFile("proguard-android-optimize.txt"),
    "proguard-rules.pro"
)
```

#### Collections
```groovy
// Groovy
excludes += '/META-INF/{AL2.0,LGPL2.1}'

// Kotlin DSL
excludes += "/META-INF/{AL2.0,LGPL2.1}"
```

#### Variables
```groovy
// Groovy
ext.kotlin_version = '2.2.0'
implementation "androidx.compose.ui:ui:$compose_version"

// Kotlin DSL
extra["kotlin_version"] = "2.2.0"
val composeVersion = rootProject.extra["compose_version"]
implementation("androidx.compose.ui:ui:$composeVersion")
```

---

## Benefits of Kotlin DSL

### 1. Type Safety
- **Compile-time checking**: Errors caught during compilation, not at runtime
- **Auto-completion**: Better IDE support with intelligent code completion
- **Refactoring**: Safer refactoring with IDE support

### 2. Better IDE Support
- **IntelliJ IDEA / Android Studio**: Full Kotlin language support
- **Navigation**: Jump to definition, find usages
- **Documentation**: Inline documentation and parameter hints

### 3. Modern Gradle Features
- **Kotlin language features**: Use Kotlin's powerful language features
- **Type-safe accessors**: Access project properties in a type-safe manner
- **Better error messages**: More descriptive compilation errors

### 4. Consistency
- **Same language**: Use Kotlin for both build scripts and app code
- **Unified tooling**: Same IDE features for both
- **Easier maintenance**: Developers familiar with Kotlin can easily work with build scripts

---

## Migration Details

### Root build.gradle.kts

**Key Changes**:
- `ext` properties → `extra` properties
- String interpolation with `${}` syntax
- Function calls with parentheses
- Type-safe task registration

```kotlin
buildscript {
    extra.apply {
        set("kotlin_version", "2.2.0")
        set("compose_version", "1.9.2")
    }
    
    repositories {
        google()
        mavenCentral()
    }
    
    dependencies {
        classpath("com.android.tools.build:gradle:8.13.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${extra["kotlin_version"]}")
        classpath("com.google.gms:google-services:4.4.2")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.52")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
```

### app/build.gradle.kts

**Key Changes**:
- Plugin IDs with parentheses and quotes
- Property assignments with `=`
- Boolean properties prefixed with `is`
- Function calls with parentheses
- Type-safe dependency declarations

```kotlin
plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.devtools.ksp") version "2.2.0-1.0.29"
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.tarotreader"
    compileSdk = 36
    
    defaultConfig {
        applicationId = "com.tarotreader"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    // ... more dependencies
}
```

### settings.gradle.kts

**Key Changes**:
- Function calls with parentheses
- String literals with double quotes
- Type-safe include syntax

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

rootProject.name = "TarotReader"
include(":app")
```

---

## Common Patterns

### Accessing Root Project Properties
```kotlin
// In app/build.gradle.kts
val composeVersion = rootProject.extra["compose_version"]
implementation("androidx.compose.ui:ui:$composeVersion")
```

### Build Config Fields
```kotlin
buildConfigField(
    "String",
    "GEMINI_API_KEY",
    "&quot;${project.findProperty("GEMINI_API_KEY") ?: ""}&quot;"
)
```

### ProGuard Files
```kotlin
proguardFiles(
    getDefaultProguardFile("proguard-android-optimize.txt"),
    "proguard-rules.pro"
)
```

### Packaging Options
```kotlin
packaging {
    resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}
```

---

## IDE Configuration

### Android Studio
- **No additional configuration needed**
- Kotlin DSL is fully supported out of the box
- Gradle sync will automatically recognize `.kts` files

### IntelliJ IDEA
- **Version**: 2023.1 or later recommended
- **Kotlin Plugin**: Ensure latest version is installed
- **Gradle**: Version 8.0+ recommended

---

## Build Performance

### Initial Sync
- **First sync may be slower**: Kotlin DSL scripts need to be compiled
- **Subsequent syncs are fast**: Compiled scripts are cached

### Build Times
- **Similar to Groovy**: No significant performance difference
- **Incremental builds**: Same performance as Groovy
- **Clean builds**: Comparable performance

### Optimization Tips
1. **Use Gradle daemon**: Enabled by default
2. **Enable configuration cache**: Add to `gradle.properties`
3. **Parallel execution**: Enabled by default in Gradle 8+

---

## Troubleshooting

### Common Issues

#### 1. Unresolved Reference
**Problem**: IDE shows "Unresolved reference" errors
**Solution**: 
- Sync Gradle project
- Invalidate caches and restart IDE
- Check Kotlin plugin version

#### 2. Script Compilation Failed
**Problem**: Build fails with script compilation errors
**Solution**:
- Check syntax (parentheses, quotes, equals signs)
- Verify property names (e.g., `isMinifyEnabled` not `minifyEnabled`)
- Review migration guide for correct syntax

#### 3. Cannot Access Properties
**Problem**: Cannot access `ext` properties
**Solution**:
- Use `extra["property_name"]` instead of `ext.property_name`
- Cast to correct type if needed: `extra["version"] as String`

---

## Verification

### Build Commands
```bash
# Clean build
./gradlew clean

# Assemble debug
./gradlew assembleDebug

# Run tests
./gradlew test

# Assemble release
./gradlew assembleRelease
```

### Expected Results
- ✅ All builds complete successfully
- ✅ No deprecation warnings
- ✅ IDE provides full code completion
- ✅ No runtime errors

---

## Migration Checklist

- [x] Convert `build.gradle` → `build.gradle.kts`
- [x] Convert `app/build.gradle` → `app/build.gradle.kts`
- [x] Convert `settings.gradle` → `settings.gradle.kts`
- [x] Update all string literals to use double quotes
- [x] Update all property assignments to use `=`
- [x] Update all boolean properties to use `is` prefix
- [x] Update all function calls to use parentheses
- [x] Update all plugin declarations
- [x] Update all dependency declarations
- [x] Test build successfully
- [x] Verify IDE support works
- [x] Document migration

---

## References

- [Gradle Kotlin DSL Primer](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
- [Migrating build logic from Groovy to Kotlin](https://docs.gradle.org/current/userguide/migrating_from_groovy_to_kotlin_dsl.html)
- [Android Gradle Plugin DSL Reference](https://developer.android.com/reference/tools/gradle-api)
- [Kotlin DSL Samples](https://github.com/gradle/kotlin-dsl-samples)

---

## Conclusion

The migration from Groovy to Kotlin DSL provides:
- ✅ **Better type safety** with compile-time checking
- ✅ **Improved IDE support** with full Kotlin features
- ✅ **Modern best practices** aligned with Gradle recommendations
- ✅ **Consistency** with app code language
- ✅ **Future-proof** build configuration

All build files are now using Kotlin DSL, providing a better development experience and maintainability.

---

*Migration Completed: 2025-01-02*
*Gradle Version: 9.2-milestone-2*
*Kotlin Version: 2.2.0*