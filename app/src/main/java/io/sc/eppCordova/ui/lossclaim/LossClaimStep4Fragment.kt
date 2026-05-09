package io.sc.eppCordova.ui.lossclaim

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.utils.OfflineBannerHelper
import javax.inject.Inject

@AndroidEntryPoint
class LossClaimStep4Fragment : Fragment() {

    @Inject
    lateinit var offlineBannerHelper: OfflineBannerHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_loss_claim_step4, container, false)
        offlineBannerHelper.attach(view.findViewById(R.id.offline_banner), viewLifecycleOwner)
        
        val cbConsent = view.findViewById<CheckBox>(R.id.cb_consent)
        val btnSubmit = view.findViewById<Button>(R.id.btn_submit)
        
        cbConsent.setOnCheckedChangeListener { _, isChecked ->
            btnSubmit.isEnabled = isChecked
        }

        btnSubmit.setOnClickListener {
            // Store loss claim in DB with isSubmitted=false (or true if online success)
            findNavController().navigate(R.id.action_lossClaimStep4_to_lossClaimStep5)
        }
        
        view.findViewById<Button>(R.id.btn_save_draft).setOnClickListener {
            // Save as draft and go back to dashboard
            findNavController().navigate(R.id.action_lossClaimStep4_to_dashboardFragment)
        }

        return view
    }
}
