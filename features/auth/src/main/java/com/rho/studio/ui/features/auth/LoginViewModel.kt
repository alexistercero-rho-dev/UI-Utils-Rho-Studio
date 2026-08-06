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
 * Date:         2026-08-06
 * ============================================================================
 * Description:
 *      The LoginViewModel manages the state and business logic for the
 *      Authentication screen, acting as the UI Layer in a pure
 *      Jetpack Compose environment.
 *      It leverages the Rho Studio BaseViewModel architecture to handle
 *      user input validation, asynchronous login requests via LoginUseCase,
 *      and reactive UI states using both Compose State and StateFlow.
 *
 *      •Extends: com.rho.studio.ui.core.ui.base.BaseViewModel
 *      •Dependencies:s
 *          •LoginUseCase: Orchestrates the login flow through the Repository.
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
 *              •UseCase: Executes loginUseCase(credentials) within a managed coroutine.
 *              •Result Handling:
 *                  •Success: Sets success toast; navigation is handled via SessionManager state.
 *                  •Failure: Customizes error messages via the handleError() hook.
 *          Layering & Architecture
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
import androidx.lifecycle.viewModelScope
import com.rho.studio.ui.core.ui.base.BaseViewModel
import com.rho.studio.ui.core.domain.model.Credentials
import com.rho.studio.ui.core.domain.model.Result
import com.rho.studio.ui.core.domain.usecase.LoginUseCase
import com.rho.studio.ui.core.data.manager.SessionManager
import com.rho.studio.ui.core.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class LoginViewModel : BaseViewModel() {
    // ==================== DEPENDENCIES ====================
    private val loginUseCase = LoginUseCase(AuthRepositoryImpl(), SessionManager.getInstance())
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
    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()
    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()
    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()

    // ==================== FORM VALIDATION ====================
    fun onEmailChanged(email: String) {
        this.email = email
        credentials.email = email
        
        emailDebounceJob?.cancel()
        emailDebounceJob = viewModelScope.launch {
            delay(300.milliseconds)
            validateEmail()
            validateForm()
        }
    }

    fun onPasswordChanged(password: String) {
        this.password = password
        credentials.password = password
        
        passwordDebounceJob?.cancel()
        passwordDebounceJob = viewModelScope.launch {
            delay(300.milliseconds)
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

    fun onLoginClick() {
        if (isLoading.value) return
        if (!credentials.isValid) {
            validateEmail()
            validatePassword()
            return
        }
        performLogin()
    }

    private fun performLogin() {
        loginJob = launchWithLoading(
            block = {
                when (val result = loginUseCase(credentials)) {
                    is Result.Success -> {
                        showToast("Login successful!")
                        clearError()
                    }
                    is Result.Error -> {
                        handleError(result.exception)
                    }
                    Result.Loading -> {}
                }
            }
        )
    }

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
        val message = e.message ?: "An unknown error occurred"
        val formattedMessage = if (message.startsWith("Login failed")) message else "Login failed: $message"
        super.handleError(Exception(formattedMessage, e.cause))
    }
}