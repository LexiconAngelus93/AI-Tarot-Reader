# TarotReader - Android Tarot Reading App

TarotReader is a comprehensive Android application for Tarot card readings that bridges the gap between physical and digital Tarot practices. The app features AI-powered image recognition, custom spread creation, reading history, and an extensive Tarot dictionary.

## Features

### 1. AI-Powered Spread Analysis from Images
- Capture photos of physical Tarot spreads
- Automatic card detection and identification
- Spread layout recognition with position mapping
- Holistic interpretation generation with eigenvalue calculation
- Deck identification with user confirmation

### 2. Guided Digital Tarot Reading
- Curated library of Tarot decks (Rider-Waite, Thoth, Morgan-Greer)
- Selection of popular spreads (Celtic Cross, Three Card, Horseshoe, Daily Draw)
- Interactive card shuffling and selection with animations
- Position-specific interpretation generation
- Comprehensive reading narrative creation

### 3. Custom Spread Creation
- Drag-and-drop spread design interface
- Custom position meaning assignment
- Spread saving and management capabilities
- Integration with digital reading experience

### 4. Reading Diary & History
- Comprehensive reading log maintenance
- Search and filtering by date, deck, spread type
- Personal notes and reflection features
- Reading sharing capabilities
- Eigenvalue tracking for spiritual insights

### 5. Tarot Deck & Card Dictionary
- Dynamic library of Tarot decks and cards
- Detailed card information (images, meanings, keywords)
- Exploration and search functionality
- Numerological, elemental, and astrological associations
- Integration with reading experience

## Technical Architecture

### MVVM with Clean Architecture
The app follows the Model-View-ViewModel pattern with Clean Architecture principles:
- **Presentation Layer**: Compose UI components and ViewModels
- **Domain Layer**: Use cases and business logic
- **Data Layer**: Repositories, database entities, and data sources

### Technologies Used
- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern UI toolkit
- **Room Database**: Local data persistence
- **Firebase Firestore**: Cloud data storage
- **CameraX**: Camera functionality
- **TensorFlow Lite**: AI image recognition
- **Navigation Component**: Screen navigation

### Database Schema
The app uses Room for local data storage with the following entities:
- `TarotCardEntity`: Individual Tarot cards with meanings and associations
- `TarotDeckEntity`: Tarot decks with metadata
- `TarotSpreadEntity`: Tarot spread layouts
- `SpreadPositionEntity`: Individual positions within spreads
- `ReadingEntity`: Completed readings with interpretations
- `CardDrawingEntity`: Cards drawn in specific positions for each reading

## Project Structure

```
app/
├── src/main/java/com/tarotreader/
│   ├── data/
│   │   ├── model/           # Data models
│   │   ├── database/       # Room database components
│   │   ├── repository/     # Data repositories
│   │   └── ai/             # AI image recognition components
│   ├── domain/
│   │   ├── model/           # Domain models
│   │   ├── repository/     # Repository interfaces
│   │   └── usecase/        # Business logic use cases
│   ├── presentation/
│   │   ├── viewmodel/       # ViewModels
│   │   ├── screen/         # Compose UI screens
│   │   └── navigation/    # Navigation components
│   ├── di/                 # Dependency injection modules
│   └── provider/           # Initial data providers
```

## Implementation Progress

### Phase 1: Foundation (4 weeks) - COMPLETE
- [x] Set up Android project with Kotlin and Jetpack Compose
- [x] Implement MVVM architecture with Clean Architecture
- [x] Create feature modules structure
- [x] Set up Room database schema
- [x] Implement core data models
- [x] Set up Firebase integration
- [x] Create basic navigation structure

### Phase 2: Digital Reading Experience (4 weeks) - IN PROGRESS
- [x] Implement deck selection UI
- [x] Create spread library with curated spreads
- [x] Develop card shuffling mechanism
- [x] Build card selection interface
- [ ] Generate position-specific interpretations
- [ ] Create comprehensive reading narrative

### Phase 3: Custom Spread Creation (3 weeks) - NOT STARTED
- [ ] Design spread creation canvas
- [ ] Implement drag-and-drop functionality
- [ ] Create position meaning assignment
- [ ] Build spread saving mechanism
- [ ] Develop spread template library

### Phase 4: Reading History (3 weeks) - NOT STARTED
- [ ] Implement reading log database
- [ ] Create history browsing interface
- [ ] Build search and filtering capabilities
- [ ] Develop note-taking functionality
- [ ] Implement reading sharing features

### Phase 5: Tarot Dictionary (3 weeks) - NOT STARTED
- [ ] Create card database structure
- [ ] Implement browsing interface
- [ ] Build detailed card information display
- [ ] Develop search functionality
- [ ] Create learning modules

### Phase 6: AI Image Recognition (4 weeks) - NOT STARTED
- [ ] Implement camera interface
- [ ] Integrate TensorFlow Lite
- [ ] Create card detection model
- [ ] Build spread layout recognition
- [ ] Implement eigenvalue calculation

### Phase 7: Polishing (3 weeks) - NOT STARTED
- [ ] Refine UI with animations
- [ ] Optimize performance
- [ ] Implement accessibility features
- [ ] Add final touches and testing

## Unique Features

### Eigenvalue Calculation
Our proprietary eigenvalue algorithm provides deeper insights into the spiritual energy of each reading by analyzing:
- Card meanings (upright vs reversed)
- Card positions in the spread
- Numerological significance
- Elemental associations
- Astrological connections

### Bridge Between Physical and Digital
TarotReader uniquely bridges physical and digital Tarot practices:
- AI analysis of physical spreads
- Traditional digital reading experience
- Custom spread creation tools
- Comprehensive history and diary features

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Build and run the app

## Future Enhancements

- Offline functionality for all core features
- Community features for reading sharing
- Advanced learning modules for Tarot study
- Personalized reading recommendations
- Integration with wearable devices for daily draws

## License

This project is licensed under the MIT License - see the LICENSE file for details.