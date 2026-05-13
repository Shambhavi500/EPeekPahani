package io.sc.eppCordova.ui.land

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.data.local.entity.LandRecord
import io.sc.eppCordova.databinding.FragmentLandSelectionBinding
import io.sc.eppCordova.domain.model.GatStatusItem
import io.sc.eppCordova.ui.SharedViewModel
import io.sc.eppCordova.ui.home.GatListAdapter

@AndroidEntryPoint
class ParcelFragment : Fragment() {
    private var _binding: FragmentLandSelectionBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    // Dummy records
    private val dummyRecords = listOf(
        GatStatusItem(LandRecord("87", "142", "राजेश विठ्ठल पाटील", 1.20, 1), "Draft"),
        GatStatusItem(LandRecord("88", "142", "राजेश विठ्ठल पाटील", 0.80, 1), "Pending"),
        GatStatusItem(LandRecord("92", "145", "सुनिता विठ्ठल पाटील", 2.50, 1), "Verified")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLandSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = GatListAdapter { item ->
            sharedViewModel.setLandRecord(item.landRecord)
        }
        binding.rvGatSelection.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGatSelection.adapter = adapter
        adapter.submitList(dummyRecords)

        binding.btnStep1Next.setOnClickListener {
            // Move to step 2
            findNavController().navigate(R.id.action_landSelection_to_cropForm)
        }
        
        binding.btnStep1Prev.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
