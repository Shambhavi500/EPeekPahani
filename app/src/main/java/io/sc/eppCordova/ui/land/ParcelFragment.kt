package io.sc.eppCordova.ui.land

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.data.local.entity.LandRecord
import io.sc.eppCordova.databinding.FragmentParcelBinding
import io.sc.eppCordova.databinding.ItemParcelCardBinding
import io.sc.eppCordova.ui.SharedViewModel

@AndroidEntryPoint
class ParcelFragment : Fragment() {
    private var _binding: FragmentParcelBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    // Dummy records
    private val dummyRecords = listOf(
        LandRecord("87", "142", "राजेश विठ्ठल पाटील", 1.20, 1),
        LandRecord("88", "142", "राजेश विठ्ठल पाटील", 0.80, 1),
        LandRecord("92", "145", "सुनिता विठ्ठल पाटील", 2.50, 1),
        LandRecord("105", "180", "अशोक कुमार", 1.00, 1)
    )

    private var selectedRecord: LandRecord? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentParcelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvParcels.layoutManager = LinearLayoutManager(requireContext())
        binding.rvParcels.adapter = ParcelAdapter(dummyRecords) { record ->
            selectedRecord = record
            binding.rvParcels.adapter?.notifyDataSetChanged()
            sharedViewModel.setLandRecord(record)
            findNavController().navigate(R.id.action_parcel_to_landRecord)
        }

        // binding.btnSelect.setOnClickListener {
        //     selectedRecord?.let {
        //         sharedViewModel.setLandRecord(it)
        //         findNavController().navigate(R.id.action_parcel_to_landRecord)
        //     }
        // }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class ParcelAdapter(
        private val records: List<LandRecord>,
        private val onClick: (LandRecord) -> Unit
    ) : RecyclerView.Adapter<ParcelAdapter.ViewHolder>() {

        inner class ViewHolder(val binding: ItemParcelCardBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemParcelCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val record = records[position]
            holder.binding.tvKhataArea.text = "खाता क्र: ${record.khataNo} | ${record.areaHectares} Ha."
            holder.binding.tvBadgeGut.text = record.gutNo
            holder.binding.tvOwnerName.text = record.ownerName

            if (record == selectedRecord) {
                holder.binding.cardParcel.setCardBackgroundColor(Color.parseColor("#E8F5E9"))
            } else {
                holder.binding.cardParcel.setCardBackgroundColor(Color.WHITE)
            }

            holder.binding.cardParcel.setOnClickListener { onClick(record) }
            holder.binding.btnSelect.setOnClickListener { onClick(record) }
        }

        override fun getItemCount() = records.size
    }
}
