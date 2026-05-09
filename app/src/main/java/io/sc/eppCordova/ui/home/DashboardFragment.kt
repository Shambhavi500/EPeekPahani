package io.sc.eppCordova.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.utils.OfflineBannerHelper
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var adapter: GatListAdapter
    
    @Inject
    lateinit var offlineBannerHelper: OfflineBannerHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        
        offlineBannerHelper.attach(view.findViewById(R.id.offline_banner), viewLifecycleOwner)
        
        val rvGats = view.findViewById<RecyclerView>(R.id.rv_gats)
        adapter = GatListAdapter { gatItem ->
            val bottomSheet = GatDetailBottomSheet(gatItem.landRecord)
            bottomSheet.show(parentFragmentManager, "GatDetailBottomSheet")
        }
        rvGats.adapter = adapter
        
        viewModel.farmerData.observe(viewLifecycleOwner) { farmer ->
            farmer?.let {
                view.findViewById<TextView>(R.id.tv_farmer_name).text = it.name
                // Assuming district/taluka/village is available via another entity or hardcoded
                view.findViewById<TextView>(R.id.tv_location).text = "नाशिक | निफाड | ओझर" 
            }
        }
        
        view.findViewById<View>(R.id.iv_farmer_avatar).setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_profile)
        }
        
        viewModel.gatList.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            view.findViewById<TextView>(R.id.tv_gat_count).text = items.size.toString()
        }
        
        view.findViewById<View>(R.id.card_new_crop).setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_adminUnit)
        }
        
        view.findViewById<View>(R.id.card_certificate).setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_certificate)
        }
        
        view.findViewById<View>(R.id.card_claim).setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_lossClaim)
        }
        
        return view
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }
}
