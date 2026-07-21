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
 * Date:         2026-07-21
 * ==========================================================================
 * Description:
 *      ViewModel for the reusable PageHeaderFragment.
 *      Decouples the header from feature-specific ViewModels by sourcing
 *      user data directly from the SessionManager.
 * ==========================================================================
 */
package com.rho.studio.ui.features.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rho.studio.ui.core.base.BaseViewModel
import com.rho.studio.ui.core.manager.SessionManager
import com.rho.studio.ui.core.model.User

class HeaderViewModel : BaseViewModel() {

    private val sessionManager = SessionManager.getInstance()
    
    val currentUser: LiveData<User?> = sessionManager.currentUser

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    fun setTitle(newTitle: String) {
        _title.value = newTitle
    }
}