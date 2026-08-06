/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         LoginButton.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-06
 * ==============================================================================================
 * Description: A compose action button for triggering the authentication process. It
 *              synchronizes with LoginViewModel to handle loading states and form validation.
 * ==============================================================================================
 */
package com.rho.studio.ui.features.auth.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rho.studio.ui.core.ui.R
import com.rho.studio.ui.features.auth.LoginViewModel
import com.rho.studio.ui.core.ui.theme.RhoRed

@Composable
fun LoginButton(
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier
) {
    val isFormValid by viewModel.isFormValid.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

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
