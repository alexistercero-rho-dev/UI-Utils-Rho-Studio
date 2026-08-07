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
 * Date:         2026-08-04
 * ==========================================================================
 * Description:
 *      The primary entry point for the RHO Studio application, migrated to 
 *      pure Jetpack Compose.
 *      Screen Assembly: Built LoginScreen and HomeScreen to unify the components.
 *      Main Entry Point: Migrated MainActivity to ComponentActivity.
 *      Compose Navigation: Implemented a NavHost in MainActivity to handle routing
 *      based on SessionManager state, replacing nav_graph.xml.
 *      State Management: Switched state observation from LiveData to StateFlow
 *      using collectAsState() for better compatibility with Compose.
 * ==========================================================================
 */
package com.rho.studio.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rho.studio.ui.core.data.manager.SessionManager
import com.rho.studio.ui.features.auth.LoginScreen
import com.rho.studio.ui.features.auth.LoginViewModel
import com.rho.studio.ui.features.home.HomeScreen
import com.rho.studio.ui.features.home.HomeViewModel
import com.rho.studio.ui.core.ui.theme.UITheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeManagers()
        setContent {
            UITheme {
                MainContent()
            }
        }
    }

    private fun initializeManagers() {
        SessionManager.init(applicationContext)
        sessionManager = SessionManager.getInstance()
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        
        lifecycleScope.launch {
            sessionManager.error.collect { error ->
                error?.let {
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
                    sessionManager.clearError()
                }
            }
        }
    }

    @Composable
    private fun MainContent() {
        val navController = rememberNavController()
        val isSessionChecked by sessionManager.isSessionChecked.collectAsState()
        val isAuthenticated by sessionManager.isAuthenticated.collectAsState()
        val isLoading by sessionManager.isLoading.collectAsState()

        if (!isSessionChecked) {
            LoadingScreen()
            return
        }

        LaunchedEffect(isAuthenticated) {
            if (isAuthenticated) {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            } else {
                loginViewModel.resetForm()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = if (isAuthenticated) "home" else "login"
            ) {
                composable("login") {
                    LoginScreen(viewModel = loginViewModel)
                }
                composable("home") {
                    HomeScreen(homeViewModel = homeViewModel)
                }
            }

            if (isLoading) {
                LoadingOverlay()
            }
        }
    }

    @Composable
    private fun LoadingScreen() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    @Composable
    private fun LoadingOverlay() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sessionManager.cleanup()
    }
}