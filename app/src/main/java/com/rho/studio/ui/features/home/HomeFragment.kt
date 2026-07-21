/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         HomeFragment.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-07-16
 * ==============================================================================================
 * Description: The main dashboard fragment that users land on after successful authentication.
 *              Displays module shortcuts and handles global session termination.
 * ==============================================================================================
 */
package com.rho.studio.ui.features.home

import android.widget.Toast
import androidx.fragment.app.viewModels
import com.rho.studio.ui.BR
import com.rho.studio.ui.R
import com.rho.studio.ui.core.base.BaseFragment
import com.rho.studio.ui.databinding.FragmentHomeBinding
import com.rho.studio.ui.features.home.components.HomeServicesFragment
import com.rho.studio.ui.features.common.PageFooterFragment
import com.rho.studio.ui.features.common.PageHeaderFragment

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override val viewModel: HomeViewModel by viewModels()
    override val layoutId: Int = R.layout.fragment_home
    override val bindingVariable: Int = BR.viewModel

    override fun initializeViews() {
        if (isAdded) {
            setupChildFragments()
        }
    }

    override fun setupObservers() {
        viewModel.navigateToService.observe(viewLifecycleOwner) { serviceId ->
            serviceId?.let {
                Toast.makeText(context, "Navigating to: $it", Toast.LENGTH_SHORT).show()
                viewModel.onServiceNavigated()
            }
        }
    }

    private fun setupChildFragments() {
        childFragmentManager.beginTransaction().apply {
            replace(R.id.header_container, PageHeaderFragment.newInstance(getString(R.string.home_title)))
            replace(R.id.services_container, HomeServicesFragment())
            replace(R.id.footer_container, PageFooterFragment())
            commitAllowingStateLoss()
        }
    }
}
