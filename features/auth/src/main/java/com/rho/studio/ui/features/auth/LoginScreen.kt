/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         LoginScreen.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-06
 * ==============================================================================================
 * Description: Provides the user interface for the authentication entry point.
 *              The screen renders a stylized login form including composable email and password
 *              input fields, handles input state via the LoginViewModel, and triggers
 *              identity verification workflows. It features a branded vertical gradient
 *              background and centers the authentication components for optimal focus.
 * ==============================================================================================
 */
package com.rho.studio.ui.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rho.studio.ui.core.ui.R
import com.rho.studio.ui.features.auth.components.LoginButton
import com.rho.studio.ui.features.auth.components.LoginEmailField
import com.rho.studio.ui.features.auth.components.LoginPasswordField
import com.rho.studio.ui.core.ui.theme.Black
import com.rho.studio.ui.core.ui.theme.RhoRed
import com.rho.studio.ui.core.ui.theme.SilverGray

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(RhoRed, SilverGray, Black)
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
