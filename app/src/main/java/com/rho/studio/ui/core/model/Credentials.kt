/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         Credentials.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-02-23
 * ==============================================================================================
 * Description:
 *  Represents the user's authentication data and provides validation logic.
 * ==============================================================================================
 */
package com.rho.studio.ui.core.model

import android.util.Patterns

/**
 * Represents the user's authentication data and provides validation logic.
 *
 * This class encapsulates the email and password fields, offering helper properties
 * to verify format integrity (RFC-compliant email patterns) and security requirements
 * (minimum password length).
 *
 * It is primarily used by ViewModels to manage UI state and provide immediate
 * validation feedback to the user.
 *
 * @property email The user's email address. Defaults to an empty string.
 * @property password The user's password. Defaults to an empty string.
 */
data class Credentials(
    var email: String = "",
    var password: String = ""
) {
    val isEmailValid: Boolean
        get() = email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    val isPasswordValid: Boolean
        get() = password.length >= 6

    val isValid: Boolean
        get() = isEmailValid && isPasswordValid

    val validationErrors: List<String>
        get() {
            val errors = mutableListOf<String>()
            if (email.isBlank()) errors.add("Email is required")
            else if (!isEmailValid) errors.add("Invalid email format")

            if (password.isBlank()) errors.add("Password is required")
            else if (!isPasswordValid) errors.add("Password must be at least 6 characters")

            return errors
        }

    val isEmpty: Boolean
        get() = email.isEmpty() && password.isEmpty()

    fun clear() {
        email = ""
        password = ""
    }

}