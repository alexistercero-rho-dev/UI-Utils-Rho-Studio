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
 * Date:         2026-02-23
 * ==============================================================================================
 * Description: User model - Core data class used across features
 * ==============================================================================================
 */
package com.rho.studio.ui.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * User model - Core data class used across features
 *
 * This class implements [Parcelable] via `@Parcelize` to allow efficient data
 * transfer between Android components (Activities, Fragments) and to survive
 * configuration changes (screen rotations).
 *
 * It encapsulates basic identity data, profile information, and nested
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
@Parcelize
data class User(
    val id: String,
    val email: String,
    val name: String = "",
    val avatar: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis(),
    val preferences: UserPreferences = UserPreferences(),
    val isActive: Boolean = true
) : Parcelable {

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
        get() = if (name.isNotEmpty()) name else email.substringBefore("@")

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
/**
* Consider splitting if:
* UserPreferences grows significantly (adds 10+ properties)
* Other models depend on UserPreferences (creating circular dependencies)
* File exceeds 200-300 lines
* Different teams own User and UserPreferences
* */
@Parcelize
data class UserPreferences(
    val darkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val language: String = "en"
) : Parcelable

/**
 * User model - Core data class used across features
 *
 * In Android, you cannot simply pass a custom Kotlin object
 * (like User) directly from one Activity to another or
 * save it when the screen rotates.
 * The data must be converted into a format the Android
 * System understands (a byte stream).
 * This process is called Serialization.
 *
 * The standard way to do this in Android
 * is by implementing the Parcelable interface.
 */