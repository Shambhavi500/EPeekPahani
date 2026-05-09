package io.sc.eppCordova.ui.lossclaim

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.utils.GeoFenceEngine
import io.sc.eppCordova.utils.GeoFenceResult
import io.sc.eppCordova.utils.OfflineBannerHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LossClaimStep2Fragment : Fragment() {

    @Inject
    lateinit var geoFenceEngine: GeoFenceEngine

    @Inject
    lateinit var offlineBannerHelper: OfflineBannerHelper

    private var isGeoFencePassed = false

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                startGeoFenceValidation()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_loss_claim_step2, container, false)
        offlineBannerHelper.attach(view.findViewById(R.id.offline_banner), viewLifecycleOwner)
        
        view.findViewById<Button>(R.id.btn_next).setOnClickListener {
            if (isGeoFencePassed) {
                findNavController().navigate(R.id.action_lossClaimStep2_to_lossClaimStep3)
            }
        }

        checkPermissionsAndStart()
        return view
    }

    private fun checkPermissionsAndStart() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startGeoFenceValidation()
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    private fun startGeoFenceValidation() {
        // Mock polygon JSON or get from SharedViewModel
        val mockPolygonJson = "[{\"lat\": 19.9975, \"lon\": 73.7898}]"
        
        lifecycleScope.launch {
            geoFenceEngine.startValidation(mockPolygonJson).collect { result ->
                updateGeoFenceUI(result)
            }
        }
    }

    private fun updateGeoFenceUI(result: GeoFenceResult) {
        val tvStatus = view?.findViewById<TextView>(R.id.tv_geofence_status)
        val tvDetail = view?.findViewById<TextView>(R.id.tv_geofence_detail)
        val pb = view?.findViewById<ProgressBar>(R.id.pb_geofence)
        val btnQr = view?.findViewById<Button>(R.id.btn_qr_code)
        val btnNext = view?.findViewById<Button>(R.id.btn_next)

        when (result) {
            is GeoFenceResult.Loading -> {
                tvStatus?.text = "GPS शोधत आहे..."
                pb?.visibility = View.VISIBLE
            }
            is GeoFenceResult.Pass -> {
                isGeoFencePassed = true
                tvStatus?.text = "✅ शेतात आहात — पुढे जाऊ शकता"
                tvStatus?.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
                tvDetail?.text = "GPS अचूकता: ±${result.accuracyMetres.toInt()}m"
                pb?.visibility = View.GONE
                btnNext?.isEnabled = true
            }
            is GeoFenceResult.Fail -> {
                isGeoFencePassed = false
                tvStatus?.text = "🔒 आपण शेताबाहेर आहात"
                tvStatus?.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                tvDetail?.text = "अंतर: ~${result.distanceMetres.toInt()} मीटर. कृपया शेतात जा."
                pb?.visibility = View.GONE
                btnNext?.isEnabled = false
            }
            is GeoFenceResult.AccuracyTooLow -> {
                tvStatus?.text = "GPS सिग्नल सुधारत आहे"
                tvDetail?.text = "मोकळ्या जागी जा"
                pb?.visibility = View.VISIBLE
            }
            is GeoFenceResult.GpsUnavailable -> {
                tvStatus?.text = "GPS उपलब्ध नाही"
                pb?.visibility = View.GONE
                btnQr?.visibility = View.VISIBLE
            }
            is GeoFenceResult.MockLocationDetected -> {
                tvStatus?.text = "Mock location आढळले"
                tvStatus?.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                pb?.visibility = View.GONE
                btnNext?.isEnabled = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        geoFenceEngine.stopValidation()
    }
}
