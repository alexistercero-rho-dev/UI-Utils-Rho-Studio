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
 * Date:         2026-07-15
 * ==============================================================================================
 * Description:
 *  The BaseViewModel is an abstract base class designed for the Rho Studio UI architecture.
 *  It extends the standard Android androidx.lifecycle.ViewModel to provide a consistent
 *  foundation for state management, coroutine safety, and error handling across
 *  all feature-specific ViewModels.
 *
 *  Key Features
 *      •Automatic Loading State: Integrated tracking of background tasks.
 *      •Safe Coroutine Execution: Built-in exception handling to prevent app crashes.
 *      •Job Management: Automatic cancellation of active coroutines when the ViewModel is cleared.
 *      •UI Communication: Standardized LiveData streams for errors and toast notifications.
 * ==============================================================================================
 */
package com.rho.studio.ui.core.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
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
 *  •Job Management: Automatic cancellation of active coroutines when the ViewModel is cleared.
 *
 *  •UI Communication: Standardized LiveData streams for errors and toast notifications.
 *
 * In standard Android development, if a coroutine launched in viewModelScope
 * throws an exception that isn't caught, the entire app crashes.By using launchSafe,
 * avoiding repetitive boilerplate code. Instead of writing try-catch in every single function
 */
abstract class BaseViewModel : ViewModel() {

    // Loading state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Error messages
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Toast messages (one-time)
    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    // Track active jobs
    private val jobs = mutableListOf<Job>()

    /**
     * Launch a coroutine with automatic loading state
     */
    protected fun launchWithLoading(
        block: suspend CoroutineScope.() -> Unit,
        onError: ((Exception) -> Unit)? = null
    ): Job {
        _isLoading.postValue(true)

        val job = viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                handleError(e)
                onError?.invoke(e)
            } finally {
                _isLoading.postValue(false)
            }
        }

        jobs.add(job)
        return job
    }

    /**
     * Executes a coroutine block safely within the [viewModelScope].
     *
     * This function wraps the execution of the [block] in a try-catch block.
     * If an exception occurs during the execution of the coroutine, it is caught
     * and passed to [handleError], preventing the app from crashing.
     *
     * @param block The suspendable lambda expression to be executed.
     */
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
        _error.postValue(e.message ?: "An error occurred")
    }

    /** Clear current error message */
    fun clearError() {
        _error.value = null
    }

    // ==================== TOAST MESSAGES ====================

    /** Show a one-time toast message to user */
    protected fun showToast(message: String) {
        _toastMessage.postValue(message)
    }

    /** Clear current toast message */
    fun clearToastMessage() {
        _toastMessage.value = null
    }

    // ==================== JOB MANAGEMENT ====================

    /** Cancel all active coroutine jobs */
    fun cancelAllJobs() {
        jobs.forEach { it.cancel() }
        jobs.clear()
    }

    override fun onCleared() {
        cancelAllJobs()  // Prevent memory leaks
        //super.onCleared()
    }
}