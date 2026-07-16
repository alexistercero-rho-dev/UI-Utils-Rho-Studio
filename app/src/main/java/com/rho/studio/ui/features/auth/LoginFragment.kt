/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         LoginFragment.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-07-16
 * ==============================================================================================
 *  LoginFragment serves as the primary orchestration layer for the authentication feature.
 *  It acts as a parent container that manages the lifecycle, state observation,
 *  and composition of specialized login components. It inherits from BaseFragment
 *  to leverage standardized ViewBinding and ViewModel integration.
 * ==============================================================================================
 */
package com.rho.studio.ui.features.auth;

import android.view.View
import androidx.fragment.app.viewModels
import com.rho.studio.ui.BR
import com.rho.studio.ui.R
import com.rho.studio.ui.core.base.BaseFragment
import com.rho.studio.ui.databinding.FragmentLoginBinding
import com.rho.studio.ui.features.auth.components.LoginButtonFragment
import com.rho.studio.ui.features.auth.components.LoginEmailFragment
import com.rho.studio.ui.features.auth.components.LoginPasswordFragment


/**
 * LoginFragment - Main container for authentication
 *
 * Feature: auth
 *
 * Responsibilities:
 *  • Reusability:            Individual login components can be reused in other flows
 *                            (e.g., Registration).
 *  • Separation of Concerns: LoginFragment manages the "How" (layout/navigation),
 *                            while child fragments handle the "What" (specific inputs).
 *
 *  1. Fragment Composition
 *      The fragment initializes the UI by embedding three core sub-components
 *      into designated containers within fragment_login.xml:
 *          •LoginEmailFragment: Handles email input and validation.
 *          •LoginPasswordFragment: Handles password input and visibility.
 *          •LoginButtonFragment: Handles the submission trigger.
 *  2. State Observation
 *      It observes the LoginViewModel to react to the following states:
 *          •Toast Messages: Short-lived UI feedback (e.g., "Welcome back").
 *          •Errors: Long-lived feedback for failed authentication attempts.
 *          •Loading State: Toggles the visibility of a global ProgressBar
 *                          to block interaction during network requests.
 *  3. Lifecycle Management
 *      •Initialization:
 *          Uses childFragmentManager to transactionally inject components
 *          once the fragment is attached.
 *      •Cleanup:
 *          Ensures that transient UI states (like error messages or toasts)
 *          are cleared from the ViewModel when the view is destroyed
 *          to prevent stale data on return.
 */
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    override val viewModel: LoginViewModel by viewModels()
    override val layoutId: Int = R.layout.fragment_login
    override val bindingVariable: Int = BR.viewModel

    override fun initializeViews() {
        if (isAdded) {
            setupChildFragments()
        }
    }

    override fun cleanupBinding() {
        // Enforce clean slate policy defined in feature docs
        viewModel.clearToastMessage()
        viewModel.clearError()
    }
    private fun setupChildFragments() {
        childFragmentManager.beginTransaction().apply {
            replace(R.id.email_container, LoginEmailFragment())
            replace(R.id.password_container, LoginPasswordFragment())
            replace(R.id.button_container, LoginButtonFragment())
            commitAllowingStateLoss()
        }
    }

    override fun onLoadingStateChanged(isLoading: Boolean) {
        withBinding { binding ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}