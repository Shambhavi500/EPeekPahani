package io.sc.eppCordova.ui.lossclaim

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.ui.SharedViewModel
import io.sc.eppCordova.utils.OfflineBannerHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class LossClaimStep1Fragment : Fragment() {

    private val viewModel: LossClaimStep1ViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    @Inject
    lateinit var offlineBannerHelper: OfflineBannerHelper

    private var selectedCard: MaterialCardView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_loss_claim_step1, container, false)
        offlineBannerHelper.attach(view.findViewById(R.id.offline_banner), viewLifecycleOwner)
        setupViews(view)
        observeData(view)
        return view
    }

    private fun setupViews(view: View) {
        val farmer = sharedViewModel.farmerState.value
        view.findViewById<TextView>(R.id.tv_farmer_info).text = farmer?.name ?: "शेतकरी"

        val lossTypes = listOf(
            Pair("🌧️", "अतिवृष्टी"), Pair("🌊", "पूर"),
            Pair("☀️", "दुष्काळ"), Pair("🐛", "कीड"),
            Pair("🌨️", "गारपीट"), Pair("🌱", "मध्य-हंगाम"),
            Pair("🍂", "रोग")
        )

        val grid = view.findViewById<GridLayout>(R.id.grid_loss_types)
        for (type in lossTypes) {
            val cardView = layoutInflater.inflate(R.layout.item_loss_type_card, grid, false) as MaterialCardView
            cardView.findViewById<TextView>(R.id.tv_emoji).text = type.first
            cardView.findViewById<TextView>(R.id.tv_label).text = type.second
            
            cardView.setOnClickListener {
                selectedCard?.strokeWidth = 0
                selectedCard?.setCardBackgroundColor(requireContext().getColor(android.R.color.white))
                
                cardView.strokeWidth = 4
                cardView.strokeColor = requireContext().getColor(android.R.color.holo_green_dark)
                cardView.setCardBackgroundColor(requireContext().getColor(android.R.color.white))
                selectedCard = cardView
                
                viewModel.selectedLossType.value = type.second
            }
            grid.addView(cardView)
        }

        val etDate = view.findViewById<TextInputEditText>(R.id.et_incident_date)
        etDate.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker().build()
            picker.addOnPositiveButtonClickListener { time ->
                val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(time))
                etDate.setText(dateStr)
                viewModel.incidentDate.value = dateStr
                viewModel.selectedLossType.value?.let { lossType ->
                    viewModel.checkWeather(dateStr, lossType)
                }
            }
            picker.show(parentFragmentManager, "DATE_PICKER")
        }

        val slider = view.findViewById<Slider>(R.id.slider_affected_area)
        val tvAreaVal = view.findViewById<TextView>(R.id.tv_affected_area_val)
        slider.addOnChangeListener { _, value, _ ->
            val formatted = String.format("%.2f", value)
            tvAreaVal.text = "प्रभावित: $formatted Ha."
            viewModel.affectedArea.value = value.toDouble()
        }

        view.findViewById<MaterialButton>(R.id.btn_next).setOnClickListener {
            if (viewModel.validateAndProceed()) {
                // We should store these in a shared viewmodel to pass to step 2,3,4
                // Since this is just Step1, we will use SharedViewModel to hold LossClaim data or args
                findNavController().navigate(R.id.action_lossClaimStep1_to_lossClaimStep2)
            } else {
                Snackbar.make(view, "कृपया सर्व माहिती भरा", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeData(view: View) {
        val spinner = view.findViewById<AutoCompleteTextView>(R.id.spinner_gat)
        val tvCrop = view.findViewById<TextView>(R.id.tv_registered_crop)
        val slider = view.findViewById<Slider>(R.id.slider_affected_area)

        viewModel.verifiedGats.observe(viewLifecycleOwner) { gats ->
            val gatNumbers = gats.map { it.landRecord?.gutNo ?: "" }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, gatNumbers)
            spinner.setAdapter(adapter)

            spinner.setOnItemClickListener { _, _, position, _ ->
                val selected = gats[position]
                viewModel.selectedGat.value = selected
                tvCrop.text = "नोंदणी केलेले पीक: ${selected.cropRecord.cropName}"
                val maxArea = selected.landRecord?.areaHectares?.toFloat() ?: 1.0f
                slider.valueTo = if (maxArea > 0f) maxArea else 1.0f
                
                // generate claim ID
                viewModel.generateClaimId("NSK", "NIP", selected.landRecord?.gutNo ?: "000")
            }
        }

        viewModel.generatedClaimId.observe(viewLifecycleOwner) { id ->
            view.findViewById<TextView>(R.id.tv_claim_id).text = "तुमचा दावा क्रमांक: $id"
        }

        viewModel.weatherCheckResult.observe(viewLifecycleOwner) { result ->
            val chip = view.findViewById<Chip>(R.id.chip_weather_check)
            if (result != null) {
                chip.visibility = View.VISIBLE
                chip.text = result.message
                when (result.type) {
                    0 -> chip.setChipBackgroundColorResource(android.R.color.darker_gray)
                    1 -> chip.setChipBackgroundColorResource(android.R.color.holo_green_dark)
                    2 -> chip.setChipBackgroundColorResource(android.R.color.holo_orange_dark)
                }
                chip.setTextColor(requireContext().getColor(android.R.color.white))
            } else {
                chip.visibility = View.GONE
            }
        }
    }
}