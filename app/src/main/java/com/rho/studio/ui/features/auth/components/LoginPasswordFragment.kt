/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         LoginPasswordFragment.kt
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
import com.rho.studio.ui.databinding.FragmentLoginPasswordBinding
import com.rho.studio.ui.features.auth.LoginViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
/**
 * LoginPasswordFragment - Reusable password input component
 *
 * Feature: auth
 * Purpose: Handles password input with two-way binding
 *
 * This fragment provides a secure input interface and communicates with the shared
 * [LoginViewModel] to validate password complexity requirements in real-time.
 *
 * ### Key Behaviors:
 * 1. **Shared ViewModel:** Scoped to the parent fragment via [requireParentFragment]
 *    to ensure password data is synchronized with email and login action components.
 * 2. **Input Debouncing:** Utilizes a [Job] with a 300ms delay on text changes
 *    to optimize performance and prevent UI "stuttering" while the user is typing.
 * 3. **Validation Feedback:** Observes [LoginViewModel.passwordError] to update
 *    the [com.google.android.material.textfield.TextInputLayout] error state.
 *
 * ### UI Components:
 * - Uses [FragmentLoginPasswordBinding] to manage the sensitive input field.
 * - Part of the modular `auth` feature components.
 */
class LoginPasswordFragment : BaseFragment<FragmentLoginPasswordBinding, LoginViewModel>() {

    override val viewModel: LoginViewModel by viewModels(ownerProducer = { requireParentFragment() })
    override val layoutId: Int = R.layout.fragment_login_password
    override val bindingVariable: Int = BR.viewModel

    private var debounceJob: Job? = null

    override fun initializeViews() {
        setupPasswordInput()
    }

    override fun setupObservers() {
        viewModel.passwordError.observe(viewLifecycleOwner) { error ->
            withBinding { binding ->
                binding.textInputLayout.error = error
            }
        }
    }

    private fun setupPasswordInput() {
        withBinding { binding ->
            binding.passwordInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    debounceJob?.cancel()
                    debounceJob = lifecycleScope.launch {
                        delay(300)
                        viewModel.onPasswordChanged(s?.toString() ?: "")
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