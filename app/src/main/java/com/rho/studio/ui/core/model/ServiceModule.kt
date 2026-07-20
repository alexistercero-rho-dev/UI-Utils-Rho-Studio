package com.rho.studio.ui.core.model

import androidx.annotation.ColorRes

/**
 * Represents a service or module available on the home dashboard.
 * 
 * @property id Unique identifier for the service.
 * @property titleRes String resource ID for the service name.
 * @property backgroundColor Background color resource for the button.
 */
data class ServiceModule(
    val id: String,
    val titleRes: Int,
    @ColorRes val backgroundColor: Int
)
