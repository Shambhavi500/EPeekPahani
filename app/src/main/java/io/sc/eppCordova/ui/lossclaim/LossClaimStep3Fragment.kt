package io.sc.eppCordova.ui.lossclaim

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.utils.NetworkUtils
import io.sc.eppCordova.utils.OfflineBannerHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class LossClaimStep3Fragment : Fragment() {

    private val viewModel: LossClaimStep3ViewModel by viewModels()

    @Inject
    lateinit var networkUtils: NetworkUtils
    
    @Inject
    lateinit var offlineBannerHelper: OfflineBannerHelper

    private lateinit var aiChatAdapter: AiChatAdapter
    private lateinit var cameraExecutor: ExecutorService
    private var videoCapture: VideoCapture<Recorder>? = null
    private var activeRecording: Recording? = null
    private var isRecording = false

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
    ): View? {
        val view = inflater.inflate(R.layout.fragment_loss_claim_step3, container, false)
        offlineBannerHelper.attach(view.findViewById(R.id.offline_banner), viewLifecycleOwner)
        
        setupOnlineMode(view)
        
        networkUtils.isOnline.observe(viewLifecycleOwner) { isOnline ->
            viewModel.setSurveyMode(isOnline)
            if (isOnline) {
                view.findViewById<View>(R.id.online_mode_container).visibility = View.VISIBLE
                view.findViewById<View>(R.id.offline_mode_container).visibility = View.GONE
            } else {
                view.findViewById<View>(R.id.online_mode_container).visibility = View.GONE
                view.findViewById<View>(R.id.offline_mode_container).visibility = View.VISIBLE
                // Setup offline viewpager if needed
            }
        }
        
        viewModel.startGpsTracking()
        
        view.findViewById<Button>(R.id.btn_next).setOnClickListener {
            findNavController().navigate(R.id.action_lossClaimStep3_to_lossClaimStep4)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera(view)

        return view
    }

    private fun startCamera(view: View) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val previewView = view.findViewById<PreviewView>(R.id.camera_preview)

        if (previewView == null) return

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val recorder = Recorder.Builder()
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, videoCapture
                )
            } catch (exc: Exception) {
                // Handle camera failure
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun setupOnlineMode(view: View) {
        val rvChat = view.findViewById<RecyclerView>(R.id.rv_ai_chat)
        aiChatAdapter = AiChatAdapter()
        rvChat.layoutManager = LinearLayoutManager(requireContext())
        rvChat.adapter = aiChatAdapter
        
        val btnRecord = view.findViewById<Button>(R.id.btn_record_online)
        
        viewModel.currentStep.observe(viewLifecycleOwner) { step ->
            if (step <= 6) {
                aiChatAdapter.addMessage(ChatMessage(instructions[step - 1], true))
                rvChat.scrollToPosition(aiChatAdapter.itemCount - 1)
                btnRecord.text = "रेकॉर्ड करा (पायरी $step)"
            } else {
                btnRecord.isEnabled = false
                btnRecord.text = "सर्वेक्षण पूर्ण"
                view.findViewById<Button>(R.id.btn_next).isEnabled = true
            }
        }

        btnRecord.setOnClickListener {
            val step = viewModel.currentStep.value ?: return@setOnClickListener
            // Mock recording delay
            btnRecord.isEnabled = false
            btnRecord.text = "रेकॉर्डिंग सुरू आहे..."
            
            lifecycleScope.launch {
                delay(2000) // Mock 2 seconds recording
                val dummyUri = "mock_video_uri_$step"
                viewModel.recordClip(step, dummyUri, 30)
                aiChatAdapter.addMessage(ChatMessage("पायरी $step पूर्ण केली", false))
                
                // Mock AI response
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
                rvChat.scrollToPosition(aiChatAdapter.itemCount - 1)
                
                btnRecord.isEnabled = true
            }
        }
    }
}
