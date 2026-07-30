/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ============================================================================
 * File:         LoginEmailField.kt (composable UI)
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-07-29
 * ============================================================================
 * Description:  A reusable Jetpack Compose component that provides a styled
 *               email input field for the Login screen, featuring validation
 *               state handling and integration with LoginViewModel.
 * ============================================================================
 */
package com.rho.studio.ui.features.auth.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.rho.studio.ui.R
import com.rho.studio.ui.features.auth.LoginViewModel
import com.rho.studio.ui.ui.theme.ErrorRed
import com.rho.studio.ui.ui.theme.RhoRed
import com.rho.studio.ui.ui.theme.RhoStrongGray
import com.rho.studio.ui.ui.theme.SilverGray
import com.rho.studio.ui.ui.theme.TitleGray

@Composable
fun LoginEmailField(
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier
) {
    val emailError by viewModel.emailError.observeAsState()

    OutlinedTextField(
        value = viewModel.email,
        onValueChange = { 
            viewModel.onEmailChanged(it)
        },
        label = { Text(stringResource(R.string.email_hint)) },
        modifier = modifier.fillMaxWidth(),
        isError = emailError != null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = RhoStrongGray,
            unfocusedBorderColor = SilverGray,
            errorBorderColor = ErrorRed,
            focusedLabelColor = RhoStrongGray,
            unfocusedLabelColor = TitleGray,
            focusedLeadingIconColor = RhoStrongGray,
            unfocusedLeadingIconColor = TitleGray
        ),
        supportingText = {
            if (emailError != null) {
                Text(text = emailError!!)
            }
        },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Email, contentDescription = null)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true
    )
}
