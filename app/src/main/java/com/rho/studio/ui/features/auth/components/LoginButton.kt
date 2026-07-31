/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ============================================================================
 * File:         LoginButton.kt (composable UI)
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-07-29
 * ============================================================================
 * Description:  A custom Jetpack Compose button component for the Login screen.
 *               It observes the LoginViewModel state to handle validation logic
 *               and loading states, automatically disabling interaction and
 *               updating its UI when a login attempt is in progress.
 * ============================================================================
 */
package com.rho.studio.ui.features.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rho.studio.ui.R
import com.rho.studio.ui.features.auth.LoginViewModel
import com.rho.studio.ui.ui.theme.RhoRed

@Composable
fun LoginButton(
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier
) {
    val isFormValid by viewModel.isFormValid.observeAsState(false)
    val isLoading by viewModel.isLoading.observeAsState(false)

    Button(
        onClick = { viewModel.onLoginClick() },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = isFormValid && !isLoading,
        colors = ButtonDefaults.buttonColors(containerColor = RhoRed)
    ) {
        Text(text = stringResource(if (isLoading) R.string.rho_studio_app else R.string.login))
    }
}