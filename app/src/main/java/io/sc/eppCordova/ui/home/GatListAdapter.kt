package io.sc.eppCordova.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import io.sc.eppCordova.R
import io.sc.eppCordova.domain.model.GatStatusItem

class GatListAdapter(
    private val onItemClick: (GatStatusItem) -> Unit
) : RecyclerView.Adapter<GatListAdapter.GatViewHolder>() {

    private var items: List<GatStatusItem> = emptyList()

    fun submitList(newItems: List<GatStatusItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gat_card, parent, false)
        return GatViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: GatViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class GatViewHolder(
        itemView: View,
        private val onItemClick: (GatStatusItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val cardView: MaterialCardView = itemView.findViewById(R.id.card_gat)
        private val tvGatNumber: TextView = itemView.findViewById(R.id.tv_gat_number)
        private val tvVillageArea: TextView = itemView.findViewById(R.id.tv_village_area)
        private val chipStatus: Chip = itemView.findViewById(R.id.chip_status)

        fun bind(item: GatStatusItem) {
            tvGatNumber.text = item.landRecord.gutNo
            tvVillageArea.text = "${item.landRecord.areaHectares} Ha."
            
            chipStatus.text = item.status
            when (item.status) {
                "Pending" -> chipStatus.setChipBackgroundColorResource(android.R.color.darker_gray)
                "Draft" -> chipStatus.setChipBackgroundColorResource(android.R.color.holo_orange_light)
                "Submitted" -> chipStatus.setChipBackgroundColorResource(android.R.color.holo_blue_light)
                "Verified" -> chipStatus.setChipBackgroundColorResource(android.R.color.holo_green_dark)
            }
            
            cardView.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}
