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