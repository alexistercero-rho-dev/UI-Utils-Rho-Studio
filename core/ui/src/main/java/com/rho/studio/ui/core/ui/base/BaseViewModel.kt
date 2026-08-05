/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         BaseViewModel.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-05
 * ==============================================================================================
 * Description:
 *  The BaseViewModel is an abstract base class designed for the Rho Studio UI architecture.
 *  It extends the standard Android androidx.lifecycle.ViewModel to provide a consistent
 *  foundation for state management, coroutine safety, and error handling across
 *  all feature-specific ViewModels.
 *
 *  Key Features
 *      • Automatic Loading State: Integrated tracking of background tasks via StateFlow.
 *      • Safe Coroutine Execution: Built-in exception handling wrappers to prevent app crashes.
 *      • Job Management: Tracking and automatic cancellation of active coroutines.
 *      • UI Communication: Standardized StateFlow streams for errors and toast notifications.
 * ==============================================================================================
 */
package com.rho.studio.ui.core.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ## BaseViewModel - Base class for all feature ViewModels
 *
 * Abstract base class designed for the Rho Studio UI architecture.
 * It extends the standard Android androidx.lifecycle.ViewModel to provide a consistent
 * foundation for state management, coroutine safety, and error handling across
 * all feature-specific ViewModels.
 *
 * __Key Features__
 *
 *  •Automatic Loading State: Integrated tracking of background tasks.
 *
 *  •Safe Coroutine Execution: Built-in exception handling to prevent app crashes.
 *
 *  •Job Management: Automatic tracking and cancellation of active coroutines when the ViewModel is cleared.
 *
 *  •UI Communication: Standardized StateFlow streams for errors and toast notifications.
 *
 * In standard Android development, if a coroutine launched in viewModelScope
 * throws an exception that isn't caught, the entire app crashes. By using `launchSafe`
 * or `launchWithLoading`, you avoid repetitive boilerplate code. Instead of writing
 * try-catch in every single function, the base class handles exceptions globally
 * via `handleError` while still allowing optional local error handling.
 */
abstract class BaseViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val jobs = mutableListOf<Job>()

    /** Launch a coroutine with automatic loading state*/
    protected fun launchWithLoading(
        block: suspend CoroutineScope.() -> Unit,
        onError: ((Exception) -> Unit)? = null
    ): Job {
        _isLoading.value = true

        val job = viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                handleError(e)
                onError?.invoke(e)
            } finally {
                _isLoading.value = false
            }
        }

        jobs.add(job)
        return job
    }

    protected fun launchSafe(
        block: suspend CoroutineScope.() -> Unit,
        onError: ((Exception) -> Unit)? = null
    ): Job {
        val job = viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                handleError(e)
                onError?.invoke(e)
            }
        }

        jobs.add(job)
        return job
    }

    // ==================== ERROR HANDLING ====================
    /**
     * Centralized error handling logic for coroutines launched via [launchSafe].
     *
     * The default implementation prints the stack trace. Override this method
     * in subclasses to provide feature-specific error handling, such as updating
     * UI state with error messages or logging to a remote service.
     *
     * @param e The [Exception] caught during coroutine execution.
     */
    protected open fun handleError(e: Exception) {
        e.printStackTrace()
        _error.value = e.message ?: "An error occurred"
    }

    fun clearError() {
        _error.value = null
    }

    protected fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun cancelAllJobs() {
        jobs.forEach { it.cancel() }
        jobs.clear()
    }

    override fun onCleared() {
        cancelAllJobs()
    }
}
