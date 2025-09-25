# Playlist App

A Kotlin Multiplatform Mobile (KMM) application with clean architecture, clean code principles, and iOS development practices with cross-platform business logic sharing.

## Project Overview

This application is a single screen audio streaming app with shared Kotlin business logic and native iOS SwiftUI presentation. Using the power of KMM for business logic sharing while maintaining native iOS user experience.

### Key Features

- **Shared Business Logic**: Kotlin Multiplatform for cross-platform business logic reuse
- **Native iOS UI**: SwiftUI implementation with @Observable pattern and proper iOS integration
- **Modern Architecture**: Clean Architecture with MVVM pattern and reactive programming
- **Professional Testing**: Unit tests with mocking strategies for both Kotlin and Swift layers
- **State Management**: Reactive state management with playing, paused, and error states
- **Network Integration**: RESTful API integration using Ktor
- **iOS Platform Integration**: Native AVPlayer integration with proper iOS media controls

## Architecture

### Architecture Pattern: Clean Architecture + MVVM

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer (iOS)                 │ 
│  ┌─────────────────┐              ┌─────────────────┐       │
│  │   SwiftUI iOS   │              │ ContentViewModel│       │
│  │   ContentView   │◄─────────────┤   (@Observable) │       │
│  │    RowView      │              │                 │       │
│  └─────────────────┘              └─────────────────┘       │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                     Domain Layer (Shared)                   │
│  ┌─────────────────┐              ┌─────────────────┐       │
│  │ MediaService    │◄─────────────┤ PlayerState     │       │
│  │ (Interface)     │              │ (Sealed Class)  │       │
│  └─────────────────┘              └─────────────────┘       │
│                 │                                           │
│  ┌─────────────────┐              ┌─────────────────┐       │
│  │PlaylistRepositor│              │   MediaPlayer   │       │
│  │   (Interface)   │              │   (Interface)   │       │
│  └─────────────────┘              └─────────────────┘       │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                      Data Layer (Shared)                    │
│  ┌─────────────────┐              ┌─────────────────┐       │
│  │MediaServiceImpl │              │PlaylistRepoImpl │       │
│  │                 │              │                 │       │
│  └─────────────────┘              └─────────────────┘       │
│                 │                           │               │
│  ┌─────────────────┐              ┌─────────────────┐       │
│  │   ApiService    │              │   HttpClient    │       │
│  │ (Ktor Client)   │              │   (Network)     │       │
│  └─────────────────┘              └─────────────────┘       │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                Platform-Specific Layer                      │
│  ┌─────────────────┐              ┌─────────────────┐       │
│  │ IOSMediaPlayer  │              │  AndroidPlayer  │       │
│  │  (AVPlayer)     │              │                 │       │
│  │                 │              │ Not Implemented │       │
│  └─────────────────┘              └─────────────────┘       │
└─────────────────────────────────────────────────────────────┘
```

### Core Components

#### 1. **Shared Business Logic** (`/shared`)
- **Domain Layer**: Pure business logic with no platform dependencies
  - `MediaService`: Core service interface for media operations
  - `PlayerState`: Sealed class representing possible player states (buffering state is missing)
  - `PlaylistRepository`: Data access abstraction layer
  
- **Data Layer**: Implementation of business logic
  - `MediaServiceImpl`: Concrete implementation with dependency injection
  - `PlaylistRepositoryImpl`: API integration with proper error handling
  - `KtorApiService`: HTTP client

#### 2. **Platform-Specific Implementations**
- **iOS** (`/iosApp`): Native SwiftUI implementation
  - `ContentViewModel`: SwiftUI-compatible view model using KMP-NativeCoroutines and @Observable pattern
  - `ContentView`: Main SwiftUI interface with playlist display and media controls
  - `RowView`: Custom SwiftUI component for playlist items with play/pause functionality
  - `IOSMediaPlayer`: AVPlayer integration with proper iOS media controls and background playback
  
- **Android** (`/composeApp`): Architecture ready for future implementation
  - Prepared structure for Jetpack Compose implementation
  - Shared business logic can be easily integrated when Android UI is implemented

#### 3. **Dependency Injection**
- `SharedDependencies`: Centralized DI container using object singleton pattern
- Factory pattern for platform-specific media player implementations
- Lazy initialization for memory management

## State Management

### Player States (Sealed Class Architecture)
```kotlin
sealed class PlayerState {
    data object Stopped : PlayerState()
    data class Playing(val mediaItem: PlaylistItem) : PlayerState()
    data class Paused(val mediaItem: PlaylistItem) : PlayerState()
    data class Error(val mediaItem: PlaylistItem) : PlayerState()
}
```

### Reactive Programming
- **StateFlow**: Reactive state management
- **KMP-NativeCoroutines**: iOS integration with Swift async/await
- **Coroutines**: Structured concurrency for async operations

## Testing Strategy

#### Test Coverage
- **Swift Tests**: iOS-specific integration tests with MockMediaService
- **Kotlin Tests**: Unit tests for shared business logic
- **Mock Strategy**: Mockable components
- **Timeout Testing**: Timeout scenario testing

## Project Structure

```
MyApplication/
├── shared/                          # Shared Kotlin Multiplatform code
│   ├── commonMain/
│   │   ├── domain/                 # Business logic layer
│   │   │   ├── service/           # Service interfaces
│   │   │   └── repository/        # Repository interfaces
│   │   ├── data/                  # Data implementation layer
│   │   │   ├── api/              # Network layer (Ktor)
│   │   │   ├── model/            # Data models
│   │   │   ├── repository/       # Repository implementations
│   │   │   └── service/          # Service implementations
│   │   ├── player/               # Media player abstractions
│   │   │   ├── MediaPlayer.kt    # Media player interface
│   │   │   └── PlayerState.kt    # Player state sealed class
│   │   ├── di/                   # Dependency injection
│   │   │   └── SharedDependencies.kt # DI container
│   │   └── MediaPlayerFactory.kt # Platform factory
│   ├── iosMain/                   # iOS-specific implementations
│   │   ├── IOSMediaPlayer.kt     # AVPlayer implementation
│   │   └── MediaPlayerFactory.kt # iOS factory implementation
│   └── androidMain/               # Android-specific implementations
│       ├── AndroidMediaPlayer.kt # ExoPlayer implementation  
│       └── MediaPlayerFactory.kt # Android factory implementation
├── iosApp/                        # iOS SwiftUI application
│   ├── Presentation/              # SwiftUI views and ViewModels
│   │   ├── ContentView.swift     # Main playlist interface
│   │   ├── ContentViewModel.swift # SwiftUI ViewModel with KMP integration
│   │   ├── RowView.swift         # Playlist item component
│   │   └── TimeoutAsyncImage.swift # Custom async image loading

```

## Technical Highlights

### Modern Development Practices
- **Kotlin Multiplatform Mobile**: Business logic sharing with native iOS UI
- **Reactive Architecture**: StateFlow with coroutines for async operations
- **Dependency Injection**: Clean DI pattern with testable architecture
- **Error Handling**: Error states
- **Memory Management**: Proper lifecycle handling and resource cleanup
- **Native iOS Integration**: SwiftUI with @Observable pattern and KMP-NativeCoroutines

### Platform Integration
- **iOS**: Native AVPlayer integration with proper background playbook support
- **Swift Interoperability**: Kotlin-Swift integration using KMP-NativeCoroutines
- **State Synchronization**: Reactive state management between Kotlin business logic and SwiftUI
- **iOS Media Controls**: Integration with iOS media session and lock screen controls
- **Background Playback**: Native iOS background audio playback capabilities

### Network Architecture
- **Ktor Client**: HTTP client
- **JSON Serialization**: Type-safe JSON handling with kotlinx.serialization

