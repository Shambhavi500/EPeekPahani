package io.sc.eppCordova.ui.review

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.databinding.FragmentReviewBinding
import io.sc.eppCordova.ui.SharedViewModel
import io.sc.eppCordova.ui.UiState

@AndroidEntryPoint
class ReviewFragment : Fragment() {
    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val land = sharedViewModel.selectedLandRecord.value
        val crop = sharedViewModel.cropFormData.value
        val gps = sharedViewModel.gpsData.value

        binding.tvReviewGat.text = "${land?.gutNo}"
        binding.tvReviewCropName.text = "${crop?.cropName}"
        binding.tvReviewArea.text = crop?.areaHectares.toString()
        binding.tvReviewDate.text = "${crop?.sowDate}"

        if (gps?.photo1Uri?.isNotEmpty() == true) {
            Glide.with(this).load(Uri.parse(gps.photo1Uri)).into(binding.ivReviewPhoto1)
        }
        if (gps?.photo2Uri?.isNotEmpty() == true) {
            Glide.with(this).load(Uri.parse(gps.photo2Uri)).into(binding.ivReviewPhoto2)
        }
        if (gps?.photo3Uri?.isNotEmpty() == true) {
            Glide.with(this).load(Uri.parse(gps.photo3Uri)).into(binding.ivReviewPhoto3)
        }

        binding.cbConsent.setOnCheckedChangeListener { _, isChecked ->
            binding.btnSubmit.isEnabled = isChecked
        }

        binding.btnSubmit.setOnClickListener {
            sharedViewModel.submitSurvey()
        }

        sharedViewModel.submitState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnSubmit.isEnabled = false
                }
                is UiState.Success -> {
                    findNavController().navigate(R.id.action_review_to_success)
                }
                is UiState.Error -> {
                    binding.btnSubmit.isEnabled = true
                    Snackbar.make(view, state.message, Snackbar.LENGTH_LONG).show()
                    // Still go to success since it's saved locally
                    findNavController().navigate(R.id.action_review_to_success)
                }
                is UiState.Idle -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
