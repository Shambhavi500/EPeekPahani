package io.sc.eppCordova.lossclaim.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import io.sc.eppCordova.R
import io.sc.eppCordova.databinding.FragmentLossClaimHomeBinding
import io.sc.eppCordova.lossclaim.viewmodel.LossClaimViewModel
import kotlinx.coroutines.launch

class LossClaimHomeFragment : Fragment() {

    private var _binding: FragmentLossClaimHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LossClaimViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLossClaimHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mock load farmer (in reality from SharedViewModel after OTP)
        viewModel.loadFarmerData("9876543210")
        
        // Auto-detect disaster based on weather (PDF says "Pre-populates the disaster type suggestion")
        viewModel.setDamageType("Flood") // Simulating auto-detection

        lifecycleScope.launch {
            viewModel.currentFarmer.collect { farmer ->
                farmer?.let {
                    binding.tvFarmerName.text = "Name: ${it.farmerName}"
                    binding.tvGatCrop.text = "Gat: ${it.gatNumber} | Crop: ${it.crop}"
                    binding.tvInsurance.text = "Insurance: PMFBY Active"
                    binding.tvDisasterType.text = "Suggested Disaster: Flood (Auto-detected)"
                }
            }
        }

        binding.btnStartSurvey.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_camera)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}