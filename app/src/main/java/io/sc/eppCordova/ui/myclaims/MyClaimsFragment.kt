package io.sc.eppCordova.ui.myclaims

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.databinding.FragmentMyClaimsBinding

@AndroidEntryPoint
class MyClaimsFragment : Fragment() {

    private val viewModel: MyClaimsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_claims, container, false)
        
        // Simplified: using RecyclerView directly instead of ViewPager2 for this demo implementation 
        // to avoid creating multiple inner fragments.
        val rv = RecyclerView(requireContext())
        rv.layoutManager = LinearLayoutManager(requireContext())
        
        val ll = view.findViewById<ViewGroup>(R.id.view_pager).parent as ViewGroup
        ll.removeView(view.findViewById(R.id.view_pager))
        ll.addView(rv)
        
        val adapter = ClaimAdapter()
        rv.adapter = adapter
        
        viewModel.lossClaims.observe(viewLifecycleOwner) { claims ->
            adapter.submitList(claims)
        }
        
        return view
    }

    class ClaimAdapter : RecyclerView.Adapter<ClaimAdapter.ViewHolder>() {
        private var list = listOf<io.sc.eppCordova.data.local.entity.LossClaimEntity>()

        fun submitList(newList: List<io.sc.eppCordova.data.local.entity.LossClaimEntity>) {
            list = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_claim_card, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            holder.itemView.findViewById<TextView>(R.id.tv_claim_ref_id).text = item.claimId
            holder.itemView.findViewById<TextView>(R.id.tv_claim_desc).text = "गट: ${item.gatNumber} | ${item.lossType}"
            holder.itemView.findViewById<TextView>(R.id.tv_claim_date).text = "तारीख: ${item.incidentDate}"
        }

        override fun getItemCount() = list.size

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v)
    }
}
