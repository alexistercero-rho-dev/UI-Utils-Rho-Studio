/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         LoginEmailFragment.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-02-23
 * ==============================================================================================
 *  Description: Handles email input with two-way binding
 * ==============================================================================================
 */
package com.rho.studio.ui.features.auth.components

import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rho.studio.ui.BR
import com.rho.studio.ui.R
import com.rho.studio.ui.core.base.BaseFragment
import com.rho.studio.ui.databinding.FragmentLoginEmailBinding
import com.rho.studio.ui.features.auth.LoginViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
/**
 * LoginEmailFragment - Reusable email input component
 *
 * Feature: auth
 * Purpose: Handles email input with two-way binding
 *
 * ### Key Behaviors:
 * 1. **Shared State:** Utilizes [requireParentFragment] as the ViewModel owner to
 *    synchronize data with the main login flow.
 * 2. **Input Debouncing:** Implements a 300ms delay on text changes to prevent
 *    excessive validation calls and UI flickering while the user is typing.
 * 3. **Error Feedback:** Observes the [LoginViewModel.emailError] to provide
 *    real-time visual feedback via [com.google.android.material.textfield.TextInputLayout].
 *
 * ### UI Components:
 * - Uses [FragmentLoginEmailBinding] for direct access to input views.
 * - Managed within the `auth` feature module.
 */
class LoginEmailFragment : BaseFragment<FragmentLoginEmailBinding, LoginViewModel>() {

    override val viewModel: LoginViewModel by viewModels(ownerProducer = { requireParentFragment() })
    override val layoutId: Int = R.layout.fragment_login_email
    override val bindingVariable: Int = BR.viewModel

    private var debounceJob: Job? = null

    override fun initializeViews() {
        setupEmailInput()
    }

    override fun setupObservers() {
        viewModel.emailError.observe(viewLifecycleOwner) { error ->
            withBinding { binding ->
                binding.textInputLayout.error = error
            }
        }
    }

    private fun setupEmailInput() {
        withBinding { binding ->
            binding.emailInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    debounceJob?.cancel()
                    debounceJob = lifecycleScope.launch {
                        delay(300)
                        viewModel.onEmailChanged(s?.toString() ?: "")
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    override fun cleanupBinding() {
        debounceJob?.cancel()
        debounceJob = null
    }
}