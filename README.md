# 🎬 Whats2Watch-Kotlin

Whats2Watch is a modular Android app built with Kotlin, designed to help users discover, swipe, and review movies with a seamless experience.

## 🚀 Features

- 🔐 User authentication and registration  
- 🎞️ Movie discovery with filters (genre, rating, release date)  
- 🧠 Smart recommendations based on user preferences  
- ✍️ Create and submit reviews  
- 🏠 Create or join interactive rooms  
- 📲 Intuitive swipe interface for movie suggestions  
- 📦 Offline caching and persistent preferences  
- 🛡️ Robust error handling and thread-safe operations

## 🧱 Architecture

Follows the **MVVM** pattern with modular components:

- `📁 app` – Main logic and UI  
- `🧩 di` – Dependency injection setup  
- `🗃️ model` – Data models and entities  
- `📚 repositories` – Data access and storage logic  
- `🎨 ui` – UI components and themes  
- `🧠 viewmodels` – Business logic layer

## 🔧 Tech Stack

- 🛠️ **Kotlin Coroutines** – Async operations  
- 🧠 **StateFlow** – Reactive UI updates  
- 🏠 **Room** – Local data persistence  
- 🎞️ **TMDB API** – Movie data source  
- 🖼️ **Coil** – Optimized image loading

## 💡 Recommendation Engine

- 📊 Analyzes liked genres, eras, actors/directors  
- 🔥 Combines trending and obscure picks  
- 🤖 Predictive preloading and smart caching  
- 🧮 Scoring based on relevance, IMDb, and randomness

## 🛠️ Setup Instructions

1. 📥 Clone the repo:
   ```bash
   git clone https://github.com/your-username/whats2watch-kotlin.git
   ```
2. 🧑‍💻 Open with Android Studio
3. 🏗️ Build the project using Gradle
4. ▶️ Run on emulator or Android device
