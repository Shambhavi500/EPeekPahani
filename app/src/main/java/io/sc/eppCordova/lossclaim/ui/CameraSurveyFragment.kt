package io.sc.eppCordova.lossclaim.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import io.sc.eppCordova.R
import io.sc.eppCordova.databinding.FragmentCameraSurveyBinding
import io.sc.eppCordova.lossclaim.viewmodel.LossClaimViewModel
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraSurveyFragment : Fragment() {

    private var _binding: FragmentCameraSurveyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LossClaimViewModel by activityViewModels()

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    private var currentStep = 1
    private val totalSteps = 5

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraSurveyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        updateUiForStep()

        binding.btnNextStep.setOnClickListener {
            if (currentStep < totalSteps) {
                currentStep++
                updateUiForStep()
            } else {
                takePhoto()
            }
        }
    }

    private fun updateUiForStep() {
        binding.tvStepProgress.text = "Step $currentStep of $totalSteps"
        
        when (currentStep) {
            1 -> {
                binding.tvAiInstruction.text = "Please point the camera towards your field."
                binding.btnNextStep.text = "Done"
            }
            2 -> {
                binding.tvAiInstruction.text = "Walk slowly along one row so I can see the full crop."
                binding.btnNextStep.text = "I am walking"
            }
            3 -> {
                binding.tvAiInstruction.text = "I see flooding. Was water above the base of the plants?"
                binding.btnNextStep.text = "Yes, it was"
            }
            4 -> {
                binding.tvAiInstruction.text = "Approximately what percentage of your crop is damaged?"
                binding.btnNextStep.text = "Around 50%" // Mocking slider response
            }
            5 -> {
                binding.tvAiInstruction.text = "Is any part of the crop still harvestable?"
                binding.btnNextStep.text = "Capture Evidence & Complete"
            }
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        
        @Suppress("DEPRECATION")
        val photoFile = File(requireContext().externalMediaDirs.firstOrNull(), "loss_claim_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        binding.btnNextStep.isEnabled = false
        binding.btnNextStep.text = "Capturing..."

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(requireContext(), "Photo failed", Toast.LENGTH_SHORT).show()
                    binding.btnNextStep.isEnabled = true
                    binding.btnNextStep.text = "Retry Capture"
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    findNavController().navigate(R.id.action_camera_to_processing)
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                // Ignore
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }
}