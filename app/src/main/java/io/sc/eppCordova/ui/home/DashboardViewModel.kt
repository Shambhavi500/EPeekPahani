package io.sc.eppCordova.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.sc.eppCordova.data.local.dao.CropRecordDao
import io.sc.eppCordova.data.local.dao.SyncQueueDao
import io.sc.eppCordova.data.local.entity.Farmer
import io.sc.eppCordova.utils.NetworkUtils
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CropDetail(
    val mainCrop: String = "",
    val secondaryCrop: String = "",
    val season: String = "",
    val irrigation: String = "",
    val area: String = ""
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val cropRecordDao: CropRecordDao,
    private val syncQueueDao: SyncQueueDao,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val _farmerData = MutableLiveData<Farmer?>()
    val farmerData: LiveData<Farmer?> = _farmerData

    private val _cropDetail = MutableLiveData<CropDetail>()
    val cropDetail: LiveData<CropDetail> = _cropDetail

    private val _schemes = MutableLiveData<List<String>>()
    val schemes: LiveData<List<String>> = _schemes

    val pendingSyncCount: LiveData<Int> = syncQueueDao.getPendingSyncCount()
    val isOnline: LiveData<Boolean> = networkUtils.isOnline

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            val farmer = cropRecordDao.getFarmer()
            _farmerData.postValue(farmer)

            // Build crop detail from farmer data
            if (farmer != null) {
                val records = cropRecordDao.getAllLandRecords()
                val firstRecord = if (records.isNotEmpty()) {
                    cropRecordDao.getCropRecordByGutNo(records.first().gutNo)
                } else null

                val season = firstRecord?.season ?: ""
                val mainCrop = farmer.primaryCrop.ifBlank { firstRecord?.cropName ?: "" }

                _cropDetail.postValue(
                    CropDetail(
                        mainCrop = mainCrop,
                        secondaryCrop = farmer.secondaryCrop,
                        season = season,
                        irrigation = farmer.irrigationSource,
                        area = farmer.landHoldingHa
                    )
                )

                // Derive schemes from farmer fields
                val schemeList = mutableListOf<String>()
                if (farmer.pmKisanBeneficiary.equals("yes", true) ||
                    farmer.pmKisanBeneficiary.equals("true", true)) {
                    schemeList.add("PM-KISAN")
                }
                if (farmer.hasKcc.equals("yes", true) ||
                    farmer.hasKcc.equals("true", true)) {
                    schemeList.add("KCC")
                }
                if (farmer.mgnregaLinked.equals("yes", true) ||
                    farmer.mgnregaLinked.equals("true", true)) {
                    schemeList.add("MGNREGA")
                }
                // Always add some defaults for demo if empty
                if (schemeList.isEmpty()) {
                    schemeList.add("PM-KISAN")
                    schemeList.add("Crop Insurance")
                    schemeList.add("Soil Health Card")
                }
                _schemes.postValue(schemeList)
            }
        }
    }
}
