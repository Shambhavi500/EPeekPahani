package io.sc.eppCordova.ui.geotag

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.ui.SharedViewModel
import io.sc.eppCordova.utils.GeoFenceEngine
import io.sc.eppCordova.utils.GeoFenceResult
import io.sc.eppCordova.utils.MatchStatus
import io.sc.eppCordova.utils.OfflineBannerHelper
import io.sc.eppCordova.utils.TFLiteCropDetector
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GpsPhotoFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    @Inject
    lateinit var geoFenceEngine: GeoFenceEngine

    @Inject
    lateinit var tfLiteCropDetector: TFLiteCropDetector
    
    @Inject
    lateinit var offlineBannerHelper: OfflineBannerHelper

    private var currentPhotoIndex = 1
    private var isGeoFencePassed = false

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.CAMERA] == true) {
                startGeoFenceValidation()
            }
        }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            handlePhotoCaptured()
        }
    }

    private var tempUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gps_photo, container, false)
        offlineBannerHelper.attach(view.findViewById(R.id.offline_banner), viewLifecycleOwner)
        setupViews(view)
        checkPermissionsAndStart()
        return view
    }

    private fun setupViews(view: View) {
        view.findViewById<Button>(R.id.btn_photo_1).setOnClickListener {
            currentPhotoIndex = 1
            launchCamera()
        }
        view.findViewById<Button>(R.id.btn_photo_2).setOnClickListener {
            currentPhotoIndex = 2
            launchCamera()
        }
        view.findViewById<Button>(R.id.btn_photo_3).setOnClickListener {
            currentPhotoIndex = 3
            launchCamera()
        }
        view.findViewById<Button>(R.id.btn_next).setOnClickListener {
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
        val tvStatus = view?.findViewById<TextView>(R.id.tv_geofence_status)
        val tvDetail = view?.findViewById<TextView>(R.id.tv_geofence_detail)
        val pb = view?.findViewById<ProgressBar>(R.id.pb_geofence)
        
        when (result) {
            is GeoFenceResult.Loading -> {
                tvStatus?.text = "GPS शोधत आहे..."
                tvStatus?.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
                pb?.visibility = View.VISIBLE
            }
            is GeoFenceResult.Pass -> {
                isGeoFencePassed = true
                tvStatus?.text = "✅ शेतात आहात"
                tvStatus?.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
                tvDetail?.text = "GPS अचूकता: ±${result.accuracyMetres.toInt()}m"
                pb?.visibility = View.GONE
                enableCameraButtons()
            }
            is GeoFenceResult.Fail -> {
                isGeoFencePassed = false
                tvStatus?.text = "🔒 आपण शेताबाहेर आहात"
                tvStatus?.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                tvDetail?.text = "अंतर: ~${result.distanceMetres.toInt()} मीटर"
                pb?.visibility = View.GONE
                disableCameraButtons()
            }
            is GeoFenceResult.AccuracyTooLow -> {
                tvStatus?.text = "GPS सिग्नल सुधारत आहे"
                tvDetail?.text = "मोकळ्या जागी जा"
                pb?.visibility = View.VISIBLE
            }
            is GeoFenceResult.GpsUnavailable -> {
                tvStatus?.text = "GPS उपलब्ध नाही"
                pb?.visibility = View.GONE
            }
            is GeoFenceResult.MockLocationDetected -> {
                tvStatus?.text = "Mock location आढळले"
                tvStatus?.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                pb?.visibility = View.GONE
                disableCameraButtons()
            }
        }
    }

    private fun enableCameraButtons() {
        view?.findViewById<Button>(R.id.btn_photo_1)?.isEnabled = true
        if (!sharedViewModel.gpsData.value?.photo1Uri.isNullOrEmpty()) view?.findViewById<Button>(R.id.btn_photo_2)?.isEnabled = true
        if (!sharedViewModel.gpsData.value?.photo2Uri.isNullOrEmpty()) view?.findViewById<Button>(R.id.btn_photo_3)?.isEnabled = true
    }

    private fun disableCameraButtons() {
        view?.findViewById<Button>(R.id.btn_photo_1)?.isEnabled = false
        view?.findViewById<Button>(R.id.btn_photo_2)?.isEnabled = false
        view?.findViewById<Button>(R.id.btn_photo_3)?.isEnabled = false
    }

    private fun launchCamera() {
        // Mock capture for demo
        handlePhotoCaptured()
    }

    private fun handlePhotoCaptured() {
        val dummyBitmap = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888)
        
        val ivPhoto = when (currentPhotoIndex) {
            1 -> view?.findViewById<ImageView>(R.id.iv_photo_1)
            2 -> view?.findViewById<ImageView>(R.id.iv_photo_2)
            3 -> view?.findViewById<ImageView>(R.id.iv_photo_3)
            else -> null
        }
        
        ivPhoto?.visibility = View.VISIBLE
        ivPhoto?.setImageBitmap(dummyBitmap)
        
        when (currentPhotoIndex) {
            1 -> {
                sharedViewModel.setPhoto1Uri("uri_1")
                view?.findViewById<Button>(R.id.btn_photo_2)?.isEnabled = true
            }
            2 -> {
                sharedViewModel.setPhoto2Uri("uri_2")
                view?.findViewById<Button>(R.id.btn_photo_3)?.isEnabled = true
            }
            3 -> {
                sharedViewModel.setPhoto3Uri("uri_3")
                checkCompletion()
            }
        }
        
        processAiDetection(dummyBitmap, currentPhotoIndex)
    }

    private fun processAiDetection(bitmap: Bitmap, index: Int) {
        val container = when (index) {
            1 -> view?.findViewById<FrameLayout>(R.id.ai_result_container_1)
            2 -> view?.findViewById<FrameLayout>(R.id.ai_result_container_2)
            3 -> view?.findViewById<FrameLayout>(R.id.ai_result_container_3)
            else -> null
        } ?: return

        container.removeAllViews()
        val aiView = layoutInflater.inflate(R.layout.ai_detection_result, container, false)
        val tvText = aiView.findViewById<TextView>(R.id.tv_ai_result_text)
        tvText.text = "पीक ओळखत आहे..."
        container.addView(aiView)

        lifecycleScope.launch {
            val declaredCrop = sharedViewModel.cropFormData.value?.cropName
            val result = tfLiteCropDetector.detectCrop(bitmap, declaredCrop)
            
            val percent = (result.confidence * 100).toInt()
            tvText.text = "ओळखलेले पीक: ${result.detectedCrop} | विश्वास: $percent%"
            
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
            view?.findViewById<Button>(R.id.btn_next)?.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        geoFenceEngine.stopValidation()
    }
}
