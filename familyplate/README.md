# FamilyPlate

**Family Meal Tracker** — A Kotlin Multiplatform app for tracking meals and sharing them with your family.

## Description

FamilyPlate helps families log meals together, see what everyone's eating, and build healthier eating habits as a unit. Plan future features like Instagram import to bring your food photos into the app.

## Features

- **Meal tracking** — Log breakfast, lunch, dinner, and snacks
- **Family sharing** — Create or join a family to share meals with household members
- **Instagram import** (planned) — Import food photos from Instagram

## Tech Stack

- **Kotlin Multiplatform (KMP)** — Shared business logic across Android and iOS
- **Compose Multiplatform** — Declarative UI for all platforms
- **Firebase** — Authentication and Cloud Firestore
- **Kotlin** — Dependency injection

## Firebase Setup Instructions

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create a new project
3. Enable **Authentication** → **Email/Password** sign-in method
4. Enable **Cloud Firestore** (start in test mode for development)
5. **For Android**: Add an Android app with package `com.familyplate.app`, download `google-services.json`, and place it in `composeApp/`
6. **For iOS**: Add an iOS app, download `GoogleService-Info.plist`, and place it in the Xcode project

## How to Build

### Android

Open the project in Android Studio or IntelliJ IDEA (with Kotlin Multiplatform support), then run on a device or emulator.

### iOS

Open `iosApp/iosApp.xcodeproj` in Xcode and run on a simulator or device.

## Firestore Security Rules

Basic example for development:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    match /families/{familyId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update: if request.auth != null && request.auth.uid in resource.data.memberIds;
    }
  }
}
```

## Phase Roadmap

| Phase | Focus |
|-------|-------|
| **Phase 1** | Auth + Family (create/join family, user profiles) |
| **Phase 2** | Meal Logging (log meals, view family meals) |
| **Phase 3** | Instagram Import (import food photos) |
| **Phase 4** | Favorites + Search (save favorite meals, search history) |
