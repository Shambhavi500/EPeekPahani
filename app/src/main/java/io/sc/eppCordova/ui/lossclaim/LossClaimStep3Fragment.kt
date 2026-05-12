package io.sc.eppCordova.ui.lossclaim

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.databinding.FragmentLossClaimStep3Binding
import io.sc.eppCordova.utils.NetworkUtils
import io.sc.eppCordova.utils.OfflineBannerHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class LossClaimStep3Fragment : Fragment() {

    private var _binding: FragmentLossClaimStep3Binding? = null
    private val binding get() = _binding!!

    private val viewModel: LossClaimStep3ViewModel by viewModels()

    @Inject
    lateinit var networkUtils: NetworkUtils
    
    @Inject
    lateinit var offlineBannerHelper: OfflineBannerHelper

    private lateinit var aiChatAdapter: AiChatAdapter
    private lateinit var cameraExecutor: ExecutorService
    private var videoCapture: VideoCapture<Recorder>? = null

    private val instructions = listOf(
        "कृपया खराब झालेल्या पिकाचे संपूर्ण क्षेत्र दाखवा (60s)",
        "उत्तर दिशेकडून शेत दाखवा (45s)",
        "दक्षिण दिशेकडून शेत दाखवा (45s)",
        "पिकाचे पान जवळून दाखवा (30s)",
        "जमिनीची अवस्था दाखवा (30s)",
        "शेताच्या सीमा दाखवा (30s)"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLossClaimStep3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupOnlineMode()
        
        networkUtils.isOnline.observe(viewLifecycleOwner) { isOnline ->
            viewModel.setSurveyMode(isOnline)
            if (isOnline) {
                binding.onlineModeContainer.visibility = View.VISIBLE
                binding.offlineModeContainer.visibility = View.GONE
            } else {
                binding.onlineModeContainer.visibility = View.GONE
                binding.offlineModeContainer.visibility = View.VISIBLE
            }
        }
        
        viewModel.startGpsTracking()
        
        binding.btnNextOnline.setOnClickListener {
            findNavController().navigate(R.id.action_lossClaimStep3_to_lossClaimStep4)
        }
        binding.btnNextOffline.setOnClickListener {
            findNavController().navigate(R.id.action_lossClaimStep3_to_lossClaimStep4)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                }

            val recorder = Recorder.Builder().build()
            videoCapture = VideoCapture.withOutput(recorder)

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, videoCapture
                )
            } catch (exc: Exception) {
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun setupOnlineMode() {
        aiChatAdapter = AiChatAdapter()
        binding.rvAiChat.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAiChat.adapter = aiChatAdapter
        
        viewModel.currentStep.observe(viewLifecycleOwner) { step ->
            if (step <= 6) {
                aiChatAdapter.addMessage(ChatMessage(instructions[step - 1], true))
                binding.rvAiChat.scrollToPosition(aiChatAdapter.itemCount - 1)
                binding.btnRecordOnline.text = "रेकॉर्ड करा (पायरी $step)"
            } else {
                binding.btnRecordOnline.isEnabled = false
                binding.btnRecordOnline.text = "सर्वेक्षण पूर्ण"
                binding.btnNextOnline.isEnabled = true
            }
        }

        binding.btnRecordOnline.setOnClickListener {
            val step = viewModel.currentStep.value ?: return@setOnClickListener
            binding.btnRecordOnline.isEnabled = false
            binding.btnRecordOnline.text = "रेकॉर्डिंग सुरू आहे..."
            
            lifecycleScope.launch {
                delay(2000)
                val dummyUri = "mock_video_uri_$step"
                viewModel.recordClip(step, dummyUri, 30)
                aiChatAdapter.addMessage(ChatMessage("पायरी $step पूर्ण केली", false))
                
                delay(1000)
                aiChatAdapter.addMessage(ChatMessage("विश्लेषण करत आहे... 🤔", true))
                delay(1500)
                val aiList = viewModel.aiFrames.value
                val lastAi = aiList?.lastOrNull()
                val damageStr = when(lastAi?.damageClass) {
                    "MILD" -> "कमी"
                    "MODERATE" -> "मध्यम"
                    else -> "तीव्र"
                }
                aiChatAdapter.addMessage(ChatMessage("नुकसान आढळले: $damageStr (${lastAi?.damagePercent}%)", true))
                binding.rvAiChat.scrollToPosition(aiChatAdapter.itemCount - 1)
                
                binding.btnRecordOnline.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
