/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ============================================================================
 * File:         LoginPasswordField.kt (composable UI)
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-07-29
 * ============================================================================
 * Description:  
 *      A specialized password input component for the Authentication screen.
 *      It integrates directly with the LoginViewModel to provide real-time 
 *      validation feedback and visibility toggling.
 *
 *      Key Features:
 *          • Reactive State: Observes password error states from the ViewModel.
 *          • Visibility Toggle: Built-in IconButton to switch between masked 
 *            and plain text using VisualTransformation.
 *          • Standardized Styling: Uses the Rho Studio theme palette (RhoStrongGray, 
 *            SilverGray, ErrorRed) for a consistent UI experience.
 *          • Accessibility: Includes localized hints and dynamic content descriptions 
 *            for the visibility icons.
 * ============================================================================
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.rho.studio.ui.R
import com.rho.studio.ui.features.auth.LoginViewModel
import com.rho.studio.ui.ui.theme.ErrorRed
import com.rho.studio.ui.ui.theme.RhoRed
import com.rho.studio.ui.ui.theme.RhoStrongGray
import com.rho.studio.ui.ui.theme.SilverGray
import com.rho.studio.ui.ui.theme.TitleGray

@Composable
fun LoginPasswordField(
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier
) {
    val passwordError by viewModel.passwordError.observeAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = viewModel.password,
        onValueChange = { 
            viewModel.onPasswordChanged(it)
        },
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
