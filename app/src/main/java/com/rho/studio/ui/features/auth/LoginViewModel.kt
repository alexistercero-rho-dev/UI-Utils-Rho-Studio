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
 * Date:         2026-07-16
 * ============================================================================
 * Description:
 *      The LoginViewModel manages the state and business logic for the
 *      Authentication screen, leveraging the Rho Studio BaseViewModel architecture.
 *      It handles user input validation, manages asynchronous login requests
 *      via SessionManager, and exposes reactive UI states using LiveData.
 *
 *      •Extends: com.rho.studio.ui.core.base.BaseViewModel
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
 *                  resetForm() provides a clean slate for the UI.
 * ============================================================================
 */
package com.rho.studio.ui.features.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rho.studio.ui.core.base.BaseViewModel
import com.rho.studio.ui.core.manager.SessionManager
import com.rho.studio.ui.core.model.Credentials
import kotlinx.coroutines.Job

class LoginViewModel : BaseViewModel() {

    // ==================== DEPENDENCIES ====================

    private val sessionManager = SessionManager.getInstance()
    private var loginJob: Job? = null

    // ==================== FORM STATE ====================

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