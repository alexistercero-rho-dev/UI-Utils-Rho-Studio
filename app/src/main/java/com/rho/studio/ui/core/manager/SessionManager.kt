/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         SessionManager.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-02-23
 * ==============================================================================================
 * Description:
 * ==============================================================================================
 */
package com.rho.studio.ui.core.manager

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rho.studio.ui.core.model.User
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.*
import java.lang.ref.WeakReference
/**
 * SessionManager - Singleton managing authentication state and session persistence.
 * Core module - Used by all features
 *
 * This component acts as the "Single Source of Truth" for the application's global
 * authentication state. It manages:
 * 1. **Persistence:** Automatically saves and restores [User] data using SharedPreferences and Gson.
 * 2. **Observability:** Exposes [LiveData] streams for UI components to react to login/logout events.
 * 3. **Concurrency:** Handles network simulations and I/O operations using a dedicated [CoroutineScope].
 *
 * ### Initialization
 * The manager must be initialized before use, typically in your `Application` class or
 * the entry `Activity`:
 * ```kotlin
 * SessionManager.init(context)
 * ```
 *
 * ### Usage
 * Access the manager via [getInstance] to observe states or perform actions:
 * ```kotlin
 * SessionManager.getInstance().isAuthenticated.observe(this) { isLoggedIn ->
 *     // Update UI accordingly
 * }
 * ```
 */
class SessionManager private constructor() {

    // ==================== COMPANION OBJECT ====================

    companion object {
        @Volatile
        private var instance: SessionManager? = null
        private const val PREF_KEY_USER = "pref_current_user"
        private const val PREF_KEY_AUTH_STATE = "pref_auth_state"

        /**
         * Thread-safe singleton instance getter
         * Uses double-checked locking pattern
         */
        fun getInstance(): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager().also { instance = it }
            }
        }

        /**
         * Initialize the session manager with application context
         * Must be called from Application class or MainActivity
         */
        fun init(context: Context) {
            getInstance().initialize(context)
        }
    }

    // ==================== PROPERTIES ====================

    private lateinit var preferences: SharedPreferences
    private lateinit var gson: Gson
    private var applicationContextRef: WeakReference<Context>? = null

    // Coroutine scope for background operations
    private val sessionScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Private mutable LiveData for internal updates
    private val _isAuthenticated = MutableLiveData<Boolean>(false)
    private val _currentUser = MutableLiveData<User?>()
    private val _isLoading = MutableLiveData(false)
    private val _error = MutableLiveData<String?>()

    // ==================== PUBLIC LIVEDATA ====================

    /**
     * Authentication state - observe for UI updates
     * True when user is logged in, false otherwise
     */
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated

    /**
     * Current user - observe for user data updates
     * Null when no user is logged in
     */
    val currentUser: LiveData<User?> = _currentUser

    /**
     * Loading state - useful for showing progress indicators
     */
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Error messages - observe for error handling
     */
    val error: LiveData<String?> = _error

    // ==================== INITIALIZATION ====================

    /**
     * Initialize the session manager
     * Called once from Application or MainActivity
     */
    private fun initialize(context: Context) {
        applicationContextRef = WeakReference(context.applicationContext)
        preferences = context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)
        gson = Gson()
        loadSavedSession()
    }

    // ==================== PUBLIC METHODS ====================

    /**
     * Authenticate user with credentials (async)
     * In real app, this would make an API call
     */
    fun login(email: String, password: String): Job {
        _isLoading.postValue(true)
        _error.postValue(null)

        return sessionScope.launch {
            try {
                // Simulate network delay
                delay(1500)

                // Validate credentials (in real app, this would be an API call)
                if (isValidCredentials(email, password)) {
                    val user = User(
                        id = generateUserId(),
                        email = email,
                        name = extractNameFromEmail(email)
                    )

                    withContext(Dispatchers.Main) {
                        _currentUser.value = user
                        _isAuthenticated.value = true
                        saveUserSession(user)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _error.value = "Invalid email or password"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _error.value = "Login failed: ${e.message}"
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
        }
    }

    /**
     * Synchronous login for testing or simple flows
     */
    fun loginSync(user: User) {
        _currentUser.value = user
        _isAuthenticated.value = true
        saveUserSession(user)
    }

    /**
     * Logout user (async)
     */
    fun logout(): Job {
        _isLoading.postValue(true)

        return sessionScope.launch {
            try {
                // Clear session data
                withContext(Dispatchers.Main) {
                    _currentUser.value = null
                    _isAuthenticated.value = false
                }
                clearUserSession()
            } finally {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
        }
    }

    /**
     * Synchronous logout
     */
    fun logoutSync() {
        _currentUser.value = null
        _isAuthenticated.value = false
        clearUserSession()
    }

    /**
     * Check if user is authenticated (synchronous)
     */
    fun isAuthenticatedSync(): Boolean = _isAuthenticated.value ?: false

    /**
     * Get current user (synchronous)
     */
    fun getCurrentUserSync(): User? = _currentUser.value

    /**
     * Clear any error messages
     */
    fun clearError() {
        _error.value = null
    }

    // ==================== PRIVATE METHODS ====================

    /**
     * Validate credentials (mock implementation)
     * In real app, this would be an API call
     */
    private suspend fun isValidCredentials(email: String, password: String): Boolean {
        // Simulate validation
        return email.isNotBlank() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                password.length >= 6
    }

    /**
     * Generate a unique user ID
     */
    private fun generateUserId(): String = "user_${System.currentTimeMillis()}"

    /**
     * Extract display name from email
     */
    private fun extractNameFromEmail(email: String): String {
        return email.substringBefore("@")
            .split(".", "_", "-")
            .joinToString(" ") {
                it.replaceFirstChar { char -> char.uppercase() }
            }
    }

    /**
     * Load saved session from SharedPreferences
     */
    private fun loadSavedSession() {
        sessionScope.launch {
            try {
                val userJson = preferences.getString(PREF_KEY_USER, null)
                if (userJson != null) {
                    val user = gson.fromJson(userJson, User::class.java)
                    withContext(Dispatchers.Main) {
                        _currentUser.value = user
                        _isAuthenticated.value = true
                    }
                }
            } catch (e: JsonSyntaxException) {
                // Corrupted data - clear it
                clearUserSession()
            }
        }
    }

    /**
     * Save user session to SharedPreferences
     */
    private fun saveUserSession(user: User) {
        sessionScope.launch {
            try {
                val userJson = gson.toJson(user)
                preferences.edit().putString(PREF_KEY_USER, userJson).apply()
            } catch (e: Exception) {
                // Log error but don't interrupt user experience
                e.printStackTrace()
            }
        }
    }

    /**
     * Clear saved session from SharedPreferences
     */
    private fun clearUserSession() {
        sessionScope.launch {
            preferences.edit().clear().apply()
        }
    }

    /**
     * Clean up resources when manager is no longer needed
     */
    fun cleanup() {
        sessionScope.cancel()
        applicationContextRef?.clear()
        applicationContextRef = null
    }
}