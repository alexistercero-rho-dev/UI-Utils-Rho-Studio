/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         User.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-05
 * ==============================================================================================
 * Description: User model - core:domain data class used across features
 * ==============================================================================================
 */
package com.rho.studio.ui.core.domain.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * User model - Core data class used across features
 *
 * Designed to be passed between Compose destinations and stored within
 * ViewModel states. It is structured to survive configuration changes
 * through standard state restoration mechanisms.
 *
 * Encapsulates basic identity data, profile information, and nested
 * [UserPreferences].
 *
 * @property id Unique identifier for the user.
 * @property email Primary contact and login identifier.
 * @property name Full name of the user. Defaults to empty string.
 * @property avatar Optional URL or path to the user's profile image.
 * @property createdAt Epoch timestamp of account creation.
 * @property lastLogin Epoch timestamp of the most recent session.
 * @property preferences Nested settings and UI configurations.
 * @property isActive Flag indicating if the account is currently enabled.
 */
data class User(
    val id: String,
    val email: String,
    val name: String = "",
    val avatar: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis(),
    val preferences: UserPreferences = UserPreferences(),
    val isActive: Boolean = true
) {

    val initials: String
        get() = if (name.isNotEmpty()) {
            name.split(" ")
                .take(2)
                .mapNotNull { it.firstOrNull()?.toString() }
                .joinToString("")
                .uppercase()
        } else {
            email.take(2).uppercase()
        }

    val displayName: String
        get() = name.ifEmpty { email.substringBefore("@") }

    fun getFormattedCreatedAt(pattern: String = "MMM dd, yyyy"): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(createdAt))
    }

    fun getLastLoginRelative(): String {
        val now = System.currentTimeMillis()
        val diff = now - lastLogin

        return when {
            diff < 60_000 -> "Just now"
            diff < 3_600_000 -> "${diff / 60_000} minutes ago"
            diff < 86_400_000 -> "${diff / 3_600_000} hours ago"
            diff < 2_592_000_000 -> "${diff / 86_400_000} days ago"
            else -> "Long time ago"
        }
    }
}

data class UserPreferences(
    val darkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val language: String = "en"
)
