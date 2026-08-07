# Technical Report: Rho Studio UI Architecture
## Modern Android Development with Jetpack Compose & MVVM
This report outlines the architecture design of the Rho Studio UI application.

<img width="480" height="940" alt="Image" src="https://github.com/user-attachments/assets/3ad79c42-4806-4dcd-8bb0-920b2587a86d" />

## Contributions

Must follow the next requirements [CONTRIBUTION.md](CONTRIBUTION.md)

---

## 1. Executive Summary
The application is a pure **Jetpack Compose** implementation following a **Single-Activity Architecture**. It leverages a reactive **MVVM (Model-View-ViewModel)** pattern to ensure a clean separation of concerns, testability, and a fluid user experience driven by Unidirectional Data Flow (UDF).

---

## 2. Integrated Architectural Perspective
The project utilizes a **Feature-Layered Architecture**. Each feature is encapsulated within its own package, maintaining a clean internal separation between UI (Compose) and Logic (ViewModels), while sharing a common Core/Data foundation.

### 2.1 UI & Feature Layers (View)
The UI is composed of stateless screens and modular components that observe state from their respective ViewModels.

- **`MainActivity.kt`**: The application's core orchestrator. Manages the high-level `NavHost`, coordinates the global `LoadingOverlay`, and synchronizes navigation via `SessionManager`.
- **Authentication Feature (`features/auth/`)**:
    - `LoginScreen.kt`: The main entry point for user authentication.
    - `LoginEmailField.kt` / `LoginPasswordField.kt`: Specialized inputs with built-in validation and security logic.
- **Home & Dashboard Feature (`features/home/`)**:
    - `HomeScreen.kt`: The primary post-auth landing page.
    - `ServiceList.kt` / `ServiceItem.kt`: Adaptive components for dynamic content delivery.
- **Common UI Feature (`features/common/`)**:
    - `PageHeader.kt` / `PageFooter.kt`: Shared layouts that provide global context and actions (e.g., Logout).

### 2.2 Business Logic & State Layer (ViewModel)
ViewModels act as the bridge between features and the data layer, handling user intent and reactive state.

- **`BaseViewModel.kt`**: The architectural anchor providing unified loading states, toast messaging, and standardized error handling.
- **`LoginViewModel.kt`**: Manages complex form state and **debounced validation** logic.
- **`HomeViewModel.kt`**: Orchestrates dashboard content lifecycle and session termination.
- **`HeaderViewModel.kt`**: Bridges the `SessionManager` state to common UI components like the `PageHeader`.

### 2.3 Core Data & Infrastructure Layer
Provides the essential services and "Single Source of Truth" for the entire application.

- **`SessionManager.kt`**: A singleton coordinator for the application's global authentication state and user profile.
- **`SessionRepository.kt`**: Manages persistent storage and retrieval of session tokens and user data.
- **`Credentials.kt` / `User.kt` / `ServiceModule.kt`**: Strongly typed data models that enforce business rules and schema consistency.

---

## 3. Core Technical Implementations

### 3.1 State-Driven Reactive Navigation
Navigation is decoupled from direct user input. `MainActivity.kt` observes the `isAuthenticated` state from `SessionManager.kt`. When this state changes, a `LaunchedEffect` executes the transition, ensuring the UI is always a reflection of the underlying session state.

### 3.2 Performance Optimized Validation
To ensure a smooth typing experience, `LoginViewModel.kt` utilizes **Coroutine Debouncing**. Input validation is deferred until the user pauses for 300ms, minimizing unnecessary UI updates and logic execution.

### 3.3 Centralized Design System
Managed in `ui/theme/`, the app uses a custom Material 3 implementation. This ensures brand consistency (`RhoRed`, `RhoStrongGray`) is automatically applied to all features through a unified `Theme.kt` and `Color.kt` definition.

---

## 4. File Registry & Responsibilities

| File | Feature | Primary Engineering Responsibility |
| :--- | :--- | :--- |
| `MainActivity.kt` | App Root | Global orchestration, NavHost, and session-based routing. |
| `BaseViewModel.kt` | Core | Shared architectural logic for Loading/Error states. |
| `SessionManager.kt` | Core | Centralized authentication and session lifecycle management. |
| `LoginViewModel.kt` | Auth | Form state management and debounced validation. |
| `HomeScreen.kt` | Home | Root layout for the post-authentication dashboard. |
| `ServiceList.kt` | Home | Efficient grid implementation for platform modules. |
| `Credentials.kt` | Core | Logic-heavy model for credential validation rules. |
| `Theme.kt` | Design | Global Material 3 theme configuration and brand mapping. |

---

## 5. Path to Enterprise-Grade Architecture

To transition this foundation into a highly scalable, enterprise-grade application, the following architectural advancements are planned to manage complex business flows and transactional integrity.

### 5.1 Domain Layer & Use Case Implementation
As business logic complexity grows, direct ViewModel-to-Repository interaction is being transitioned to a dedicated **Domain Layer**.
- **Use Cases (Interactors)**: Classes like `LoginUseCase.kt` (`core/domain/usecase/LoginUseCase.kt`) encapsulate specific business rules, making the logic reusable across different ViewModels and testable in isolation.
- **Business Transaction Flow**: A single user action (e.g., "Login") may involve multiple steps: credential validation -> token acquisition -> user profile synchronization. These are managed as atomic transactions within the Domain Layer.
- **Best Practice**: [Android Guide to the Domain Layer](https://developer.android.com/topic/architecture/domain-layer)

### 5.2 Advanced Data Flow & Synchronization
Enterprise apps require robust data handling beyond simple memory state.
- **Repository Pattern**: Refined `SessionRepository.kt` and future repositories will implement a **Single Source of Truth (SSOT)** strategy, coordinating between local storage (Room) and remote APIs (Retrofit).
- **Reactive Stream Transactions**: Utilizing **Kotlin Flow** for end-to-end reactive streams. Transactions are modeled as immutable states flowing from the Data Layer to the UI.
- **Best Practice**: [Data Layer with Repositories](https://developer.android.com/topic/architecture/data-layer)

### 5.3 Scalability & Reliability Standards
- **Dependency Injection (Hilt)**: Moving from manual singleton management to **Dagger Hilt** for better decoupling and automated lifecycle management.
- **Modularization**: Splitting the current feature packages into independent Gradle modules (`:feature:auth`, `:feature:home`, `:core:data`) to improve build times and enforce strict visibility boundaries.
- **Best Practice**: [Guide to App Modularization](https://developer.android.com/topic/modularization)

---

## 6. References & Standards
- **MAD (Modern Android Development)**: Adhering to official [Android Architecture Guidelines](https://developer.android.com/topic/architecture).
- **Jetpack Compose Best Practices**: Following [UDF (Unidirectional Data Flow)](https://developer.android.com/jetpack/compose/architecture#udf) principles for state management.
- **Clean Architecture**: Implementing principles from Robert C. Martin to maintain a high degree of testability and independence from external libraries. [Clean Architecture Reference](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

---
**[Rho.Studio®](https://rho.studio/) - Engineering Department** - Contact [alexis.tercero@rho.studio](mailto:alexis.tercero@rho.studio) 
