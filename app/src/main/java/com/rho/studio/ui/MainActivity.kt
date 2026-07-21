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
 * Date:         2026-07-20
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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.rho.studio.ui.core.manager.SessionManager
import com.rho.studio.ui.databinding.ActivityMainBinding
import com.rho.studio.ui.features.auth.LoginFragment
import com.rho.studio.ui.features.auth.LoginViewModel
import com.rho.studio.ui.features.home.HomeFragment

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
    private lateinit var navController: NavController
    private var isNavGraphReady = false

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

            // Removed showInitialScreen() as handleAuthStateChange 
            // will be triggered by the SessionManager observer automatically.
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
        
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_container) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun initializeManagers() {
        SessionManager.init(applicationContext)
        sessionManager = SessionManager.getInstance()
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding.sessionManager = sessionManager
    }

    private fun setupObservers() {
        // Wait for session check before deciding initial route
        sessionManager.isSessionChecked.observe(this) { isChecked ->
            if (isChecked) {
                handleAuthStateChange(sessionManager.isAuthenticatedSync())
            } else {
                // Show loading while checking
                binding.progressBar.visibility = View.VISIBLE
            }
        }

        sessionManager.isAuthenticated.observe(this) { isAuthenticated ->
            // Only handle subsequent changes if graph is already ready
            if (isNavGraphReady) {
                handleAuthStateChange(isAuthenticated)
            }
        }

        sessionManager.isLoading.observe(this) { isLoading ->
            // Combine with isSessionChecked logic
            if (sessionManager.isSessionChecked.value == true) {
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        sessionManager.error.observe(this) { error ->
            error?.let {
                android.widget.Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                sessionManager.clearError()
            }
        }
    }

    private fun showLoginScreen() {
        val currentDest = navController.currentDestination?.id
        if (currentDest != null && currentDest != R.id.loginFragment) {
            navController.navigate(R.id.action_homeFragment_to_loginFragment)
        }
    }

    private fun showHomeScreen() {
        val currentDest = navController.currentDestination?.id
        if (currentDest != null && currentDest != R.id.homeFragment) {
            navController.navigate(R.id.action_loginFragment_to_homeFragment)
        }
    }

    private fun handleAuthStateChange(isAuthenticated: Boolean) {
        Log.d("MainActivity", "Auth state change: isAuthenticated = $isAuthenticated")
        
        if (!isNavGraphReady) {
            setupNavGraph(isAuthenticated)
            isNavGraphReady = true
            // Hide initial loading
            binding.progressBar.visibility = if (sessionManager.isLoading.value == true) 
                View.VISIBLE else View.GONE
        } else {
            if (isAuthenticated) {
                showHomeScreen()
                loginViewModel.resetForm()
            } else {
                showLoginScreen()
            }
        }
    }

    /**
     * Set up the Navigation Graph programmatically to avoid the "Start Destination" flicker.
     */
    private fun setupNavGraph(isAuthenticated: Boolean) {
        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.nav_graph)

        // Choose start destination based on authentication state
        graph.setStartDestination(if (isAuthenticated) R.id.homeFragment else R.id.loginFragment)
        
        navController.graph = graph
    }

    fun getLoginViewModel(): LoginViewModel = loginViewModel

    override fun onDestroy() {
        super.onDestroy()
        sessionManager.cleanup()
    }
}
