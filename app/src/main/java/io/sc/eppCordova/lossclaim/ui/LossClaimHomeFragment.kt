package io.sc.eppCordova.lossclaim.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
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

        // Mock load farmer
        viewModel.loadFarmerData("9876543210")

        lifecycleScope.launch {
            viewModel.currentFarmer.collect { farmer ->
                farmer?.let {
                    val gatInfo = listOf("Gat ${it.gatNumber} - ${it.crop} - ${it.area}")
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, gatInfo)
                    binding.spinnerGat.adapter = adapter
                }
            }
        }

        binding.cardFlood.setOnClickListener {
            viewModel.setDamageType("Flood")
            binding.cardFlood.strokeWidth = 4
            binding.cardDrought.strokeWidth = 0
        }

        binding.cardDrought.setOnClickListener {
            viewModel.setDamageType("Drought")
            binding.cardDrought.strokeWidth = 4
            binding.cardFlood.strokeWidth = 0
        }

        binding.btnStartSurvey.setOnClickListener {
            if (viewModel.selectedDamageType.value.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a damage type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_home_to_camera)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}