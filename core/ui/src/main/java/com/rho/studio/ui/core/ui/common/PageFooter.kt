/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ============================================================================
 * File:         PageFooter.kt (composable UI)
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-06
 * ============================================================================
 * Description:
 *      A reusable footer component designed for consistent placement at the
 *      bottom of screens to provide access to essential session-level
 *      actions. It ensures a uniform user experience across different
 *      modules by standardizing the appearance and behavior of the
 *      logout mechanism.
 *
 *      Key Features:
 *          • Session Management: Provides a clear logout entry point,
 *            delegating the action to the caller via a callback.
 *          • Semantic Styling: Utilizes the brand's primary red (rho_red)
 *            to visually communicate the destructive nature of the action.
 *          • Adaptive Layout: Configured to span the full width with
 *            standard padding for high touch-target visibility.
 * ============================================================================
 */
package com.rho.studio.ui.core.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rho.studio.ui.core.ui.R

@Composable
fun PageFooter(
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onLogoutClick,
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = colorResource(id = R.color.rho_red)
        )
    ) {
        Text(text = stringResource(id = R.string.logout))
    }
}
