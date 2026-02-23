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
 * Date:         2026-02-23
 * ==============================================================================================
 * Description:  Base class for all feature ViewModels
 * ==============================================================================================
 */
package com.rho.studio.ui.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * BaseViewModel - Base class for all feature ViewModels
 *
 * Provides common functionality like coroutine scope and error handling
 *
 * In standard Android development, if a coroutine launched in viewModelScope
 * throws an exception that isn't caught, the entire app crashes.By using launchSafe,
 * avoiding repetitive boilerplate code. Instead of writing try-catch in every single function
 *
 * Inherit from this class to ensure consistent error logging and to leverage
 * the [launchSafe] utility for asynchronous operations.
 */

abstract class BaseViewModel : ViewModel() {
    /**
     * Executes a coroutine block safely within the [viewModelScope].
     *
     * This function wraps the execution of the [block] in a try-catch block.
     * If an exception occurs during the execution of the coroutine, it is caught
     * and passed to [handleError], preventing the app from crashing.
     *
     * @param block The suspendable lambda expression to be executed.
     */
    protected fun launchSafe(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

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
        // Override in child classes for specific error handling
        e.printStackTrace()
    }
}