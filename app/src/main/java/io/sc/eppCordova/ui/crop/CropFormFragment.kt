package io.sc.eppCordova.ui.crop

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.databinding.FragmentCropFormBinding
import io.sc.eppCordova.ui.CropFormData
import io.sc.eppCordova.ui.SharedViewModel
import java.util.Calendar

@AndroidEntryPoint
class CropFormFragment : Fragment() {
    private var _binding: FragmentCropFormBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val crops = arrayOf("गहू", "ज्वारी", "बाजरी", "तूर", "हरभरा", "सोयाबीन", "कापूस", "ऊस", "भात", "मका")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCropFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, crops)
        binding.acvCropName.setAdapter(adapter)

        binding.etSowingDate.setOnClickListener { showDatePicker { date -> binding.etSowingDate.setText(date) } }

        binding.btnStep2Prev.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnStep2Next.setOnClickListener {
            val crop = binding.acvCropName.text.toString()
            val sowDate = binding.etSowingDate.text.toString()
            val areaStr = binding.etSowingArea.text.toString()

            val cropType = if (binding.cbMixedCrop.isChecked) "मिश्र" else "एकल"

            if (crop.isEmpty() || sowDate.isEmpty() || areaStr.isEmpty()) {
                Snackbar.make(view, "कृपया सर्व माहिती भरा", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val area = areaStr.toDoubleOrNull()
            if (area == null || area <= 0.0) {
                Snackbar.make(view, "कृपया वैध क्षेत्र टाका", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val formData = sharedViewModel.cropFormData.value ?: CropFormData()
            formData.cropName = crop
            formData.sowDate = sowDate
            formData.cropType = cropType
            formData.areaHectares = area
            sharedViewModel.cropFormData.value = formData

            findNavController().navigate(R.id.action_cropForm_to_gpsPhoto)
        }
    }

    private fun showDatePicker(onDateSet: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, y, m, d ->
            val formattedDate = String.format("%02d/%02d/%04d", d, m + 1, y)
            onDateSet(formattedDate)
        }, year, month, day).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
