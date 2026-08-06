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
 * Date:         2026-08-05
 * ==============================================================================================
 * Description: core:domain
 *  Represents the user's authentication data and provides validation logic.
 * ==============================================================================================
 */
package com.rho.studio.ui.core.domain.model

/**
 * Represents the user's authentication data and provides validation logic.
 */
data class Credentials(
    var email: String = "",
    var password: String = ""
) {
    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    }

    val isEmailValid: Boolean
        get() = email.isNotBlank() && email.matches(EMAIL_REGEX)

    val isPasswordValid: Boolean
        get() = password.length >= 6

    val isValid: Boolean
        get() = isEmailValid && isPasswordValid

    fun clear() {
        email = ""
        password = ""
    }
}
