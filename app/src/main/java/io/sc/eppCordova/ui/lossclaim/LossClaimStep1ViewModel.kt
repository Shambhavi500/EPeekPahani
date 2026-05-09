package io.sc.eppCordova.ui.lossclaim

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.sc.eppCordova.data.local.dao.CropRecordDao
import io.sc.eppCordova.data.local.dao.LandRecordDao
import io.sc.eppCordova.domain.model.CropRegistrationWithGat
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

data class WeatherCheckResult(val message: String, val type: Int) // 0: gray, 1: green, 2: orange

@HiltViewModel
class LossClaimStep1ViewModel @Inject constructor(
    private val cropRecordDao: CropRecordDao,
    private val landRecordDao: LandRecordDao
) : ViewModel() {

    private val _verifiedGats = MutableLiveData<List<CropRegistrationWithGat>>()
    val verifiedGats: LiveData<List<CropRegistrationWithGat>> = _verifiedGats

    val selectedGat = MutableLiveData<CropRegistrationWithGat?>()
    val selectedLossType = MutableLiveData<String>()
    val incidentDate = MutableLiveData<String>()
    val affectedArea = MutableLiveData<Double>(0.0)
    
    private val _generatedClaimId = MutableLiveData<String>()
    val generatedClaimId: LiveData<String> = _generatedClaimId

    private val _weatherCheckResult = MutableLiveData<WeatherCheckResult?>()
    val weatherCheckResult: LiveData<WeatherCheckResult?> = _weatherCheckResult

    init {
        loadVerifiedGats()
    }

    private fun loadVerifiedGats() {
        viewModelScope.launch {
            // Mock: We consider all submitted crops as verified for this flow if aiMatchStatus is not in DB correctly, 
            // but let's query all pending and non-pending crops to show them.
            // Ideally: Query CropRecord where status=VERIFIED.
            val lands = landRecordDao.getLandRecordsByVillage(0) // Mock village ID or get all
            // Since we don't have a direct query for all crops, let's just get all lands and crops.
            // For now, let's use all land records and assume a mock CropRecord if needed, or query them.
            
            // Wait, cropRecordDao.getPendingRecords() gets only isSubmitted=0. We need all crops or get farmer's crops.
            // Let's add a query or just mock it if we can't query all.
            // We can just query `landRecordDao.getAllLandRecords()` which we added to CropRecordDao earlier or LandRecordDao.
            val allLands = cropRecordDao.getAllLandRecords()
            val validGats = mutableListOf<CropRegistrationWithGat>()
            
            for (land in allLands) {
                val crop = cropRecordDao.getCropRecordByGutNo(land.gutNo)
                if (crop != null) {
                    validGats.add(CropRegistrationWithGat(crop, land))
                }
            }
            _verifiedGats.postValue(validGats)
        }
    }

    fun generateClaimId(district: String, taluka: String, gat: String) {
        if (_generatedClaimId.value == null) {
            val seq = Random.nextInt(100, 999)
            _generatedClaimId.value = "MH-2025-${district.take(3).uppercase()}-${taluka.take(3).uppercase()}-$gat-$seq"
        }
    }

    fun checkWeather(date: String, lossType: String) {
        // Mock logic: random 70% chance of green chip
        if (lossType == "अतिवृष्टी" || lossType == "पूर") {
            if (Random.nextFloat() < 0.7f) {
                _weatherCheckResult.value = WeatherCheckResult("✅ या तारखेला पावसाची नोंद आहे", 1)
            } else {
                _weatherCheckResult.value = WeatherCheckResult("⚠️ हवामान डेटा जुळत नाही — पुढे जाता येईल", 2)
            }
        } else if (lossType == "दुष्काळ") {
            _weatherCheckResult.value = WeatherCheckResult("⚠️ हवामान डेटा जुळत नाही — पुढे जाता येईल", 2)
        } else {
            _weatherCheckResult.value = WeatherCheckResult("हवामान तपासणी लागू नाही", 0)
        }
    }

    fun validateAndProceed(): Boolean {
        return selectedGat.value != null &&
                !selectedLossType.value.isNullOrEmpty() &&
                !incidentDate.value.isNullOrEmpty() &&
                (affectedArea.value ?: 0.0) > 0.0
    }
}
