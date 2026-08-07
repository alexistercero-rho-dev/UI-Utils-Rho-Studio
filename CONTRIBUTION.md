# Architecture & Contribution Guide: Rho Studio UI

## 1. Project Vision & Architecture
Rho Studio UI is a modern Android application built with **Jetpack Compose** and **MVVM** following a **Single-Activity Architecture**. 

To achieve enterprise-grade scalability, we strictly implement **Clean Architecture** principles. This ensures a clear separation of concerns, framework independence, and high testability.

---

## 2. Clean Architecture Layer Responsibilities
All contributions must respect the strict boundaries between the following three layers:

### 2.1 The UI Layer (`features/` & `ui/`)
*   **Role**: Handles user interaction and data presentation.
*   **Components**: 
    *   **Compose Screens/Components**: Purely declarative and stateless. They observe state and emit events.
    *   **ViewModels**: Act as a bridge. They manage UI state (Loading, Error, Toast) and handle user intent by calling Use Cases.
*   **Boundary Rule**: Never contains business logic. Never interacts directly with Repositories.

### 2.2 The Domain Layer (`core/domain/`)
*   **Role**: The "Heart" of the application. Contains the essential business rules.
*   **Components**: 
    *   **Use Cases (Interactors)**: Classes like `LoginUseCase.kt` that encapsulate a single, atomic business transaction.
    *   **Domain Models**: Pure data entities (e.g., `User.kt`) that are framework-independent.
*   **Boundary Rule**: **Pure Kotlin only**. Must not import `android.*` or depend on any external libraries/frameworks (except pure Kotlin ones). This layer is the "Single Source of Truth" for *logic*.

### 2.3 The Data Layer (`core/data/`)
*   **Role**: Manages data acquisition and persistence.
*   **Components**: 
    *   **Repositories**: Implementation of data fetching (API, Room, Preferences).
    *   **Managers**: State holders like `SessionManager.kt` that coordinate global app state.
*   **Boundary Rule**: Acts as the "Single Source of Truth" for *data state*. It implements the requirements defined by the Domain layer.

---

## 3. Core Requirements for Contributions

### 3.1 MVVM & UDF (Unidirectional Data Flow)
- **State flows down**: From ViewModel to Composables.
- **Events flow up**: From UI to ViewModel via lambdas.

### 3.2 Single-Activity & Reactive Navigation
- **MainActivity** is the sole navigation orchestrator.
- **ViewModels** and **Use Cases** must **never** hold a `NavController` or trigger navigation directly.
- **Logic**: Use Cases update the session/state in the Data layer. `MainActivity` observes this state and performs the transition (e.g., auto-routing to Login on session expiry).

---

## 4. Implementing New Features (Profile, Feed, Chat)

Every new feature should be built following the **Inside-Out** approach:

1.  **Inside (Domain)**: Create the `UseCase` (e.g., `UpdateProfileUseCase`, `GetFeedUseCase`, `SendMessageUseCase`).
    - Use the `Result<T>` wrapper for success/failure.
    - Write a Unit Test for the logic.
2.  **Middle (ViewModel)**: Create the bridge that transforms the Use Case `Result` into observable UI state.
3.  **Outside (UI)**: Build the stateless Compose UI.
    - **Feed UI**: Use `LazyColumn` for efficiency. Implement a stateless `PostItem.kt`.
    - **Chat UI**: Implement specialized "Message Bubble" components. Input fields must update the ViewModel state immediately.

---

## 5. Technical Constraints
- **Atomic Transactions**: Multi-step actions (e.g., validate -> save -> sync) must be managed as a single atomic unit within a `UseCase`.
- **Framework Independence**: Keep the Domain layer free of Android dependencies to support future Gradle modularization.
- **Standardized Results**: Always return `Result.Success`, `Result.Error`, or `Result.Loading` from Use Cases.

---
**[Rho.Studio®](https://rho.studio/) - Engineering Department** - Contact [alexis.tercero@rho.studio](mailto:alexis.tercero@rho.studio) 

