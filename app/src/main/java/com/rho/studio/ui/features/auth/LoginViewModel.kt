/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ============================================================================
 * File:         LoginViewModel.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-02-22
 * ============================================================================
 * Description:
 *      The LoginViewModel manages the state and
 *      business logic for the Authentication screen.
 *      It handles user input validation, manages asynchronous login requests
 *      via SessionManager, and exposes reactive UI states using LiveData.
 *
 *      •Extends: androidx.lifecycle.ViewModel
 *      •Dependencies:
 *          •SessionManager: Singleton handling network/local session state.
 *          •Credentials: A data model encapsulating email and password logic.
 *
 *      Core Logic Flows
 *          Real-time Validation
 *              •onEmailChanged() / onPasswordChanged():
 *                  Triggered on every keystroke.
 *              •Updates the credentials model and
 *                  immediately evaluates validation rules
 *                  (blank checks, email regex, password length).
 *          Authentication Process
 *              Trigger: onLoginClick() validates the form one final time.
 *              Execution: performLogin() sets isLoading = true
 *                         and launches a coroutine.
 *              Network: Calls sessionManager.login().
 *              Result Handling:
 *                  •Success: Sets success toast and relies
 *                            on SessionManager state for navigation.
 *                  •Failure: Catches exceptions or handles auth errors,
 *                            updating the error LiveData.
*               Cleanup: Resets isLoading in the finally block.
 *          Lifecycle & Memory Management
 *              •Job Cancellation:
 *                  The loginJob is manually cancelled if a new login
 *                  attempt starts or when the ViewModel is destroyed
 *                  (onCleared) to prevent memory leaks and redundant
 *                  network callbacks.
 *              •State Reset:
 *                  resetForm() provides a clean slate for the UI
 *                  (useful for logout scenarios or clearing sensitive data).
 * ============================================================================
 */
package com.rho.studio.ui.features.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rho.studio.ui.core.manager.SessionManager
import com.rho.studio.ui.core.model.Credentials
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

class LoginViewModel : ViewModel() {

    // ==================== DEPENDENCIES ====================

    private val sessionManager = SessionManager.getInstance()
    private var loginJob: Job? = null

    // ==================== FORM STATE ====================

    val credentials = Credentials()

    // ==================== UI STATE ====================

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    private val _isFormValid = MutableLiveData(false)
    val isFormValid: LiveData<Boolean> = _isFormValid

    // ==================== FORM VALIDATION ====================

    fun onEmailChanged(email: String) {
        credentials.email = email
        validateEmail()
        validateForm()
    }

    fun onPasswordChanged(password: String) {
        credentials.password = password
        validatePassword()
        validateForm()
    }

    private fun validateEmail() {
        _emailError.value = when {
            credentials.email.isBlank() -> "Email is required"
            !credentials.isEmailValid -> "Please enter a valid email address"
            else -> null
        }
    }

    private fun validatePassword() {
        _passwordError.value = when {
            credentials.password.isBlank() -> "Password is required"
            !credentials.isPasswordValid -> "Password must be at least 6 characters"
            else -> null
        }
    }

    private fun validateForm() {
        _isFormValid.value = credentials.isValid
    }

    // ==================== ACTIONS ====================

    fun onLoginClick() {
        loginJob?.cancel()

        if (!credentials.isValid) {
            validateEmail()
            validatePassword()
            _toastMessage.value = "Please fix the errors above"
            return
        }

        performLogin()
    }

    private fun performLogin() {
        _isLoading.value = true
        _error.value = null

        loginJob = viewModelScope.launch {
            try {
                sessionManager.login(credentials.email, credentials.password).join()

                // Check authentication state after login
                if (sessionManager.isAuthenticatedSync()) {
                    _toastMessage.value = "Login successful!"
                } else {
                    _error.value = sessionManager.error.value ?: "Login failed"
                }
            } catch (e: Exception) {
                _error.value = "Login failed: ${e.message}"
                _toastMessage.value = "Login failed. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ==================== UTILITY METHODS ====================

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun clearError() {
        _error.value = null
    }

    fun resetForm() {
        credentials.clear()
        _emailError.value = null
        _passwordError.value = null
        _isFormValid.value = false
        _error.value = null
        _toastMessage.value = null
    }

    // ==================== LIFECYCLE ====================

    override fun onCleared() {
        super.onCleared()
        loginJob?.cancel()
    }
}