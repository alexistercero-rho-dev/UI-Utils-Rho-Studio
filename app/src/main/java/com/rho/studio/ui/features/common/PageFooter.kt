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
 * Date:         2026-07-29
 * ============================================================================
 * Description:  
 *      A persistent UI component placed at the bottom of the screen to 
 *      provide access to global session-level actions.
 *
 *      Key Features:
 *          • Session Management: Provides a clear entry point for the user 
 *            to log out, delegating the operation to the HomeViewModel.
 *          • Distinct Styling: Utilizes the brand's primary red (rho_red) 
 *            for the logout action to signal its significance.
 *          • Layout Integration: Designed to span the full width of the 
 *            screen with standard padding, ensuring high touch-target visibility.
 * ============================================================================
 */
package com.rho.studio.ui.features.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rho.studio.ui.R
import com.rho.studio.ui.features.home.HomeViewModel

@Composable
fun PageFooter(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = { viewModel.logout() },
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = colorResource(id = R.color.rho_red)
        )
    ) {
        Text(text = stringResource(id = R.string.logout))
    }
}
