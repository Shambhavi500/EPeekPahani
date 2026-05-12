package io.sc.eppCordova.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.databinding.FragmentProfileBinding
import io.sc.eppCordova.ui.SharedViewModel

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        sharedViewModel.farmerState.observe(viewLifecycleOwner) { farmer ->
            if (farmer != null) {
                binding.tvFarmerName.text = farmer.name
                binding.tvAvatarInitials.text = farmer.name.take(1)
                binding.tvMobile.text = farmer.mobile
                
                // Populate from CSV data if available
                val village = farmer.village.takeIf { it.isNotEmpty() } ?: "अज्ञात"
                val taluka = farmer.taluka.takeIf { it.isNotEmpty() } ?: "अज्ञात"
                val district = farmer.district.takeIf { it.isNotEmpty() } ?: "अज्ञात"
                binding.tvAddress.text = "$village | $taluka | $district"
                
                if (farmer.aadhaarMasked.isNotEmpty()) {
                    binding.tvAadhaar.text = farmer.aadhaarMasked
                }
                
                binding.tvKhasra.text = farmer.khasraNumber.takeIf { it.isNotEmpty() } ?: "N/A"
                binding.tvLandArea.text = farmer.landHoldingHa.takeIf { it.isNotEmpty() } ?: "N/A"
                binding.tvLandType.text = farmer.landType.takeIf { it.isNotEmpty() } ?: "N/A"
                binding.tvSoilType.text = farmer.soilType.takeIf { it.isNotEmpty() } ?: "N/A"
                binding.tvIrrigation.text = farmer.irrigationSource.takeIf { it.isNotEmpty() } ?: "N/A"
                val crops = listOf(farmer.primaryCrop, farmer.secondaryCrop).filter { it.isNotEmpty() && it != "None" }.joinToString(", ")
                binding.tvCrops.text = crops.takeIf { it.isNotEmpty() } ?: "N/A"
                binding.tvKcc.text = farmer.hasKcc.takeIf { it.isNotEmpty() } ?: "N/A"
            }
        }

        // Set current language text
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        if (!currentLocales.isEmpty) {
            when (currentLocales.get(0)?.language) {
                "hi" -> binding.tvCurrentLanguage.text = "हिंदी"
                "en" -> binding.tvCurrentLanguage.text = "English"
                else -> binding.tvCurrentLanguage.text = "मराठी"
            }
        } else {
            binding.tvCurrentLanguage.text = "मराठी"
        }

        // Language setting click listener
        binding.llLanguage.setOnClickListener {
            findNavController().navigate(R.id.languageFragment)
        }

        binding.btnLogout.setOnClickListener {
            // Mock logout: Reset state and go to login
            sharedViewModel.resetState()
            findNavController().navigate(R.id.action_profile_to_login)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
