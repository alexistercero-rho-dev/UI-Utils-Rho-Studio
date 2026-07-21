/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==========================================================================
 * File:         HomeServicesFragment.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-07-21
 * ==========================================================================
 * Description:
 *      A sub-fragment of the Home feature that implements an MVVM pattern
 *      to display a grid of services. It utilizes a shared HomeViewModel
 *      to observe service data and populates a RecyclerView via
 *      ServiceAdapter. This component leverages ViewBinding and
 *      GridLayoutManager to provide a responsive 2-column layout
 *      representative of the platform's core offerings.
 * ==========================================================================
 */
package com.rho.studio.ui.features.home.components

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.rho.studio.ui.BR
import com.rho.studio.ui.R
import com.rho.studio.ui.core.base.BaseFragment
import com.rho.studio.ui.databinding.FragmentHomeServicesBinding
import com.rho.studio.ui.features.home.HomeViewModel
import com.rho.studio.ui.features.home.ServiceAdapter

class HomeServicesFragment : BaseFragment<FragmentHomeServicesBinding, HomeViewModel>() {

    override val viewModel: HomeViewModel by viewModels(ownerProducer = { requireParentFragment() })
    override val layoutId: Int = R.layout.fragment_home_services
    override val bindingVariable: Int = BR.viewModel

    private lateinit var serviceAdapter: ServiceAdapter

    override fun initializeViews() {
        setupRecyclerView()
    }

    override fun setupObservers() {
        viewModel.services.observe(viewLifecycleOwner) { services ->
            serviceAdapter.submitList(services)
        }
    }

    private fun setupRecyclerView() {
        serviceAdapter = ServiceAdapter(viewModel)
        withBinding { binding ->
            binding.servicesRecyclerView.apply {
                adapter = serviceAdapter
                layoutManager = GridLayoutManager(context, 2)
                setHasFixedSize(true)
            }
        }
    }
}