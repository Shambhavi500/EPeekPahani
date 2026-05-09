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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.utils.MatchStatus
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@AndroidEntryPoint
class CertificateFragment : Fragment() {

    private val viewModel: CertificateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_certificate, container, false)
        
        viewModel.certificateData.observe(viewLifecycleOwner) { data ->
            view.findViewById<TextView>(R.id.tv_cert_farmer_name).text = data.farmer?.name ?: "-"
            view.findViewById<TextView>(R.id.tv_cert_gat).text = data.cropRecord?.gutNo ?: "-"
            view.findViewById<TextView>(R.id.tv_cert_crop).text = data.cropRecord?.cropName ?: "-"
            view.findViewById<TextView>(R.id.tv_cert_ai_crop).text = data.cropRecord?.aiDetectedCrop ?: "N/A"
            
            val chip = view.findViewById<Chip>(R.id.chip_ai_status)
            when (data.cropRecord?.aiMatchStatus) {
                MatchStatus.VERIFIED.name -> {
                    chip.text = "AI Verified"
                    chip.setChipBackgroundColorResource(android.R.color.holo_green_dark)
                }
                MatchStatus.MISMATCH.name -> {
                    chip.text = "Mismatch Warning"
                    chip.setChipBackgroundColorResource(android.R.color.holo_orange_dark)
                }
                else -> {
                    chip.text = "Pending"
                    chip.setChipBackgroundColorResource(android.R.color.darker_gray)
                }
            }
            
            data.qrBitmap?.let {
                view.findViewById<ImageView>(R.id.iv_qr_code).setImageBitmap(it)
            }
        }
        
        view.findViewById<ExtendedFloatingActionButton>(R.id.fab_download).setOnClickListener {
            generateAndSavePdf(view)
        }
        
        view.findViewById<Button>(R.id.btn_share).setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "माझे ई-पीक पाहणी प्रमाणपत्र पहा!")
            }
            startActivity(Intent.createChooser(shareIntent, "Share Certificate"))
        }

        val mockGatNo = "001" 
        viewModel.generateCertificate(mockGatNo)

        return view
    }

    private fun generateAndSavePdf(view: View) {
        // Measure and layout the view to ensure we get its actual dimensions
        val width = view.width
        val height = view.height
        
        if (width <= 0 || height <= 0) {
            Toast.makeText(requireContext(), "त्रुटी: दृश्य आकारमान प्राप्त करू शकत नाही", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a bitmap and draw the view onto it
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
        val page = document.startPage(pageInfo)

        // Draw the bitmap onto the PDF page
        page.canvas.drawBitmap(bitmap, 0f, 0f, null)
        document.finishPage(page)

        // Save using MediaStore for scoped storage
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
}