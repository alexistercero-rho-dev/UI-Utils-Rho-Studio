/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         HomeViewModel.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-07-16
 * ==============================================================================================
 * Description: ViewModel for the Home feature, managing dashboard state and session data.
 * ==============================================================================================
 */
package com.rho.studio.ui.features.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rho.studio.ui.R
import com.rho.studio.ui.core.base.BaseViewModel
import com.rho.studio.ui.core.manager.SessionManager
import com.rho.studio.ui.core.model.ServiceModule
import com.rho.studio.ui.core.model.User

class HomeViewModel : BaseViewModel() {

    private val sessionManager = SessionManager.getInstance()

    private val _currentUser = MutableLiveData<User?>(sessionManager.getCurrentUserSync())
    val currentUser: LiveData<User?> = _currentUser

    // Parametrized services for the dashboard
    private val _services = MutableLiveData<List<ServiceModule>>(
        listOf(
            ServiceModule("inv", R.string.module_inventory, R.color.rho_red),
            ServiceModule("sales", R.string.module_sales, R.color.rho_strong_gray),
            ServiceModule("cust", R.string.module_customers, R.color.rho_strong_gray),
            ServiceModule("rep", R.string.module_reports, R.color.rho_red)
        )
    )
    val services: LiveData<List<ServiceModule>> = _services

    private val _navigateToService = MutableLiveData<String?>()
    val navigateToService: LiveData<String?> = _navigateToService

    fun onServiceClick(serviceId: String) {
        _navigateToService.value = serviceId
    }

    fun onServiceNavigated() {
        _navigateToService.value = null
    }

    /**
     * Trigger user logout via SessionManager.
     * The transition to the login screen is handled by the MainActivity observer.
     */
    fun logout() {
        launchWithLoading({
            sessionManager.logout().join()
        })
    }
}
