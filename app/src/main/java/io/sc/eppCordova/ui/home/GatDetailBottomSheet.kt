package io.sc.eppCordova.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.sc.eppCordova.R
import io.sc.eppCordova.data.local.entity.LandRecord

class GatDetailBottomSheet(
    private val landRecord: LandRecord
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_gat_detail, container, false)
        
        view.findViewById<TextView>(R.id.tv_detail_gat_number).text = "गट क्रमांक: ${landRecord.gutNo}"
        view.findViewById<TextView>(R.id.tv_detail_area).text = "क्षेत्र: ${landRecord.areaHectares} Ha."
        view.findViewById<TextView>(R.id.tv_detail_owner).text = "खातेदार: ${landRecord.ownerName}"
        
        view.findViewById<Button>(R.id.btn_register_crop).setOnClickListener {
            dismiss()
            findNavController().navigate(R.id.action_dashboard_to_adminUnit)
        }
        
        return view
    }
}
