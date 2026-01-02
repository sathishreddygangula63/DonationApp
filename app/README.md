# 📍  Donation App

The **Donation App** is a mobile application designed to connect donors, non-governmental organizations (NGOs), and people in need through location-based services. The app enables users to discover nearby donation drives, contribute funds, donate essential items, and request pickups seamlessly, ensuring transparency and efficiency in charitable activities.

---

## 📌 Key Features

### 🔹 Campaign Management
- View live donation campaigns
- Supports **Fund Donation Campaigns** and **Item Donation Campaigns**
- Category-based campaign filtering
- Campaign progress tracking (for fund campaigns)

### 🔹 Fund Donations
- Donate funds securely to campaigns
- Real-time update of raised amount
- Donation history with receipt-style UI
- Donations saved under user email for easy retrieval

### 🔹 Item Donations & Pickup Requests
- Submit pickup requests for item donation campaigns
- Enter items to donate
- Upload item images (via ImgBB integration)
- Select pickup location using Google Maps
- Automatic address detection from map location
- Track pickup request status (Pending / Approved / Completed)

### 🔹 Location-Based Features
- Locate nearby donation centers using Google Maps
- View donation center details in a bottom sheet
- Call donation centers directly from the app
- Mark donation centers as **Favorites**
- Toggle between **All Centers** and **Favorite Centers**

### 🔹 User History
- Tab-based history screen
- Separate tabs for:
    - Fund Donations
    - Pickup Requests
- Clean and premium UI for history records

---

## 🏗️ Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Backend:** Firebase Realtime Database
- **Maps:** Google Maps SDK + Compose
- **Image Upload:** ImgBB API
- **Architecture:** MVVM (recommended structure)

---

## 🚀 How to Run the Project

1. Clone the repository
2. Open the project in **Android Studio**
3. Add your:
    - Firebase configuration (`google-services.json`)
    - Google Maps API Key
    - ImgBB API Key
4. Sync Gradle and run the app on an emulator or real device

---

