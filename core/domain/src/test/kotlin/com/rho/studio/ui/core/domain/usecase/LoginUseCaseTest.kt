/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         LoginUseCaseTest.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-06
 * ==============================================================================================
 * Description: Check expected behavior of [LoginUseCase]
 * ==============================================================================================
 */
package com.rho.studio.ui.core.domain.usecase

import com.rho.studio.ui.core.domain.model.Credentials
import com.rho.studio.ui.core.domain.model.Result
import com.rho.studio.ui.core.domain.model.User
import com.rho.studio.ui.core.domain.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var authRepository: AuthRepository
    private lateinit var sessionManager: SessionManagerInterface
    private var lastUpdatedUser: User? = null
    private var isSessionCleared = false

    @Before
    fun setUp() {
        sessionManager = object : SessionManagerInterface {
            override fun updateSession(user: User) {
                lastUpdatedUser = user
            }
            override fun clearSession() {
                isSessionCleared = true
            }
            override fun extractNameFromEmail(email: String): String {
                return "Test"
            }
        }
        authRepository = object : AuthRepository {
            override suspend fun login(credentials: Credentials): User {
                if (credentials.email == "error@rho.studio") throw RuntimeException("Network error")
                return User("user_123", credentials.email, "Test")
            }
        }
        loginUseCase = LoginUseCase(authRepository, sessionManager)
    }

    @Test
    fun `login success with valid credentials`() = runBlocking {
        val credentials = Credentials("test@rho.studio", "password123")
        val result = loginUseCase(credentials)
        assertTrue(result is Result.Success)
        val user = (result as Result.Success).data
        assertEquals("test@rho.studio", user.email)
        assertEquals("Test", user.name)
        assertEquals(user, lastUpdatedUser)
    }

    @Test
    fun `login failure with invalid credentials`() = runBlocking {
        val credentials = Credentials("invalid-email", "short")
        val result = loginUseCase(credentials)
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is IllegalArgumentException)
    }

    @Test
    fun `logout success`() = runBlocking {
        val logoutUseCase = LogoutUseCase(sessionManager)
        logoutUseCase(Unit)
        assertTrue(isSessionCleared)
    }
}
