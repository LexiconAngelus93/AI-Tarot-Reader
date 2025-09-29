# AI Tarot Reader

An Android application that combines traditional Tarot reading practices with modern AI technology to provide comprehensive digital Tarot experiences.

## Features

### 1. AI-Powered Spread Analysis from Images
- Capture photos of physical Tarot spreads with your device camera
- AI image recognition identifies cards and their positions
- Automatic deck identification with user confirmation
- Generates holistic interpretations based on card positions and eigenvalue calculations
- Handles blurry images and incomplete spreads with error detection

### 2. Guided Digital Tarot Reading
- Select from a curated library of Tarot decks
- Choose from various spread layouts (Celtic Cross, Three Card, Seven Card, etc.)
- Interactive card shuffling with smooth animations
- Position-specific card interpretations
- Comprehensive reading narratives combining all card meanings

### 3. Custom Spread Creation
- Drag-and-drop interface for designing custom spreads
- Assign custom meanings to each position in your spread
- Save and manage your personalized spread layouts
- Integration with the digital reading experience

### 4. Reading Diary & History
- Maintain a comprehensive log of all your readings
- Search and filter readings by date, deck used, or spread type
- Add personal notes and reflections to each reading
- Share readings with others through social media or messaging apps

### 5. Tarot Deck & Card Dictionary
- Dynamic library of Tarot decks and cards
- Detailed card information including images, upright meanings, reversed meanings, and keywords
- Explore card symbolism and learn about different decks
- Search functionality to quickly find specific cards

### 6. Daily Card Draw
- Receive a daily card draw with orientation (upright or reversed)
- Personal reflection feature to record your thoughts on the daily card
- View recent daily draws in a scrollable history
- Share daily draws with others

## Technical Implementation

### Architecture
The app follows MVVM architecture with Clean Architecture principles, organized into three layers:
- **Presentation Layer**: UI components using Jetpack Compose
- **Domain Layer**: Business logic and use cases
- **Data Layer**: Data sources including Room database and Firebase

### Technology Stack
- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern UI toolkit
- **Room**: Local database for storing readings, decks, and cards
- **Firebase**: Cloud database for synchronization
- **TensorFlow Lite**: AI image recognition for physical spreads
- **Dagger Hilt**: Dependency injection
- **Navigation Component**: Screen navigation

### Data Models
The app includes comprehensive data models for:
- Tarot cards (with upright/reversed meanings and keywords)
- Tarot decks (with metadata and card collections)
- Spread positions (with custom meanings)
- Tarot spreads (collections of positions)
- Readings (complete reading sessions with interpretations)
- Daily draws (daily card selections with reflections)
- User preferences (customization settings)

### AI Image Recognition
The AI-powered image recognition system uses TensorFlow Lite to:
1. Process images captured through the device camera
2. Detect and identify Tarot cards in the image
3. Map card positions according to the spread layout
4. Generate interpretations based on the identified spread

## Development Phases

1. **Foundation**: Project setup, architecture implementation, database schema
2. **Digital Reading Experience**: Deck/spread selection, card shuffling, reading generation
3. **Custom Spread Creation**: Spread design canvas, position editing, saving functionality
4. **Reading History**: Reading log database, browsing interface, filtering capabilities
5. **Tarot Dictionary**: Card database, browsing interface, search functionality
6. **AI Image Recognition**: Camera functionality, image processing, card detection
7. **Additional Features**: Daily card draw, user preferences, sharing functionality
8. **Polishing**: UI refinement, animations, accessibility features, performance optimization

## Testing

The app includes unit tests for core functionality, particularly for the daily draw feature and user preferences system.

## License

This project is licensed under the MIT License - see the LICENSE file for details.