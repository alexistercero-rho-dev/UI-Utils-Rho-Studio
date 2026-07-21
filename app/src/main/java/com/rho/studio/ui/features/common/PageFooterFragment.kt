/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==========================================================================
 * File:         MainActivity.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-07-21
 * ==========================================================================
 * Description:
 *      A reusable footer fragment for pages.
 *      Handles global actions like logout.
 * ==========================================================================
 */
package com.rho.studio.ui.features.common

import androidx.fragment.app.viewModels
import com.rho.studio.ui.BR
import com.rho.studio.ui.R
import com.rho.studio.ui.core.base.BaseFragment
import com.rho.studio.ui.databinding.FragmentPageFooterBinding
import com.rho.studio.ui.features.home.HomeViewModel

class PageFooterFragment : BaseFragment<FragmentPageFooterBinding, HomeViewModel>() {
    override val viewModel: HomeViewModel by viewModels(ownerProducer = { requireParentFragment() })
    override val layoutId: Int = R.layout.fragment_page_footer
    override val bindingVariable: Int = BR.viewModel
}