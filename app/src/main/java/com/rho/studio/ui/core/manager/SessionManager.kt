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
 * Date:         2026-07-14
 * ==============================================================================================
 * Description: Singleton orchestrator for authentication state and session lifecycle.
 *              Delegates persistence to a SessionRepository
 *              and exposes reactive state via LiveData.
 * ==============================================================================================
 */
package com.rho.studio.ui.core.manager

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rho.studio.ui.core.model.User
import com.rho.studio.ui.core.repository.SessionRepository
import com.rho.studio.ui.core.repository.SessionRepositoryImpl
import kotlinx.coroutines.*

/**
 * # SessionManager
 * Core module - Singleton orchestrator for authentication state and session lifecycle.
 * Delegates persistence to a SessionRepository and exposes reactive state via LiveData.
 */
class SessionManager private constructor() {

    companion object {
        private const val TAG = "SessionManager"

        /** ensure that changes to the instance variable in a SessionManager are
         * immediately visible to all threads, preventing issues caused
         * by thread-local caching.*/
        @Volatile
        private var instance: SessionManager? = null

        /**
         * Thread-safe singleton instance getter.
         */
        fun getInstance(): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager().also { instance = it }
            }
        }

        /**
         * Initialize the session manager with application context.
         * Should be called in Application.onCreate() or MainActivity.onCreate().
         */
        fun init(context: Context) {
            getInstance().initialize(SessionRepositoryImpl(context))
        }
    }

    // ==================== PROPERTIES ====================

    /**Instead of blocking the UI thread when saving
     * to disk or simulating a network call,
     * it uses a dedicated sessionScope*/
    private val sessionScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var repository: SessionRepository
    private var isInitialized = false

    /** # Private mutable LiveData
     * Only the SessionManager can modify the state*/
    private val _isAuthenticated = MutableLiveData<Boolean>(false)
    private val _currentUser = MutableLiveData<User?>(null)
    private val _isLoading = MutableLiveData(false)
    private val _error = MutableLiveData<String?>(null)

    /** # PUBLIC LIVEDATA
     * The rest of the app (UI/ViewModels) can only observe the state.*/
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated
    val currentUser: LiveData<User?> = _currentUser
    val isLoading: LiveData<Boolean> = _isLoading
    val error: LiveData<String?> = _error

    // ==================== INITIALIZATION ====================

    private fun initialize(repository: SessionRepository) {
        if (isInitialized) return

        this.repository = repository
        isInitialized = true
        loadSavedSession()
    }

    private fun checkInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("SessionManager must be initialized with init(context) before use.")
        }
    }

    // ==================== PUBLIC METHODS ====================

    /**
     * Authenticate user with credentials (mock implementation).
     */
    fun login(email: String, password: String): Job {
        checkInitialized()
        _isLoading.postValue(true)
        _error.postValue(null)

        return sessionScope.launch {
            try {
                delay(1500) // Simulate network

                if (isValidCredentials(email, password)) {
                    val user = User(
                        id = "user_${System.currentTimeMillis()}",
                        email = email,
                        name = extractNameFromEmail(email)
                    )

                    saveUserSession(user)
                    
                    withContext(Dispatchers.Main) {
                        _currentUser.value = user
                        _isAuthenticated.value = true
                    }
                } else {
                    _error.postValue("Invalid email or password")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login error", e)
                _error.postValue("Login failed: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun logout(): Job {
        checkInitialized()
        _isLoading.postValue(true)

        return sessionScope.launch {
            try {
                clearUserSession()
                withContext(Dispatchers.Main) {
                    _currentUser.value = null
                    _isAuthenticated.value = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Logout error", e)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun isAuthenticatedSync(): Boolean = _isAuthenticated.value ?: false

    fun getCurrentUserSync(): User? = _currentUser.value

    fun clearError() {
        _error.value = null
    }

    // ==================== PRIVATE HELPERS ====================

    private fun isValidCredentials(email: String, password: String): Boolean {
        return email.isNotBlank() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                password.length >= 6
    }

    private fun extractNameFromEmail(email: String): String {
        return email.substringBefore("@")
            .split(".", "_", "-")
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
    }

    private fun loadSavedSession() {
        sessionScope.launch {
            try {
                val user = repository.getUser()
                user?.let {
                    withContext(Dispatchers.Main) {
                        _currentUser.value = it
                        _isAuthenticated.value = true
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load saved user", e)
                clearUserSession()
            }
        }
    }

    private fun saveUserSession(user: User) {
        sessionScope.launch {
            try {
                repository.saveUser(user)
            } catch (e: Exception) {
                Log.e(TAG, "Error saving session", e)
            }
        }
    }

    private fun clearUserSession() {
        sessionScope.launch {
            try {
                repository.clearSession()
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing session", e)
            }
        }
    }

    fun cleanup() {
        sessionScope.cancel()
    }
}
