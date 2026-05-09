package io.sc.eppCordova.ui.geotag

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.databinding.FragmentGpsPhotoBinding
import io.sc.eppCordova.ui.GpsData
import io.sc.eppCordova.ui.SharedViewModel
import io.sc.eppCordova.utils.FileProviderUtils

@AndroidEntryPoint
class GpsPhotoFragment : Fragment() {
    private var _binding: FragmentGpsPhotoBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var photo1Uri: Uri? = null
    private var photo2Uri: Uri? = null
    private var isPhoto1 = true

    private var currentLat = 0.0
    private var currentLon = 0.0
    private var currentAcc = 0f

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            startLocationUpdates()
        }
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            val uri = if (isPhoto1) photo1Uri else photo2Uri
            val imageView = if (isPhoto1) binding.ivPhoto1 else binding.ivPhoto2
            val button = if (isPhoto1) binding.btnPhoto1 else binding.btnPhoto2
            
            Glide.with(this).load(uri).into(imageView)
            button.text = "✓ फोटो घेतला"
            
            checkCompletion()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGpsPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (hasLocationPermission()) {
            startLocationUpdates()
        } else {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }

        binding.btnPhoto1.setOnClickListener {
            isPhoto1 = true
            val file = FileProviderUtils.createImageFile(requireContext())
            photo1Uri = FileProviderUtils.getUriForFile(requireContext(), file)
            takePicture.launch(photo1Uri)
        }

        binding.btnPhoto2.setOnClickListener {
            isPhoto1 = false
            val file = FileProviderUtils.createImageFile(requireContext())
            photo2Uri = FileProviderUtils.getUriForFile(requireContext(), file)
            takePicture.launch(photo2Uri)
        }

        binding.btnNext.setOnClickListener {
            val gpsData = GpsData(
                latitude = currentLat,
                longitude = currentLon,
                accuracy = currentAcc,
                photo1Uri = photo1Uri.toString(),
                photo2Uri = photo2Uri.toString()
            )
            sharedViewModel.gpsData.value = gpsData
            findNavController().navigate(R.id.action_gpsPhoto_to_review)
        }
    }

    private fun hasLocationPermission() =
        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(2000)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                currentLat = location.latitude
                currentLon = location.longitude
                currentAcc = location.accuracy

                binding.pbLocation.visibility = View.GONE
                binding.tvLocation.text = "📍 स्थान मिळाले: $currentLat N, $currentLon E"
                binding.tvAccuracy.text = "अचूकता: ±${currentAcc.toInt()} मीटर"

                if (currentAcc > 20) {
                    binding.tvGpsWarning.visibility = View.VISIBLE
                } else {
                    binding.tvGpsWarning.visibility = View.GONE
                }
                
                checkCompletion()
            }
        }
    }

    private fun checkCompletion() {
        if (currentLat != 0.0 && photo1Uri != null && photo2Uri != null) {
            binding.btnNext.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        _binding = null
    }
}
