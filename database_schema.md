# Tarot Reading Android App - Database Schema

## Overview

The app will use a combination of local database (Room) for offline functionality and cloud database (Firebase Firestore) for synchronization and backup. Below is the detailed schema for both databases.

## Local Database (Room)

### 1. Decks Table

```
Table: decks
- id: INTEGER (Primary Key)
- name: TEXT
- description: TEXT
- creator: TEXT
- image_path: TEXT
- card_back_image_path: TEXT
- is_default: BOOLEAN
- is_premium: BOOLEAN
- created_at: TIMESTAMP
- updated_at: TIMESTAMP
- remote_id: TEXT (for cloud sync)
```

### 2. Cards Table

```
Table: cards
- id: INTEGER (Primary Key)
- deck_id: INTEGER (Foreign Key -> decks.id)
- name: TEXT
- number: INTEGER
- arcana_type: TEXT (Major/Minor)
- suit: TEXT (for Minor Arcana)
- image_path: TEXT
- keywords: TEXT
- upright_meaning: TEXT
- reversed_meaning: TEXT
- element: TEXT
- zodiac: TEXT
- numerology: TEXT
- symbolism: TEXT
- created_at: TIMESTAMP
- updated_at: TIMESTAMP
- remote_id: TEXT (for cloud sync)
```

### 3. Spreads Table

```
Table: spreads
- id: INTEGER (Primary Key)
- name: TEXT
- description: TEXT
- is_custom: BOOLEAN
- is_default: BOOLEAN
- is_premium: BOOLEAN
- user_id: TEXT (null for default spreads)
- created_at: TIMESTAMP
- updated_at: TIMESTAMP
- remote_id: TEXT (for cloud sync)
```

### 4. Spread Positions Table

```
Table: spread_positions
- id: INTEGER (Primary Key)
- spread_id: INTEGER (Foreign Key -> spreads.id)
- position_number: INTEGER
- name: TEXT
- description: TEXT
- x_position: FLOAT (relative position for layout)
- y_position: FLOAT (relative position for layout)
- rotation: FLOAT (card rotation in degrees)
- created_at: TIMESTAMP
- updated_at: TIMESTAMP
- remote_id: TEXT (for cloud sync)
```

### 5. Readings Table

```
Table: readings
- id: INTEGER (Primary Key)
- user_id: TEXT
- deck_id: INTEGER (Foreign Key -> decks.id)
- spread_id: INTEGER (Foreign Key -> spreads.id)
- title: TEXT
- question: TEXT
- interpretation: TEXT
- eigenvalue: INTEGER
- eigenvalue_card_id: INTEGER (Foreign Key -> cards.id)
- is_physical_reading: BOOLEAN
- physical_image_path: TEXT (if is_physical_reading is true)
- created_at: TIMESTAMP
- updated_at: TIMESTAMP
- remote_id: TEXT (for cloud sync)
```

### 6. Reading Cards Table

```
Table: reading_cards
- id: INTEGER (Primary Key)
- reading_id: INTEGER (Foreign Key -> readings.id)
- card_id: INTEGER (Foreign Key -> cards.id)
- position_id: INTEGER (Foreign Key -> spread_positions.id)
- is_reversed: BOOLEAN
- position_interpretation: TEXT
- created_at: TIMESTAMP
- updated_at: TIMESTAMP
- remote_id: TEXT (for cloud sync)
```

### 7. User Notes Table

```
Table: user_notes
- id: INTEGER (Primary Key)
- reading_id: INTEGER (Foreign Key -> readings.id)
- content: TEXT
- created_at: TIMESTAMP
- updated_at: TIMESTAMP
- remote_id: TEXT (for cloud sync)
```

### 8. User Preferences Table

```
Table: user_preferences
- id: INTEGER (Primary Key)
- user_id: TEXT
- default_deck_id: INTEGER (Foreign Key -> decks.id)
- default_spread_id: INTEGER (Foreign Key -> spreads.id)
- theme: TEXT
- card_back_design: TEXT
- animation_speed: TEXT
- interpretation_detail_level: TEXT
- enable_cloud_sync: BOOLEAN
- last_sync_timestamp: TIMESTAMP
- created_at: TIMESTAMP
- updated_at: TIMESTAMP
```

## Cloud Database (Firebase Firestore)

### 1. Users Collection

```
Collection: users
Document ID: {user_id}
Fields:
- email: string
- displayName: string
- photoURL: string
- createdAt: timestamp
- lastLoginAt: timestamp
- subscriptionStatus: string
- subscriptionExpiryDate: timestamp
- preferences: {
    defaultDeckId: string,
    defaultSpreadId: string,
    theme: string,
    cardBackDesign: string,
    animationSpeed: string,
    interpretationDetailLevel: string
  }
```

### 2. Decks Collection

```
Collection: decks
Document ID: {deck_id}
Fields:
- name: string
- description: string
- creator: string
- imageUrl: string
- cardBackImageUrl: string
- isDefault: boolean
- isPremium: boolean
- createdAt: timestamp
- updatedAt: timestamp
```

### 3. Cards Collection

```
Collection: decks/{deck_id}/cards
Document ID: {card_id}
Fields:
- name: string
- number: number
- arcanaType: string
- suit: string
- imageUrl: string
- keywords: array<string>
- uprightMeaning: string
- reversedMeaning: string
- element: string
- zodiac: string
- numerology: string
- symbolism: string
- createdAt: timestamp
- updatedAt: timestamp
```

### 4. Spreads Collection

```
Collection: spreads
Document ID: {spread_id}
Fields:
- name: string
- description: string
- isCustom: boolean
- isDefault: boolean
- isPremium: boolean
- userId: string (null for default spreads)
- createdAt: timestamp
- updatedAt: timestamp
- positions: array<{
    positionNumber: number,
    name: string,
    description: string,
    xPosition: number,
    yPosition: number,
    rotation: number
  }>
```

### 5. Readings Collection

```
Collection: users/{user_id}/readings
Document ID: {reading_id}
Fields:
- deckId: string
- spreadId: string
- title: string
- question: string
- interpretation: string
- eigenvalue: number
- eigenvalueCardId: string
- isPhysicalReading: boolean
- physicalImageUrl: string
- createdAt: timestamp
- updatedAt: timestamp
- cards: array<{
    cardId: string,
    positionId: string,
    isReversed: boolean,
    positionInterpretation: string
  }>
- notes: array<{
    content: string,
    createdAt: timestamp
  }>
```

## Relationships and Data Flow

1. **One-to-Many Relationships**:
   - One Deck has Many Cards
   - One Spread has Many Positions
   - One Reading has Many Cards
   - One Reading has Many Notes

2. **Many-to-Many Relationships**:
   - Many Cards can be in Many Readings (through reading_cards table)

3. **Data Synchronization Flow**:
   - Local changes are timestamped
   - Periodic sync with cloud database
   - Conflict resolution based on timestamps
   - Offline changes queued for sync when connection is restored

## Data Migration and Versioning

1. **Database Versioning**:
   - Room database version controlled through migrations
   - Schema changes handled with proper migration paths

2. **Initial Data Population**:
   - Default decks and spreads pre-populated
   - Standard Tarot card data included in app package

3. **Backup and Restore**:
   - User data exportable to local storage
   - Import functionality for restoring from backup
   - Cloud backup through Firebase

## Security Considerations

1. **Local Data Security**:
   - Sensitive user data encrypted at rest
   - Optional app lock feature

2. **Cloud Security**:
   - Firebase Authentication for user identity
   - Firestore security rules to enforce access control
   - User data isolated by user ID
   - Encrypted transmission of data