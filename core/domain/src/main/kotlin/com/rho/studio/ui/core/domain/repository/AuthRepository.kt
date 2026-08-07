/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         AuthRepository.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-04
 * ==============================================================================================
 * Description: Expected behavior of the Auth feature.
 * ==============================================================================================
 */
package com.rho.studio.ui.core.domain.repository

import com.rho.studio.ui.core.domain.model.Credentials
import com.rho.studio.ui.core.domain.model.User

/**
 * Interface defining the authentication operations.
 */
interface AuthRepository {
    /**
     * Authenticates a user with the provided credentials.
     * @param credentials The user's email and password.
     * @return The authenticated [User].
     * @throws Exception if authentication fails.
     */
    suspend fun login(credentials: Credentials): User
}
