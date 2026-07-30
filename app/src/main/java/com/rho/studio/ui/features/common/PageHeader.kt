/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ============================================================================
 * File:         PageHeader.kt (composable UI)
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-07-29
 * ============================================================================
 * Description: 
 *      A standard UI component that provides context and a personalized 
 *      greeting at the top of the application's screens.
 *
 *      Key Features:
 *          • Personalized Greeting: Dynamically displays the current user's 
 *            name, observing state from the HeaderViewModel.
 *          • Contextual Title: Provides a secondary text line to indicate 
 *            the current section or active feature of the app.
 *          • Branding Styles: Applies consistent typography (24sp Bold) 
 *            and the signature SilverGray color palette for readability.
 *          • Resource Integration: Uses localized string resources for 
 *            formatted greetings (e.g., "Welcome, [User]").
 * ============================================================================
 */
package com.rho.studio.ui.features.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rho.studio.ui.R

@Composable
fun PageHeader(
    viewModel: HeaderViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.observeAsState()
    val title by viewModel.title.observeAsState("")

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.welcome_user, currentUser?.name ?: "User"),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.silver_gray)
        )
        Text(
            text = title,
            fontSize = 16.sp,
            color = colorResource(id = R.color.silver_gray)
        )
    }
}
