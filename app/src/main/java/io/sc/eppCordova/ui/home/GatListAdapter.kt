package io.sc.eppCordova.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.sc.eppCordova.R
import io.sc.eppCordova.databinding.ItemGatCardBinding
import io.sc.eppCordova.domain.model.GatStatusItem

class GatListAdapter(
    private val onItemClick: (GatStatusItem) -> Unit
) : ListAdapter<GatStatusItem, GatListAdapter.GatViewHolder>(GatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GatViewHolder {
        val binding = ItemGatCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GatViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: GatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class GatViewHolder(
        private val binding: ItemGatCardBinding,
        private val onItemClick: (GatStatusItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GatStatusItem) {
            binding.tvGatNumber.text = "गट क्र. ${item.landRecord?.gutNo ?: "-"}"
            binding.tvGatArea.text = "क्षेत्र: ${item.landRecord?.areaHectares ?: "0"} हेक्टर"

            binding.tvGatStatus.text = item.status
            val ctx = binding.root.context
            when (item.status) {
                "Verified" -> {
                    binding.tvGatStatus.setTextColor(ContextCompat.getColor(ctx, R.color.status_verified))
                    binding.tvGatStatus.setBackgroundResource(R.drawable.bg_badge_verified)
                }
                "Pending" -> {
                    binding.tvGatStatus.setTextColor(ContextCompat.getColor(ctx, R.color.status_pending))
                    binding.tvGatStatus.setBackgroundResource(R.drawable.bg_badge_pending)
                }
                "Draft" -> {
                    binding.tvGatStatus.setTextColor(ContextCompat.getColor(ctx, R.color.outline))
                    binding.tvGatStatus.setBackgroundResource(R.drawable.bg_badge_draft)
                }
                else -> {
                    binding.tvGatStatus.setTextColor(ContextCompat.getColor(ctx, R.color.on_surface_variant))
                }
            }

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    class GatDiffCallback : DiffUtil.ItemCallback<GatStatusItem>() {
        override fun areItemsTheSame(old: GatStatusItem, new: GatStatusItem) =
            old.landRecord?.gutNo == new.landRecord?.gutNo
        override fun areContentsTheSame(old: GatStatusItem, new: GatStatusItem) = old == new
    }
}
