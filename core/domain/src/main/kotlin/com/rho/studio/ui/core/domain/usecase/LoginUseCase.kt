/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         LoginUseCase.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-06
 * ==============================================================================================
 * Description: Orchestrates the authentication process by validating user credentials,
 *              interacting with the AuthRepository to verify identity, and updating the
 *              global application session state upon successful login.
 * ==============================================================================================
 */
package com.rho.studio.ui.core.domain.usecase

import com.rho.studio.ui.core.domain.model.Credentials
import com.rho.studio.ui.core.domain.model.User
import com.rho.studio.ui.core.domain.repository.AuthRepository

/*** Encapsulates the login business transaction.*/
class LoginUseCase(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManagerInterface
) : BaseUseCase<Credentials, User>() {

    override suspend fun execute(parameters: Credentials): User {
        // 1. Validate input using the Credentials domain model.
        if (!parameters.isValid) {
            throw IllegalArgumentException("Invalid credentials")
        }

        // 2. Authentication performed by Repository
        val user = authRepository.login(parameters)

        // 3. Update global session state
        sessionManager.updateSession(user)

        return user
    }
}
