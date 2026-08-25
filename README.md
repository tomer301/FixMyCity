# FixMyCity 🏙️
**A community-driven Android application for real-time reporting, transparency, and prioritization of urban hazards.**

---

## 📌 About FixMyCity
**FixMyCity** is a location-based social/community application that empowers citizens to report municipal hazards in real time, track their resolution status, and support existing reports through community upvotes (**Upvotes**).

Unlike traditional municipal 106 hotlines, FixMyCity emphasizes public transparency, active civic engagement, and real-time synchronization across all users.

---

## 🚀 Key Features

- 🔐 **Secure Authentication:** Full user lifecycle management (Login & Registration) powered by **Firebase Authentication**.
- 📋 **Real-Time Hazards Feed:** Live updating feed driven by **Firebase Firestore Snapshot Listeners**, sorted chronologically from newest to oldest.
- 📸 **Hazard Reporting:** Upload photos directly from the device gallery to **Firebase Storage**, select precise locations (City, Neighborhood, Category, and optional Address), and provide detailed hazard descriptions.
- 👍 **Community Upvoting System:**
    - Real-time vote toggling (Upvote / Downvote).
    - **Atomic Security (Transactions):** Powered by Firestore's `runTransaction` to prevent race conditions and negative vote counts.
    - **Self-Upvote Prevention:** Business logic prevents users from upvoting their own reported hazards while providing friendly UX feedback.
- 🏙️ **Dynamic Geographic Filtering:** Fast filtering by City and Neighborhood using robust index-based (`Int`) state management to prevent text-mismatch bugs.
- 🚪 **Secure Session & Logout:** Complete activity backstack clearing (`FLAG_ACTIVITY_NEW_TASK` | `FLAG_ACTIVITY_CLEAR_TASK`) on logout to ensure secure session termination.
- 🎨 **Enhanced User Experience (UX):** Dynamic empty-state views ("No reports found in this area") and centralized Toast management to prevent notification queues.

---

## 🏗️ Architecture & Best Practices

The application is engineered adhering to **Clean Architecture** and **SOLID** principles:

- **Repository Pattern (`ReportRepository.kt`):** Complete decoupling between UI activities and the data/backend layer. Activities remain unaware of raw Firestore queries or Firebase Storage SDK calls.
- **Singleton Design Pattern (`SignalManager.kt`):** A thread-safe Singleton initialized at application startup (`App.kt`) providing centralized notification management without memory leaks.
- **View Binding:** Type-safe and null-safe view access across all activities and view holders.
- **RecyclerView & Custom Adapter (`ReportsAdapter.kt`):** Efficient list rendering using CardViews with dynamic image tinting (`ImageViewCompat`) for upvote states.
- **Glide Integration:** Asynchronous and cached image loading from Firebase Storage URLs.
- **Input Validation (`ReportValidator.kt`):** Comprehensive client-side checks for mandatory fields and image selection prior to network calls.

---

## 🛠️ Setup & Installation

Since this project uses Firebase and sensitive configuration files are excluded for security, follow these steps to run the app:

1. **Create a Firebase Project:** Go to the [Firebase Console](https://console.firebase.google.com/).
2. **Add an Android App:** Use the package name `com.example.fixmycity`.
3. **Download Configuration:** Download the `google-services.json` file provided by Firebase.
4. **Place the File:** Copy the `google-services.json` file into the `app/` directory of this project.
5. **Build & Run:** Sync Gradle and you're ready to go!

> [!IMPORTANT]
> Make sure your Firebase project has **Authentication**, **Cloud Firestore**, and **Cloud Storage** enabled.

---

## 🛠️ Tech Stack & Libraries

| Layer / Domain            | Technology / Library                                                    |
|:--------------------------|:------------------------------------------------------------------------|
| **Language**              | Kotlin (100%)                                                           |
| **UI Framework**          | Material Components, ConstraintLayout, RecyclerView, CardView           |
| **Backend & Cloud**       | Firebase Auth, Cloud Firestore, Firebase Storage                        |
| **Image Loading**         | Glide                                                                   |
| **Architecture Patterns** | Clean Architecture, Repository Pattern, Singleton Pattern, View Binding |

---

## 📁 Project Structure

```text
com.example.fixmycity
│
├── App.kt                      // Application class for central initialization
├── MainActivity.kt             // Main feed, filtering, and session management
├── AddReportActivity.kt        // Hazard creation screen
│
├── auth/                       // User authentication module
│   ├── LoginActivity.kt        // Authentication entry point
│   └── RegisterActivity.kt     // User registration screen
│
├── models/
│   └── HazardReport.kt         // Data class representing a hazard report
│
├── repository/
│   └── ReportRepository.kt     // Centralized data access layer for Firestore & Storage
│
├── adapters/
│   └── ReportsAdapter.kt       // Custom RecyclerView adapter with Upvote UI logic
│
└── utils/
    ├── SignalManager.kt         // Thread-safe Singleton Toast manager
    ├── CityNeighborhoodHelper.kt// Helper for dynamic XML array loading
    ├── ReportValidator.kt       // Input validation helper (Reports)
    ├── AuthValidator.kt         // Authentication validation logic
    ├── NavigateUtils.kt         // Navigation helpers and Extensions
    └── Constants.kt             // Application constants and Firebase collection keys