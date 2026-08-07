/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         AuthRepositoryImpl.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-04
 * ==============================================================================================
 * Description: Repository for persisting session data. Current as a mock.
 * ==============================================================================================
 */
package com.rho.studio.ui.core.data.repository

import com.rho.studio.ui.core.domain.model.Credentials
import com.rho.studio.ui.core.domain.model.User
import com.rho.studio.ui.core.domain.repository.AuthRepository
import kotlinx.coroutines.delay

/**
 * Mock implementation of [AuthRepository] for development purposes.
 * This will be replaced by Firebase Auth in the future.
 */
class AuthRepositoryImpl : AuthRepository {
    override suspend fun login(credentials: Credentials): User {
        // Simulated network delay
        delay(1500)

        // Mocking authentication check
        if (credentials.email == "error@rho.studio") {
            throw RuntimeException("Network error")
        }

        // Mock User creation
        return User(
            id = "user_${System.currentTimeMillis()}",
            email = credentials.email,
            name = extractNameFromEmail(credentials.email)
        )
    }

    private fun extractNameFromEmail(email: String): String {
        return email.substringBefore("@")
            .split(".", "_", "-")
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
    }
}
