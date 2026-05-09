package io.sc.eppCordova.ui.myclaims

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.sc.eppCordova.data.local.dao.CropRecordDao
import io.sc.eppCordova.data.local.dao.LossClaimDao
import io.sc.eppCordova.data.local.entity.CropRecord
import io.sc.eppCordova.data.local.entity.LossClaimEntity
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyClaimsViewModel @Inject constructor(
    private val cropRecordDao: CropRecordDao,
    private val lossClaimDao: LossClaimDao
) : ViewModel() {

    private val _cropRecords = MutableLiveData<List<CropRecord>>()
    val cropRecords: LiveData<List<CropRecord>> = _cropRecords

    private val _lossClaims = MutableLiveData<List<LossClaimEntity>>()
    val lossClaims: LiveData<List<LossClaimEntity>> = _lossClaims
    
    private val _certificates = MutableLiveData<List<CropRecord>>()
    val certificates: LiveData<List<CropRecord>> = _certificates

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            val farmer = cropRecordDao.getFarmer()
            if (farmer != null) {
                // We'll observe the LossClaims via LiveData directly in fragment, but for now we can just load CropRecords
                // Because cropRecordDao.getPendingRecords() exists, but we want all crops for farmer.
                // We'll mock getting all crops by just getting all land records' crops
                val allLands = cropRecordDao.getAllLandRecords()
                val crops = mutableListOf<CropRecord>()
                for (land in allLands) {
                    cropRecordDao.getCropRecordByGutNo(land.gutNo)?.let { crops.add(it) }
                }
                _cropRecords.postValue(crops)
                
                val certs = crops.filter { it.certificateId != null }
                _certificates.postValue(certs)
            }
        }
    }
    
    fun getClaimsLiveData(farmerId: String): LiveData<List<LossClaimEntity>> {
        return lossClaimDao.getClaimsByFarmer(farmerId)
    }
    
    suspend fun getFarmerId(): String? {
        return cropRecordDao.getFarmer()?.userId
    }
}
