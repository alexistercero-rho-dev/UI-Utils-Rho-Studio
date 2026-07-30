/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ============================================================================
 * File:         LoginScreen.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-07-29
 * ============================================================================
 * Description:  Implementation of the Login screen using Jetpack Compose and
 *               MVVM architecture.
 *
 * Features:
 * - State Management: Utilizes LiveData observed as Compose State for reactive
 *   UI updates (e.g., loading states, input validation).
 * - Unidirectional Data Flow (UDF): Events are passed from the UI to the
 *   ViewModel, while State flows down from the ViewModel to the Composables.
 * - Component Modularization: Extracts input fields and buttons into dedicated
 *   sub-components for reusability and cleaner code structure.
 * - Theming: Integrates custom branding colors (RhoRed, SilverGray) via
 *   gradient backgrounds and Material3 typography.
 * ============================================================================
 */
package com.rho.studio.ui.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rho.studio.ui.R
import com.rho.studio.ui.features.auth.components.LoginButton
import com.rho.studio.ui.features.auth.components.LoginEmailField
import com.rho.studio.ui.features.auth.components.LoginPasswordField
import com.rho.studio.ui.ui.theme.Black
import com.rho.studio.ui.ui.theme.RhoRed
import com.rho.studio.ui.ui.theme.RhoStrongGray
import com.rho.studio.ui.ui.theme.SilverGray
import com.rho.studio.ui.ui.theme.White

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier
) {
    val isLoading by viewModel.isLoading.observeAsState(false)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        RhoRed,
                        SilverGray,
                        Black
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(48.dp))
            LoginEmailField(viewModel = viewModel)
            Spacer(modifier = Modifier.height(16.dp))
            LoginPasswordField(viewModel = viewModel)
            Spacer(modifier = Modifier.height(24.dp))
            LoginButton(viewModel = viewModel)
        }
    }
}
