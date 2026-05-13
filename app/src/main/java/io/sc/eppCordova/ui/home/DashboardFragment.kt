package io.sc.eppCordova.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.databinding.FragmentDashboardBinding
import io.sc.eppCordova.utils.NetworkUtils
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var adapter: GatListAdapter

    @Inject
    lateinit var networkUtils: NetworkUtils

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOfflineBanner()
        setupRecyclerView()
        setupClickListeners()
        observeData()
    }

    private fun setupOfflineBanner() {
        // Show/hide offline banner based on connectivity
        val isOnline = networkUtils.isOnline.value == true
        binding.layoutOfflineBanner.visibility = if (isOnline) View.GONE else View.VISIBLE
        binding.btnDismissOffline.setOnClickListener {
            binding.layoutOfflineBanner.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        adapter = GatListAdapter { gatItem ->
            val bottomSheet = GatDetailBottomSheet(gatItem.landRecord)
            bottomSheet.show(parentFragmentManager, "GatDetailBottomSheet")
        }
        binding.rvGatNumbers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGatNumbers.adapter = adapter
    }

    private fun setupClickListeners() {
        // Farmer avatar → profile
        binding.ivFarmerAvatar.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_profile)
        }

        // See all gat numbers
        binding.btnSeeAllGat.setOnClickListener {
            // Register idea removed
        }

        // Quick actions


        binding.cardMyCertificate.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_certificate)
        }

        binding.cardLossComplaint.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_lossClaim)
        }
    }

    private fun observeData() {
        viewModel.farmerData.observe(viewLifecycleOwner) { farmer ->
            farmer?.let {
                binding.tvFarmerName.text = it.name
                binding.tvFarmerLocation.text = "नाशिक | निफाड | ओझर"
            }
        }

        viewModel.gatList.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData()
        setupOfflineBanner()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
