package io.sc.eppCordova.ui.certificate

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.databinding.FragmentCertificateBinding
import io.sc.eppCordova.utils.MatchStatus
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@AndroidEntryPoint
class CertificateFragment : Fragment() {

    private var _binding: FragmentCertificateBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CertificateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCertificateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.certificateData.observe(viewLifecycleOwner) { data ->
            binding.tvCertFarmerName.text = data.farmer?.name ?: "-"
            binding.tvCertGat.text = data.cropRecord?.gutNo ?: "-"
            binding.tvCertCrop.text = data.cropRecord?.cropName ?: "-"
            binding.tvCertAiCrop.text = data.cropRecord?.aiDetectedCrop ?: "N/A"
            
            when (data.cropRecord?.aiMatchStatus) {
                MatchStatus.VERIFIED.name -> {
                    binding.chipAiStatus.text = "AI Verified"
                    binding.chipAiStatus.setChipBackgroundColorResource(android.R.color.holo_green_dark)
                }
                MatchStatus.MISMATCH.name -> {
                    binding.chipAiStatus.text = "Mismatch Warning"
                    binding.chipAiStatus.setChipBackgroundColorResource(android.R.color.holo_orange_dark)
                }
                else -> {
                    binding.chipAiStatus.text = "Pending"
                    binding.chipAiStatus.setChipBackgroundColorResource(android.R.color.darker_gray)
                }
            }
            
            data.qrBitmap?.let {
                binding.ivQrCode.setImageBitmap(it)
            }
        }
        
        binding.fabDownload.setOnClickListener {
            generateAndSavePdf(view)
        }
        
        binding.btnShare.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "माझे ई-पीक पाहणी प्रमाणपत्र पहा!")
            }
            startActivity(Intent.createChooser(shareIntent, "Share Certificate"))
        }

        val mockGatNo = "001" 
        viewModel.generateCertificate(mockGatNo)
    }

    private fun generateAndSavePdf(view: View) {
        val width = view.width
        val height = view.height
        
        if (width <= 0 || height <= 0) {
            Toast.makeText(requireContext(), "त्रुटी: दृश्य आकारमान प्राप्त करू शकत नाही", Toast.LENGTH_SHORT).show()
            return
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
        val page = document.startPage(pageInfo)

        page.canvas.drawBitmap(bitmap, 0f, 0f, null)
        document.finishPage(page)

        val fileName = "Sowing_Certificate_${System.currentTimeMillis()}.pdf"
        var outputStream: OutputStream? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = requireContext().contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    outputStream = resolver.openOutputStream(uri)
                }
            } else {
                @Suppress("DEPRECATION")
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)
                outputStream = FileOutputStream(file)
            }

            outputStream?.use {
                document.writeTo(it)
                Toast.makeText(requireContext(), "प्रमाणपत्र जतन केले: Downloads", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "प्रमाणपत्र जतन करण्यात त्रुटी", Toast.LENGTH_SHORT).show()
        } finally {
            document.close()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}