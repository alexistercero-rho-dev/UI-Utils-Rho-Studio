/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==========================================================================
 * File:         PageHeaderFragment.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-07-21
 * ==========================================================================
 * Description:
 *      A reusable header fragment for pages.
 *      Uses HeaderViewModel to source user data and accepts a title argument.
 * ==========================================================================
 */
package com.rho.studio.ui.features.common

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.rho.studio.ui.BR
import com.rho.studio.ui.R
import com.rho.studio.ui.core.base.BaseFragment
import com.rho.studio.ui.databinding.FragmentPageHeaderBinding

/**
 * A reusable header fragment for pages.
 * Uses HeaderViewModel to source user data and accepts a title argument.
 */
class PageHeaderFragment : BaseFragment<FragmentPageHeaderBinding, HeaderViewModel>() {
    override val viewModel: HeaderViewModel by viewModels()
    override val layoutId: Int = R.layout.fragment_page_header
    override val bindingVariable: Int = BR.viewModel

    companion object {
        private const val ARG_TITLE = "arg_title"

        fun newInstance(title: String): PageHeaderFragment {
            val fragment = PageHeaderFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            fragment.arguments = args
            return fragment
        }
    }

    override fun initializeViews() {
        arguments?.getString(ARG_TITLE)?.let {
            viewModel.setTitle(it)
        }
    }
}