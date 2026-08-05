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
 * Date:         2026-08-04
 * ==============================================================================================
 * Description: Singleton orchestrator for authentication state and session lifecycle.
 *              Delegates persistence to a SessionRepository.kt and exposes reactive state
 *              via Kotlin Coroutines StateFlow for lifecycle-aware UI updates.
 * ==============================================================================================
 */
package com.rho.studio.ui.core.data.manager

import android.content.Context
import com.rho.studio.ui.core.domain.model.User
import com.rho.studio.ui.core.data.repository.SessionRepository
import com.rho.studio.ui.core.data.repository.SessionRepositoryImpl
import com.rho.studio.ui.core.domain.usecase.SessionManagerInterface
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * # SessionManager
 * Implementation of SessionManagerInterface and SSOT for session state.
 */
class SessionManager private constructor() : SessionManagerInterface {

    companion object {

        /** ensure that changes to the instance variable in a SessionManager are
         * immediately visible to all threads, preventing issues caused
         * by thread-local caching.*/
        @Volatile
        private var instance: SessionManager? = null

        /** Thread-safe singleton instance getter.*/
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

        fun init(repository: SessionRepository) {
            getInstance().initialize(repository)
        }
    }

    // ==================== PROPERTIES ====================

    /**Instead of blocking the UI thread when saving
     * to disk or simulating a network call,
     * it uses a dedicated sessionScope*/
    private val sessionScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var repository: SessionRepository
    private var isInitialized = false

    /** # Private MutableStateFlow
     * Only the SessionManager can modify the state*/
    private val _isAuthenticated = MutableStateFlow<Boolean>(false)
    private val _currentUser = MutableStateFlow<User?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _isSessionChecked = MutableStateFlow(false)

    /** # PUBLIC StateFlow
     * The rest of the app (UI/ViewModels) can only observe the state.*/
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val error: StateFlow<String?> = _error.asStateFlow()
    val isSessionChecked: StateFlow<Boolean> = _isSessionChecked.asStateFlow()

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

    override fun updateSession(user: User) {
        checkInitialized()
        _currentUser.value = user
        _isAuthenticated.value = true
        saveUserSession(user)
    }

    override fun clearSession() {
        checkInitialized()
        _currentUser.value = null
        _isAuthenticated.value = false
        clearUserSession()
    }

    override fun extractNameFromEmail(email: String): String {
        return email.substringBefore("@")
            .split(".", "_", "-")
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
    }

    private fun loadSavedSession() {
        sessionScope.launch {
            try {
                val user = repository.getUser() // persistence
                if (user != null) {
                    _currentUser.value = user
                    _isAuthenticated.value = true
                } else {
                    _isAuthenticated.value = false
                }
            } catch (e: Exception) {
                _isAuthenticated.value = false
                clearUserSession()
            } finally {
                _isSessionChecked.value = true
            }
        }
    }

    private fun saveUserSession(user: User) {
        sessionScope.launch {
            try {
                repository.saveUser(user)
            } catch (e: Exception) {
            }
        }
    }

    private fun clearUserSession() {
        sessionScope.launch {
            try {
                repository.clearSession()
            } catch (e: Exception) {
            }
        }
    }

    fun isAuthenticatedSync(): Boolean = _isAuthenticated.value
    fun getCurrentUserSync(): User? = _currentUser.value
    fun clearError() { _error.value = null }
    fun cleanup() { sessionScope.cancel() }
}
