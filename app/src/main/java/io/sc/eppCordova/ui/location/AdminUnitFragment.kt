package io.sc.eppCordova.ui.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.data.local.entity.AdminUnit
import io.sc.eppCordova.databinding.FragmentAdminUnitBinding
import io.sc.eppCordova.ui.SharedViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminUnitFragment : Fragment() {
    private var _binding: FragmentAdminUnitBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminUnitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            val divisions = sharedViewModel.getDivisions()
            setupDropdown(binding.acDivision, divisions)
        }

        binding.acDivision.setOnItemClickListener { _, _, position, _ ->
            val div = binding.acDivision.adapter.getItem(position) as String
            viewLifecycleOwner.lifecycleScope.launch {
                val districts = sharedViewModel.getDistricts(div)
                setupDropdown(binding.acDistrict, districts)
                binding.acDistrict.text = null
                binding.acTaluka.text = null
                binding.acVillage.text = null
            }
        }

        binding.acDistrict.setOnItemClickListener { _, _, position, _ ->
            val dist = binding.acDistrict.adapter.getItem(position) as String
            viewLifecycleOwner.lifecycleScope.launch {
                val talukas = sharedViewModel.getTalukas(dist)
                setupDropdown(binding.acTaluka, talukas)
                binding.acTaluka.text = null
                binding.acVillage.text = null
            }
        }

        binding.acTaluka.setOnItemClickListener { _, _, position, _ ->
            val taluka = binding.acTaluka.adapter.getItem(position) as String
            viewLifecycleOwner.lifecycleScope.launch {
                val villages = sharedViewModel.getVillages(taluka)
                setupDropdown(binding.acVillage, villages)
                binding.acVillage.text = null
            }
        }

        binding.btnNext.setOnClickListener {
            val div = binding.acDivision.text.toString()
            val dist = binding.acDistrict.text.toString()
            val tal = binding.acTaluka.text.toString()
            val vil = binding.acVillage.text.toString()

            if (div.isEmpty() || dist.isEmpty() || tal.isEmpty() || vil.isEmpty()) {
                Snackbar.make(view, "कृपया सर्व माहिती निवडा", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val unit = AdminUnit(division = div, district = dist, taluka = tal, village = vil)
            sharedViewModel.setAdminUnit(unit)
            findNavController().navigate(R.id.action_adminUnit_to_parcel)
        }
    }

    private fun setupDropdown(view: android.widget.AutoCompleteTextView, items: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        view.setAdapter(adapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
