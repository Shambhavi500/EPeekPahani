package io.sc.eppCordova.ui.lossclaim

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.databinding.FragmentLossClaimStep5Binding
import io.sc.eppCordova.utils.NetworkUtils
import javax.inject.Inject

@AndroidEntryPoint
class LossClaimStep5Fragment : Fragment() {

    private var _binding: FragmentLossClaimStep5Binding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var networkUtils: NetworkUtils

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLossClaimStep5Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val isOnline = networkUtils.isOnline.value == true
        
        if (isOnline) {
            binding.tvStatusTitle.text = "दावा यशस्वीरित्या सादर झाला! ✅"
            binding.tvStatusTitle.setTextColor(requireContext().getColor(android.R.color.holo_green_dark))
            binding.tvStatusSubtitle.text = "आपला दावा प्रक्रियेत आहे."
        }

        binding.btnViewClaims.setOnClickListener {
            findNavController().navigate(R.id.action_lossClaimStep5_to_myClaims)
        }
        
        binding.btnHome.setOnClickListener {
            findNavController().navigate(R.id.action_lossClaimStep5_to_dashboardFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
