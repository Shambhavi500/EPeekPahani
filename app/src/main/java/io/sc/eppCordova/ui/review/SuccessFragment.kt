package io.sc.eppCordova.ui.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.databinding.FragmentSuccessBinding
import io.sc.eppCordova.ui.SharedViewModel
import kotlin.random.Random

@AndroidEntryPoint
class SuccessFragment : Fragment() {
    private var _binding: FragmentSuccessBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val district = sharedViewModel.selectedAdminUnit.value?.district?.uppercase() ?: "PUNE"
        val refNum = "REG-2025-$district-${Random.nextInt(100, 999)}-001"
        binding.tvReferenceId.text = refNum

        val farmer = sharedViewModel.farmerState.value
        val land = sharedViewModel.selectedLandRecord.value
        val crop = sharedViewModel.cropFormData.value

        binding.tvCertFarmerName.text = farmer?.name ?: "शेतकरी"
        binding.tvCertGat.text = "${land?.gutNo}"
        binding.tvCertCrop.text = "${crop?.cropName}"
        binding.tvCertSeason.text = "${crop?.season ?: "खरीप २०२५"}"

        binding.btnNewGatRegistration.setOnClickListener {
            sharedViewModel.resetState()
            // Navigate back to adminUnit (which is now LandSelection in nav_graph)
            findNavController().navigate(R.id.action_success_to_landSelection)
        }

        binding.btnViewCertificate.setOnClickListener {
            findNavController().navigate(R.id.action_success_to_certificate)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
