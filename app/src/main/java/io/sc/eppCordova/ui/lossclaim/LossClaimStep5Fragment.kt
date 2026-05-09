package io.sc.eppCordova.ui.lossclaim

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.utils.NetworkUtils
import javax.inject.Inject

@AndroidEntryPoint
class LossClaimStep5Fragment : Fragment() {

    @Inject
    lateinit var networkUtils: NetworkUtils

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_loss_claim_step5, container, false)
        
        val isOnline = networkUtils.isOnline.value == true
        
        val tvIcon = view.findViewById<TextView>(R.id.tv_success_icon)
        val tvTitle = view.findViewById<TextView>(R.id.tv_status_title)
        val tvSubtitle = view.findViewById<TextView>(R.id.tv_status_subtitle)

        if (isOnline) {
            // Can use vector drawable checkmark here
            tvTitle.text = "दावा यशस्वीरित्या सादर झाला! ✅"
            tvTitle.setTextColor(requireContext().getColor(android.R.color.holo_green_dark))
            tvSubtitle.text = "आपला दावा प्रक्रियेत आहे."
        }

        view.findViewById<Button>(R.id.btn_view_claims).setOnClickListener {
            findNavController().navigate(R.id.action_lossClaimStep5_to_myClaims)
        }
        
        view.findViewById<Button>(R.id.btn_home).setOnClickListener {
            findNavController().navigate(R.id.action_lossClaimStep5_to_dashboardFragment)
        }

        return view
    }
}
