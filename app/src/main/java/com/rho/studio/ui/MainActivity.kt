/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==========================================================================
 * File:         MainActivity.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-02-23
 * ==========================================================================
 * Description:
 *      This activity follows the "Single Activity" architecture pattern, acting as the
 *      main orchestrator for fragment navigation and global state management.
 * ==========================================================================
 */
package com.rho.studio.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.rho.studio.ui.core.manager.SessionManager
import com.rho.studio.ui.databinding.ActivityMainBinding
import com.rho.studio.ui.features.auth.LoginFragment
import com.rho.studio.ui.features.auth.LoginViewModel

/**
 * The primary entry point and root container for the RHO Studio application.
 *
 * This activity follows the "Single Activity" architecture pattern, acting as the
 * main orchestrator for fragment navigation and global state management.
 *
 * ### Key Responsibilities:
 * 1. **Initialization:** Bootstraps the [SessionManager] and core ViewModels.
 * 2. **Authentication Routing:** Observes [SessionManager.isAuthenticated] to
 *    automatically toggle between the login flow and the home dashboard.
 * 3. **Global Error Handling:** Implements a top-level [Thread.UncaughtExceptionHandler]
 *    to log and display fatal crashes during development.
 * 4. **Resource Management:** Ensures the [SessionManager] is cleaned up during
 *    the activity destruction to prevent memory leaks.
 *
 * ### UI Components:
 * - Uses [ActivityMainBinding] for layout management.
 * - Hosts fragments within the `main_container` (ID: R.id.main_container).
 * - Manages a global progress indicator synchronized with [SessionManager.isLoading].
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Set a default error handler
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                Log.e("MainActivityCrash", "Uncaught exception", throwable)
                // Show error in a Toast (might not work if UI thread is dead)
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Crash: ${throwable.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            initializeBinding()
            initializeManagers()
            setupObservers()

            if (savedInstanceState == null) {
                showInitialScreen()
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Initialization failed", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("is_initialized", true)
    }

    private fun initializeBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
    }

    private fun initializeManagers() {
        SessionManager.init(applicationContext)
        sessionManager = SessionManager.getInstance()
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding.sessionManager = sessionManager
    }

    private fun setupObservers() {
        sessionManager.isAuthenticated.observe(this) { isAuthenticated ->
            handleAuthStateChange(isAuthenticated)
        }

        sessionManager.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        sessionManager.error.observe(this) { error ->
            error?.let {
                android.widget.Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                sessionManager.clearError()
            }
        }
    }

    private fun showInitialScreen() {
        if (sessionManager.isAuthenticatedSync()) {
            // TODO: Navigate to home screen
        } else {
            showLoginScreen()
        }
    }

    private fun showLoginScreen() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, LoginFragment())
            .commitAllowingStateLoss()
    }

    private fun handleAuthStateChange(isAuthenticated: Boolean) {
        if (isAuthenticated) {
            // TODO: Navigate to home screen
            loginViewModel.resetForm()
        } else {
            showLoginScreen()
        }
    }

    fun getLoginViewModel(): LoginViewModel = loginViewModel

    override fun onDestroy() {
        super.onDestroy()
        sessionManager.cleanup()
    }
}