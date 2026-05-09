package io.sc.eppCordova.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                binding.tvAddress.text = "नाशिक | निफाड | ओझर"
            }
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
