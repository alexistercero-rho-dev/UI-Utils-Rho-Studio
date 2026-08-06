/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         LoginEmailField.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-06
 * ==============================================================================================
 * Description: A compose text input component for user email addresses, integrated with
 *              LoginViewModel for state management, validation feedback, and styling.
 * ==============================================================================================
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.rho.studio.ui.core.ui.R
import com.rho.studio.ui.features.auth.LoginViewModel
import com.rho.studio.ui.core.ui.theme.ErrorRed
import com.rho.studio.ui.core.ui.theme.RhoStrongGray
import com.rho.studio.ui.core.ui.theme.SilverGray
import com.rho.studio.ui.core.ui.theme.TitleGray

@Composable
fun LoginEmailField(
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier
) {
    val emailError by viewModel.emailError.collectAsState()

    OutlinedTextField(
        value = viewModel.email,
        onValueChange = { viewModel.onEmailChanged(it) },
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
