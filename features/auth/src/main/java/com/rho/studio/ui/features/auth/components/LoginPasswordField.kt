/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         LoginPasswordField.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-06
 * ==============================================================================================
 * Description: A compose password input component for the login screen that features
 *              visibility toggling, validation error handling, and standardized Rho styling.
 * ==============================================================================================
 */
package com.rho.studio.ui.features.auth.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.rho.studio.ui.core.ui.R
import com.rho.studio.ui.features.auth.LoginViewModel
import com.rho.studio.ui.core.ui.theme.ErrorRed
import com.rho.studio.ui.core.ui.theme.RhoStrongGray
import com.rho.studio.ui.core.ui.theme.SilverGray
import com.rho.studio.ui.core.ui.theme.TitleGray

@Composable
fun LoginPasswordField(
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier
) {
    val passwordError by viewModel.passwordError.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = viewModel.password,
        onValueChange = { viewModel.onPasswordChanged(it) },
        label = { Text(stringResource(R.string.password_hint)) },
        modifier = modifier.fillMaxWidth(),
        isError = passwordError != null,
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
            if (passwordError != null) {
                Text(text = passwordError!!)
            }
        },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Lock, contentDescription = null)
        },
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = description)
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true
    )
}
