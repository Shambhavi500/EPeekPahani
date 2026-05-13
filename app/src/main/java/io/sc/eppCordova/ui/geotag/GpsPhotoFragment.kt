package io.sc.eppCordova.ui.geotag

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.databinding.FragmentGpsPhotoBinding
import io.sc.eppCordova.ui.SharedViewModel
import io.sc.eppCordova.utils.GeoFenceEngine
import io.sc.eppCordova.utils.GeoFenceResult
import io.sc.eppCordova.utils.MatchStatus
import io.sc.eppCordova.utils.TFLiteCropDetector
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GpsPhotoFragment : Fragment() {

    private var _binding: FragmentGpsPhotoBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    @Inject
    lateinit var geoFenceEngine: GeoFenceEngine

    @Inject
    lateinit var tfLiteCropDetector: TFLiteCropDetector

    private var currentPhotoIndex = 1
    private var isGeoFencePassed = false

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.CAMERA] == true) {
                startGeoFenceValidation()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGpsPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        checkPermissionsAndStart()
    }

    private fun setupViews() {
        binding.cardPhoto1.setOnClickListener {
            currentPhotoIndex = 1
            launchCamera()
        }
        binding.cardPhoto2.setOnClickListener {
            currentPhotoIndex = 2
            launchCamera()
        }
        binding.cardPhoto3.setOnClickListener {
            currentPhotoIndex = 3
            launchCamera()
        }
        
        binding.btnStep3Prev.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnStep3Next.setOnClickListener {
            findNavController().navigate(R.id.action_gpsPhoto_to_review)
        }
    }

    private fun checkPermissionsAndStart() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startGeoFenceValidation()
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA))
        }
    }

    private fun startGeoFenceValidation() {
        val polygonJson = sharedViewModel.selectedLandRecord.value?.boundaryPolygonJson
        lifecycleScope.launch {
            geoFenceEngine.startValidation(polygonJson).collect { result ->
                updateGeoFenceUI(result)
            }
        }
    }

    private fun updateGeoFenceUI(result: GeoFenceResult) {
        when (result) {
            is GeoFenceResult.Loading -> {
                binding.tvGeoStatusTitle.text = "GPS शोधत आहे..."
                binding.tvGeoStatusTitle.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
                binding.tvGeoStatusDesc.text = "स्थान तपासत आहे"
            }
            is GeoFenceResult.Pass -> {
                isGeoFencePassed = true
                binding.tvGeoStatusTitle.text = "✅ आपण आपल्या शेतात आहात"
                binding.tvGeoStatusTitle.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
                binding.tvGeoStatusDesc.text = "GPS अचूकता: ±${result.accuracyMetres.toInt()}m"
                enableCameraButtons()
            }
            is GeoFenceResult.Fail -> {
                isGeoFencePassed = false
                binding.tvGeoStatusTitle.text = "🔒 आपण शेताबाहेर आहात"
                binding.tvGeoStatusTitle.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                binding.tvGeoStatusDesc.text = "अंतर: ~${result.distanceMetres.toInt()} मीटर"
                disableCameraButtons()
            }
            is GeoFenceResult.AccuracyTooLow -> {
                binding.tvGeoStatusTitle.text = "GPS सिग्नल सुधारत आहे"
                binding.tvGeoStatusDesc.text = "मोकळ्या जागी जा"
            }
            is GeoFenceResult.GpsUnavailable -> {
                binding.tvGeoStatusTitle.text = "GPS उपलब्ध नाही"
                binding.tvGeoStatusDesc.text = "कृपया GPS चालू करा"
            }
            is GeoFenceResult.MockLocationDetected -> {
                binding.tvGeoStatusTitle.text = "Mock location आढळले"
                binding.tvGeoStatusTitle.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                binding.tvGeoStatusDesc.text = "बनावट स्थान वापरण्यास मनाई आहे"
                disableCameraButtons()
            }
        }
    }

    private fun enableCameraButtons() {
        binding.cardPhoto1.isEnabled = true
        if (!sharedViewModel.gpsData.value?.photo1Uri.isNullOrEmpty()) binding.cardPhoto2.isEnabled = true
        if (!sharedViewModel.gpsData.value?.photo2Uri.isNullOrEmpty()) binding.cardPhoto3.isEnabled = true
    }

    private fun disableCameraButtons() {
        binding.cardPhoto1.isEnabled = false
        binding.cardPhoto2.isEnabled = false
        binding.cardPhoto3.isEnabled = false
    }

    private fun launchCamera() {
        // Mock capture for demo
        handlePhotoCaptured()
    }

    private fun handlePhotoCaptured() {
        val dummyBitmap = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888)
        
        when (currentPhotoIndex) {
            1 -> {
                binding.ivPhoto1Preview.visibility = View.VISIBLE
                binding.ivPhoto1Preview.setImageBitmap(dummyBitmap)
                sharedViewModel.setPhoto1Uri("uri_1")
                binding.cardPhoto2.isEnabled = true
            }
            2 -> {
                sharedViewModel.setPhoto2Uri("uri_2")
                binding.cardPhoto3.isEnabled = true
            }
            3 -> {
                sharedViewModel.setPhoto3Uri("uri_3")
                checkCompletion()
            }
        }
        
        processAiDetection(dummyBitmap, currentPhotoIndex)
    }

    private fun processAiDetection(bitmap: Bitmap, index: Int) {
        lifecycleScope.launch {
            val declaredCrop = sharedViewModel.cropFormData.value?.cropName
            val result = tfLiteCropDetector.detectCrop(bitmap, declaredCrop)
            
            val percent = (result.confidence * 100).toInt()
            binding.tvAiResult.text = "🌱 ओळखलेले पीक: ${result.detectedCrop} ($percent%)"
            
            if (result.matchStatus == MatchStatus.MISMATCH) {
                showMismatchDialog(declaredCrop ?: "", result.detectedCrop, percent)
            }
        }
    }

    private fun showMismatchDialog(declared: String, detected: String, confidence: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("⚠️ पीक जुळत नाही")
            .setMessage("घोषित: $declared | आढळले: $detected ($confidence%)\nतलाठी पुनर्तपासणी आवश्यक")
            .setPositiveButton("माझी घोषणा बरोबर आहे") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("पीक बदला") { dialog, _ ->
                findNavController().popBackStack()
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun checkCompletion() {
        if (isGeoFencePassed && currentPhotoIndex == 3) {
            binding.btnStep3Next.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        geoFenceEngine.stopValidation()
        _binding = null
    }
}
