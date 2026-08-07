/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==========================================================================
 * File:         HeaderViewModel.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-06
 * ==========================================================================
 * Description:
 *      ViewModel for the reusable PageHeaderFragment.
 *      Decouples the header from feature-specific ViewModels by sourcing
 *      user data directly from the SessionManager and managing the
 *      page-specific title state reactively.
 * ==========================================================================
 */
package com.rho.studio.ui.core.ui.common

import com.rho.studio.ui.core.ui.base.BaseViewModel
import com.rho.studio.ui.core.data.manager.SessionManager
import com.rho.studio.ui.core.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HeaderViewModel : BaseViewModel() {
    private val sessionManager = SessionManager.getInstance()
    val currentUser: StateFlow<User?> = sessionManager.currentUser
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()
    fun setTitle(newTitle: String) { _title.value = newTitle }
}
