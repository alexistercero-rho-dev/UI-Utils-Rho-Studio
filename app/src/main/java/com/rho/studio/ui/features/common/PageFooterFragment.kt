package com.rho.studio.ui.features.common

import androidx.fragment.app.viewModels
import com.rho.studio.ui.BR
import com.rho.studio.ui.R
import com.rho.studio.ui.core.base.BaseFragment
import com.rho.studio.ui.databinding.FragmentPageFooterBinding
import com.rho.studio.ui.features.home.HomeViewModel

/**
 * A reusable footer fragment for pages.
 * Handles global actions like logout.
 */
class PageFooterFragment : BaseFragment<FragmentPageFooterBinding, HomeViewModel>() {
    override val viewModel: HomeViewModel by viewModels(ownerProducer = { requireParentFragment() })
    override val layoutId: Int = R.layout.fragment_page_footer
    override val bindingVariable: Int = BR.viewModel
}