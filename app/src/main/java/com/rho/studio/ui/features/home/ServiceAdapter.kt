/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==========================================================================
 * File:         ServiceAdapter.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-07-21
 * ==========================================================================
 * Description:
 *      RecyclerView adapter responsible for managing and displaying ServiceModule
 *      items within the Home feature. Utilizes ListAdapter with DiffUtil for
 *      optimized list updates and Data Binding to link UI components with the
 *      HomeViewModel.
 * ==========================================================================
 */
package com.rho.studio.ui.features.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rho.studio.ui.core.model.ServiceModule
import com.rho.studio.ui.databinding.ItemServiceBinding

class ServiceAdapter(private val viewModel: HomeViewModel) :
    ListAdapter<ServiceModule, ServiceAdapter.ServiceViewHolder>(ServiceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemServiceBinding.inflate(layoutInflater, parent, false)
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(getItem(position), viewModel)
    }

    class ServiceViewHolder(private val binding: ItemServiceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(service: ServiceModule, viewModel: HomeViewModel) {
            binding.service = service
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

    class ServiceDiffCallback : DiffUtil.ItemCallback<ServiceModule>() {
        override fun areItemsTheSame(oldItem: ServiceModule, newItem: ServiceModule): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ServiceModule, newItem: ServiceModule): Boolean {
            return oldItem == newItem
        }
    }
}
