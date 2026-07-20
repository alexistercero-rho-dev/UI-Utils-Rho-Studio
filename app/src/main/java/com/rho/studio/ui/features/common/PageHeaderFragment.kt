package com.rho.studio.ui.features.common

import androidx.fragment.app.viewModels
import com.rho.studio.ui.BR
import com.rho.studio.ui.R
import com.rho.studio.ui.core.base.BaseFragment
import com.rho.studio.ui.databinding.FragmentPageHeaderBinding
import com.rho.studio.ui.features.home.HomeViewModel

/**
 * A reusable header fragment for pages.
 * NOTE: Currently uses HomeViewModel, but can be refactored to use a SharedViewModel
 * or SessionManager directly for true feature-agnostic behavior.
 */
class PageHeaderFragment : BaseFragment<FragmentPageHeaderBinding, HomeViewModel>() {
    override val viewModel: HomeViewModel by viewModels(ownerProducer = { requireParentFragment() })
    override val layoutId: Int = R.layout.fragment_page_header
    override val bindingVariable: Int = BR.viewModel
}