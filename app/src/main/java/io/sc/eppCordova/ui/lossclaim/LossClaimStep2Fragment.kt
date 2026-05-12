package io.sc.eppCordova.ui.lossclaim

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.databinding.FragmentLossClaimStep2Binding
import io.sc.eppCordova.utils.GeoFenceEngine
import io.sc.eppCordova.utils.GeoFenceResult
import io.sc.eppCordova.utils.OfflineBannerHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LossClaimStep2Fragment : Fragment() {

    private var _binding: FragmentLossClaimStep2Binding? = null
    private val binding get() = _binding!!

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
    ): View {
        _binding = FragmentLossClaimStep2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnNext.setOnClickListener {
            if (isGeoFencePassed) {
                findNavController().navigate(R.id.action_lossClaimStep2_to_lossClaimStep3)
            }
        }

        checkPermissionsAndStart()
    }

    private fun checkPermissionsAndStart() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startGeoFenceValidation()
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    private fun startGeoFenceValidation() {
        val mockPolygonJson = "[{\"lat\": 19.9975, \"lon\": 73.7898}]"
        
        lifecycleScope.launch {
            geoFenceEngine.startValidation(mockPolygonJson).collect { result ->
                updateGeoFenceUI(result)
            }
        }
    }

    private fun updateGeoFenceUI(result: GeoFenceResult) {
        when (result) {
            is GeoFenceResult.Loading -> {
                binding.tvGeofenceStatus.text = "GPS शोधत आहे..."
                binding.pbGeofence.visibility = View.VISIBLE
            }
            is GeoFenceResult.Pass -> {
                isGeoFencePassed = true
                binding.tvGeofenceStatus.text = "✅ शेतात आहात — पुढे जाऊ शकता"
                binding.tvGeofenceStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
                binding.tvGeofenceDetail.text = "GPS अचूकता: ±${result.accuracyMetres.toInt()}m"
                binding.pbGeofence.visibility = View.GONE
                binding.btnNext.isEnabled = true
            }
            is GeoFenceResult.Fail -> {
                isGeoFencePassed = false
                binding.tvGeofenceStatus.text = "🔒 आपण शेताबाहेर आहात"
                binding.tvGeofenceStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                binding.tvGeofenceDetail.text = "अंतर: ~${result.distanceMetres.toInt()} मीटर. कृपया शेतात जा."
                binding.pbGeofence.visibility = View.GONE
                binding.btnNext.isEnabled = false
            }
            is GeoFenceResult.AccuracyTooLow -> {
                binding.tvGeofenceStatus.text = "GPS सिग्नल सुधारत आहे"
                binding.tvGeofenceDetail.text = "मोकळ्या जागी जा"
                binding.pbGeofence.visibility = View.VISIBLE
            }
            is GeoFenceResult.GpsUnavailable -> {
                binding.tvGeofenceStatus.text = "GPS उपलब्ध नाही"
                binding.pbGeofence.visibility = View.GONE
                binding.btnQrCode.visibility = View.VISIBLE
            }
            is GeoFenceResult.MockLocationDetected -> {
                binding.tvGeofenceStatus.text = "Mock location आढळले"
                binding.tvGeofenceStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                binding.pbGeofence.visibility = View.GONE
                binding.btnNext.isEnabled = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        geoFenceEngine.stopValidation()
        _binding = null
    }
}
