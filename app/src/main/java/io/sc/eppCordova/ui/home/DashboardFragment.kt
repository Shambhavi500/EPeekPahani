package io.sc.eppCordova.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
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
        setupClickListeners()
        observeData()
    }

    private fun setupOfflineBanner() {
        val isOnline = networkUtils.isOnline.value == true
        binding.layoutOfflineBanner.visibility = if (isOnline) View.GONE else View.VISIBLE
        binding.btnDismissOffline.setOnClickListener {
            binding.layoutOfflineBanner.visibility = View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.cardFarmerInfo.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_profile)
        }
    }

    private fun observeData() {
        viewModel.farmerData.observe(viewLifecycleOwner) { farmer ->
            farmer?.let {
                binding.tvFarmerName.text = it.name
                binding.tvFarmerVillage.text = it.village
                binding.tvFarmerTaluka.text = it.taluka
                binding.tvFarmerDistrict.text = it.district
                binding.tvFarmerState.text = it.state
                binding.tvFarmerPincode.text = it.pincode
            }
        }

        viewModel.cropDetail.observe(viewLifecycleOwner) { crop ->
            binding.tvCropMainCrop.text = crop.mainCrop.ifBlank { "--" }
            binding.tvCropSeason.text = crop.season.ifBlank { "--" }
            binding.tvCropIrrigation.text = crop.irrigation.ifBlank { "--" }
            binding.tvCropArea.text = crop.area.ifBlank { "--" }
        }

        viewModel.schemes.observe(viewLifecycleOwner) { schemes ->
            binding.chipGroupSchemes.removeAllViews()
            if (schemes.isEmpty()) {
                binding.tvSchemesEmpty.isVisible = true
            } else {
                binding.tvSchemesEmpty.isVisible = false
                schemes.forEach { scheme ->
                    val chip = Chip(requireContext()).apply {
                        text = scheme
                        isClickable = false
                        isCheckable = false
                        setChipBackgroundColorResource(R.color.primary_container)
                        setTextColor(resources.getColor(R.color.on_primary, null))
                        chipCornerRadius = resources.getDimension(R.dimen.card_corner_full)
                    }
                    binding.chipGroupSchemes.addView(chip)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData()
        setupOfflineBanner()
        requireActivity().title = getString(R.string.str_dashboard_title)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
