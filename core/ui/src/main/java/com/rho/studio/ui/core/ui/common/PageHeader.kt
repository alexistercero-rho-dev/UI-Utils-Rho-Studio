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
 * Date:         2026-08-06
 * ============================================================================
 * Description:
 *      A standardized header component that serves as the primary entry point
 *      for user orientation within the application's layout.
 *
 *      Key Features:
 *          • Reactive State Management: Leverages Kotlin StateFlow and
 *            collectAsState() to synchronize UI with HeaderViewModel
 *            session data in real-time.
 *          • Adaptive Greeting: Renders a personalized welcome message for
 *            authenticated users with a graceful fallback for guest states.
 *          • Hierarchical Typography: Establishes visual hierarchy using
 *            bold 24sp headlines and 16sp subtitles for clear section
 *            identification.
 *          • Rho Design System Compliance: Implements the SilverGray color
 *            specification and standard 16dp structural padding.
 *          • Localization Ready: Leverages Android's string resource system
 *            for multi-language greeting support.
 * ============================================================================
 */
package com.rho.studio.ui.core.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rho.studio.ui.core.ui.R

@Composable
fun PageHeader(
    viewModel: HeaderViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val title by viewModel.title.collectAsState()

    Column(
        modifier = modifier
            .statusBarsPadding()
            .padding(16.dp)
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
