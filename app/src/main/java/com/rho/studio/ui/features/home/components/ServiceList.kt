/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ============================================================================
 * File:         ServiceList.kt (composable UI)
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-07-29
 * ============================================================================
 * Description: 
 *      A grid-based component that displays the collection of available 
 *      services. It acts as the primary content container for the HomeScreen.
 *
 *      Key Features:
 *          • Adaptive Grid: Utilizes `LazyVerticalGrid` with a fixed column 
 *            count to present service items in a clean, organized layout.
 *          • State Observation: Reactively observes the services list from 
 *            the `HomeViewModel` using `observeAsState`.
 *          • Event Delegation: Forwards user interactions (clicks) back to 
 *            the ViewModel for centralized business logic handling.
 *          • Performance: Efficiently renders large lists by utilizing 
 *            lazy-loading mechanics.
 * ============================================================================
 */
package com.rho.studio.ui.features.home.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rho.studio.ui.features.home.HomeViewModel

@Composable
fun ServiceList(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val services by viewModel.services.observeAsState(emptyList())

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(services) { service ->
            ServiceItem(
                service = service,
                onClick = { viewModel.onServiceClick(it) }
            )
        }
    }
}
