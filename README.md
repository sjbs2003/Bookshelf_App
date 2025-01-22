```markdown
# 📚 Bookshelf App

A modern Android application that allows users to search and explore books using both Google Books API and Open Library API. Built as part of the Android Developer Internship assignment for Vijayi WFH Technologies.

## 🌟 Features

- **Dual API Integration**: Seamlessly switch between Google Books and Open Library APIs
- **Search Functionality**: 
  - Search books by title, author, or subject
  - Real-time search with debouncing
  - Toggle between different search types
- **Book Details**:
  - Comprehensive book information including title, authors, and publication date
  - Book cover images with loading states
  - Detailed descriptions and additional information
- **Modern UI**:
  - Material Design 3 implementation
  - Responsive layouts
  - Loading states and error handling
  - Smooth animations and transitions

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Koin
- **Networking**: 
  - Retrofit
  - RxKotlin for concurrent API calls
- **Image Loading**: Coil
- **Other Libraries**:
  - kotlinx.serialization
  - Android Architecture Components

## 📱 Screenshots

[Screenshots will be added here]

## 🏗️ Architecture

The app follows MVVM architecture pattern and is organized into the following packages:

- `model`: Data classes and repository implementations
- `network`: API service interfaces and networking setup
- `ui`: Compose UI components and screens
- `viewModel`: ViewModels for managing UI state
- `di`: Dependency injection modules

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- Minimum SDK 21
- Target SDK 34

### Installation

1. Clone the repository
```bash
git clone https://github.com/sjbs2003/bookshelf-app.git
```

2. Open the project in Android Studio

3. Build and run the app

## 🧪 Testing

The project includes unit tests for:
- ViewModels
- Repository implementations
- API response mapping

## 👤 Author

**Sarthak Jain**
- GitHub: [@sjbs2003](https://github.com/sjbs2003)

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

## 📝 Assignment Requirements Met

✅ Dual API Integration with simultaneous calls using Single.zip  
✅ MVVM Architecture implementation  
✅ Dependency Injection using Koin  
✅ Efficient error handling  
✅ Proper use of Jetpack Compose  
✅ Clean and maintainable code structure

## 📋 Future Improvements

- Implement caching for offline support
- Add unit tests for additional components
- Enhance UI/UX with animations
- Add bookmarking functionality
- Implement advanced search filters
```
