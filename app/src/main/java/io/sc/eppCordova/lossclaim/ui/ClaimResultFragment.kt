package io.sc.eppCordova.lossclaim.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import io.sc.eppCordova.databinding.FragmentClaimResultBinding
import io.sc.eppCordova.lossclaim.viewmodel.LossClaimViewModel

class ClaimResultFragment : Fragment() {

    private var _binding: FragmentClaimResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LossClaimViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClaimResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mock AI output based on damage type
        val damagePercentage = if (viewModel.selectedDamageType.value == "Flood") 63 else 45
        val compensation = if (damagePercentage > 50) 37800.0 else 15000.0

        binding.tvDamagePercent.text = "$damagePercentage%"
        binding.tvCompensation.text = "₹$compensation"

        binding.btnSubmitClaim.setOnClickListener {
            viewModel.submitClaim(damagePercentage, compensation, "mock_path.jpg")
            Toast.makeText(requireContext(), "Claim Submitted Successfully!", Toast.LENGTH_LONG).show()
            // Navigate to success/dashboard
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}