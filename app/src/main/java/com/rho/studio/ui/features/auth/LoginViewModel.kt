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
 * Date:         2026-07-29
 * ============================================================================
 * Description:
 *      The LoginViewModel manages the state and business logic for the
 *      Authentication screen in a pure Jetpack Compose environment.
 *      It leverages the Rho Studio BaseViewModel architecture to handle 
 *      user input validation, asynchronous login requests via SessionManager, 
 *      and reactive UI states.
 *
 *      •Extends: com.rho.studio.ui.core.base.BaseViewModel
 *      •Dependencies:
 *          •SessionManager: Singleton handling network/local session state.
 *          •Credentials: A data model encapsulating email and password logic.
 *
 *      Core Logic Flows
 *          State-Driven Input
 *              •email / password: Uses Compose `mutableStateOf` to provide
 *                  immediate, observable reactivity for the UI layer.
 *          Real-time & Debounced Validation
 *              •onEmailChanged() / onPasswordChanged():
 *                  Triggered on every keystroke, updating the state immediately.
 *              •300ms Debounce: Logic moved from Fragments to the ViewModel,
 *                  ensuring validation is only performed after the user pauses typing.
 *          Authentication Process
 *              •Trigger: onLoginClick() performs final validation and guards
 *                         against concurrent attempts using the base loading state.
 *              •Execution: performLogin() utilizes launchWithLoading() to
 *                         automatically manage the UI loading state and error trapping.
 *              •Network: Calls sessionManager.login().
 *              •Result Handling:
 *                  •Success: Sets success toast and relies on SessionManager state.
 *                  •Failure: Customizes error messages via the handleError() hook.
 *          Lifecycle & Architecture
 *              •Job Management:
 *                  Relies on BaseViewModel's automated job tracking and cleanup
 *                  to prevent memory leaks without manual cancellation logic.
 *              •State Reset:
 *                  resetForm() provides a clean, secure slate for the UI by
 *                  clearing Compose states and the underlying model.
 * ============================================================================
 */
package com.rho.studio.ui.features.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rho.studio.ui.core.base.BaseViewModel
import com.rho.studio.ui.core.manager.SessionManager
import com.rho.studio.ui.core.model.Credentials
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel : BaseViewModel() {
    // ==================== DEPENDENCIES ====================
    private val sessionManager = SessionManager.getInstance()
    private var loginJob: Job? = null
    private var emailDebounceJob: Job? = null
    private var passwordDebounceJob: Job? = null
    // ==================== FORM STATE ====================
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    val credentials = Credentials()
    // ==================== UI STATE ====================
    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError
    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError
    private val _isFormValid = MutableLiveData(false)
    val isFormValid: LiveData<Boolean> = _isFormValid
    // ==================== FORM VALIDATION ====================
    fun onEmailChanged(email: String) {
        this.email = email
        credentials.email = email
        
        emailDebounceJob?.cancel()
        emailDebounceJob = viewModelScope.launch {
            delay(300)
            validateEmail()
            validateForm()
        }
    }

    fun onPasswordChanged(password: String) {
        this.password = password
        credentials.password = password
        
        passwordDebounceJob?.cancel()
        passwordDebounceJob = viewModelScope.launch {
            delay(300)
            validatePassword()
            validateForm()
        }
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
        // Guard against multiple concurrent login attempts
        if (isLoading.value == true) return

        if (!credentials.isValid) {
            validateEmail()
            validatePassword()
            showToast("Please fix the errors above")
            return
        }

        performLogin()
    }

    private fun performLogin() {
        loginJob = launchWithLoading(
            block = {
                // Delegate authentication to the session manager
                sessionManager.login(credentials.email, credentials.password).join()

                // Evaluate the authentication result
                if (sessionManager.isAuthenticatedSync()) {
                    showToast("Login successful!")
                    clearError()
                } else {
                    // Capture and handle the specific error from SessionManager
                    val errorMsg = sessionManager.error.value ?: "Authentication failed"
                    handleError(Exception(errorMsg))
                }
            },
            onError = {
                // General fallback if the login process crashes
                showToast("Login process encountered an error. Please try again.")
            }
        )
    }

    // ==================== UTILITY METHODS ====================
    fun resetForm() {
        email = ""
        password = ""
        credentials.clear()
        _emailError.value = null
        _passwordError.value = null
        _isFormValid.value = false
        clearError()
        clearToastMessage()
    }

    override fun handleError(e: Exception) {
        // Ensure error messages are properly prefixed for context
        val message = e.message ?: "An unknown error occurred"
        val formattedMessage = message.takeIf { it.startsWith("Login failed") }
            ?: "Login failed: $message"

        super.handleError(Exception(formattedMessage, e.cause))
    }
}