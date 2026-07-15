/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         BaseFragment.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-07-15
 * ==============================================================================================
 * Description:
 *  A generic base class for [Fragment]s that utilize DataBinding and BaseViewModel.
 * ==============================================================================================
 */
package com.rho.studio.ui.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.rho.studio.ui.core.manager.SessionManager

/**
 * BaseFragment - Base class for all feature fragments
 *
 * A generic base class for [Fragment]s that utilize DataBinding and [BaseViewModel].
 *
 * This base class standardizes the fragment lifecycle, enforces memory leak prevention
 * for view bindings, and provides common hooks for initialization and observation.
 *
 * ### Key Features:
 * 1. **Automated DataBinding:** Inflates the layout and attaches the ViewModel automatically.
 * 2. **Lifecycle Safety:** Manages the backing property for [_binding] to prevent
 *    memory leaks by nulling it out in [onDestroyView].
 * 3. **Session Integration:** Provides lazy access to the global [SessionManager].
 * 4. **Standardized Workflow:** Defines a clear execution order: Binding -> [initializeViews] -> [setupObservers].
 *
 * ### How to implement:
 * ```kotlin
 * class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {
 *     override val viewModel: LoginViewModel by viewModels()
 *     override val layoutId: Int = R.layout.fragment_login
 *     override val bindingVariable: Int = BR.viewModel
 *
 *     override fun initializeViews() {
 *         binding.loginButton.setOnClickListener { ... }
 *     }
 * }
 * ```
 *
 * @param T The specific [ViewDataBinding] class generated for the fragment's layout.
 * @param VM The [ViewModel] class associated with this fragment.
 */
abstract class BaseFragment<T : ViewDataBinding, VM : BaseViewModel> : Fragment() {

    // ==================== ABSTRACT PROPERTIES ====================

    /**It declares a read-only property named viewModel of type VM
     * (the specific ViewModel type provided when the subclass is created).*/
    protected abstract val viewModel: VM

    @get:LayoutRes
    protected abstract val layoutId: Int

    protected abstract val bindingVariable: Int

    // ==================== BINDING WITH MEMORY LEAK PROTECTION ====================

    private var _binding: T? = null

    /**
     * Protected binding property - safe access only between onCreateView and onDestroyView
     * Throws IllegalStateException if accessed outside this window
     */
    protected val binding: T
        get() = _binding ?: throw IllegalStateException(
            "Cannot access binding after onDestroyView or before onCreateView"
        )

    // ==================== OPTIONAL DEPENDENCIES ====================

    /**
     * SessionManager - lazy initialized, only created if accessed
     * Made open so fragments can override if needed
     */
    protected open val sessionManager: SessionManager by lazy {
        SessionManager.getInstance()
    }

    // ==================== LIFECYCLE METHODS ====================

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //backing property
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)

        /** snippet is used in
         * Android Data Binding to connect your layout views to a data source
         * (usually a ViewModel) and ensure the UI reflects changes immediately.
         * */
        with(binding) {
            /**layout uses LiveData, the binding needs a lifecycle owner to observe that data.
             * Without this line, LiveData changes in your ViewModel
             * will not automatically update the UI*/
            lifecycleOwner = viewLifecycleOwner
            setVariable(bindingVariable, viewModel)
            executePendingBindings()  // Immediate UI update
        }

        return binding.root
    }

    /**
     * Set up views and observers in this method
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCommonObservers()
        initializeViews()
        setupObservers()
    }

    /**
     * Critical for memory leak prevention
     */
    final override fun onDestroyView() {
        cleanupBinding()
        super.onDestroyView()
        _binding = null
    }

    /**
     * Sets up automatic observation of common ViewModel LiveData.
     * This eliminates boilerplate in child fragments.
     */
    private fun setupCommonObservers() {
        // Loading state - override onLoadingStateChanged for custom UI
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            onLoadingStateChanged(isLoading)
        }

        // Error messages - automatically shown and cleared
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                onError(it)
                viewModel.clearError()
            }
        }

        // Toast messages - automatically shown and cleared
        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                onToastMessage(it)
                viewModel.clearToastMessage()
            }
        }
    }

    // ==================== EXTENSION POINTS FOR CHILD FRAGMENTS ====================

    /**
     * Called after binding is set up - use for view initialization
     * Examples: setting up RecyclerView, adapters, click listeners
     */
    protected open fun initializeViews() {}

    /**
     * Called after initializeViews - use for LiveData observers
     * Separated from initializeViews for better organization
     */
    protected open fun setupObservers() {}

    /**
     * Optional cleanup method for fragments that need to release resources
     * Called before binding is nulled
     */
    protected open fun cleanupBinding() {
        // Override in child fragments if needed
    }

    // ==================== UTILITY METHODS ====================

    /** Called when loading state changes - override for custom loading UI */
    protected open fun onLoadingStateChanged(isLoading: Boolean) {}

    /** Called when an error occurs - override for custom error handling */
    protected open fun onError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    /** Called for toast messages - override for custom toast behavior */
    protected open fun onToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Check if binding is available (between onCreateView and onDestroyView)
     */
    protected fun isBindingAvailable(): Boolean = _binding != null

    /**
     * Safely execute code that requires binding
     */
    protected fun withBinding(block: (T) -> Unit) {
        _binding?.let(block)
    }
}