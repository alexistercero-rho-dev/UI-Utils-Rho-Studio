/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         LoginButtonFragment.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-02-23
 * ==============================================================================================
 *  Description: Manages the primary action button and loading
 *               state for the login flow.
 * ==============================================================================================
 */
package com.rho.studio.ui.features.auth.components

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rho.studio.ui.BR
import com.rho.studio.ui.R
import com.rho.studio.ui.core.base.BaseFragment
import com.rho.studio.ui.databinding.FragmentLoginButtonBinding
import com.rho.studio.ui.features.auth.LoginViewModel
import kotlinx.coroutines.launch

/**
 * LoginEmailFragment - Reusable email input component
 *
 * Feature: auth
 * Purpose: Manages the primary action button and loading
 * state for the login flow.
 *
 * This fragment acts as a reactive component within the Auth module. It synchronizes
 * the button's enabled state with form validation and displays progress indicators
 * during asynchronous login operations.
 *
 * ### Key Behaviors:
 * 1. **Shared State:** Scoped to the parent fragment via [requireParentFragment] to
 *    interact with the shared [LoginViewModel].
 * 2. **Reactive UI:** Automatically enables/disables the login button based on
 *    [LoginViewModel.isFormValid] and [LoginViewModel.isLoading].
 * 3. **Visual Feedback:** Manages the visibility of a progress bar during the
 *    authentication network simulation.
 * 4. **Binding Adapters:** Provides a static [showToast] adapter to allow the XML
 *    layout to reactively trigger system toasts based on ViewModel messages.
 *
 * ### Usage in XML:
 * ```xml
 * <Button
 *     app:toastMessage="@{viewModel.toastMessage}" />
 * ```
 */
class LoginButtonFragment : BaseFragment<FragmentLoginButtonBinding, LoginViewModel>() {

    override val viewModel: LoginViewModel by viewModels(ownerProducer = { requireParentFragment() })
    override val layoutId: Int = R.layout.fragment_login_button
    override val bindingVariable: Int = BR.viewModel

    companion object {
        @JvmStatic
        @BindingAdapter("toastMessage")
        fun showToast(view: View, message: String?) {
            message?.takeIf { it.isNotEmpty() }?.let {
                Toast.makeText(view.context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun initializeViews() {
        setupButton()
    }

    override fun setupObservers() {
        viewModel.isFormValid.observe(viewLifecycleOwner) { isValid ->
            withBinding { binding ->
                binding.loginButton.isEnabled = isValid
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            withBinding { binding ->
                binding.loginButton.isEnabled = !isLoading && (viewModel.isFormValid.value ?: false)
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setupButton() {
        withBinding { binding ->
            binding.loginButton.setOnClickListener {
                if (!binding.loginButton.isEnabled) return@setOnClickListener

                lifecycleScope.launch {
                    viewModel.onLoginClick()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.tag = viewModel
    }
}