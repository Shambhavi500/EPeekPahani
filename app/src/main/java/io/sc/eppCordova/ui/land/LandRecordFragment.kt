package io.sc.eppCordova.ui.land

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.databinding.FragmentLandRecordBinding
import io.sc.eppCordova.ui.CropFormData
import io.sc.eppCordova.ui.SharedViewModel

@AndroidEntryPoint
class LandRecordFragment : Fragment() {
    private var _binding: FragmentLandRecordBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLandRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.selectedLandRecord.observe(viewLifecycleOwner) { record ->
            binding.tvKhata.text = record.khataNo
            binding.tvGut.text = record.gutNo
            binding.tvArea.text = record.areaHectares.toString()
            binding.tvOwner.text = record.ownerName
        }

        sharedViewModel.selectedAdminUnit.observe(viewLifecycleOwner) { unit ->
            binding.tvVillage.text = unit.village
        }

        binding.btnStart.setOnClickListener {
            val seasonId = binding.rgSeason.checkedRadioButtonId
            if (seasonId == -1) {
                Snackbar.make(view, "कृपया हंगाम निवडा", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val season = if (seasonId == R.id.rbKharif) "खरीप 2025" else "रब्बी 2025"
            
            val formData = sharedViewModel.cropFormData.value ?: CropFormData()
            formData.season = season
            sharedViewModel.cropFormData.value = formData
            
            findNavController().navigate(R.id.action_landRecord_to_cropForm)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
