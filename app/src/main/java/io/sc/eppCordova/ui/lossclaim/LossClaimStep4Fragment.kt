package io.sc.eppCordova.ui.lossclaim

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.databinding.FragmentLossClaimStep4Binding
import io.sc.eppCordova.utils.OfflineBannerHelper
import javax.inject.Inject

@AndroidEntryPoint
class LossClaimStep4Fragment : Fragment() {

    private var _binding: FragmentLossClaimStep4Binding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var offlineBannerHelper: OfflineBannerHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLossClaimStep4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        offlineBannerHelper.attach(binding.offlineBanner.root, viewLifecycleOwner)
        
        binding.cbConsent.setOnCheckedChangeListener { _, isChecked ->
            binding.btnSubmit.isEnabled = isChecked
        }

        binding.btnSubmit.setOnClickListener {
            findNavController().navigate(R.id.action_lossClaimStep4_to_lossClaimStep5)
        }
        
        binding.btnSaveDraft.setOnClickListener {
            findNavController().navigate(R.id.action_lossClaimStep4_to_dashboardFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
