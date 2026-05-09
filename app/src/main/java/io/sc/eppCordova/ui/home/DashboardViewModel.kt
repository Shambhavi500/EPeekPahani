package io.sc.eppCordova.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.sc.eppCordova.data.local.dao.CropRecordDao
import io.sc.eppCordova.data.local.dao.SyncQueueDao
import io.sc.eppCordova.data.local.entity.Farmer
import io.sc.eppCordova.domain.model.GatStatusItem
import io.sc.eppCordova.utils.NetworkUtils
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val cropRecordDao: CropRecordDao,
    private val syncQueueDao: SyncQueueDao,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val _farmerData = MutableLiveData<Farmer?>()
    val farmerData: LiveData<Farmer?> = _farmerData

    private val _gatList = MutableLiveData<List<GatStatusItem>>()
    val gatList: LiveData<List<GatStatusItem>> = _gatList

    val pendingSyncCount: LiveData<Int> = syncQueueDao.getPendingSyncCount()
    val isOnline: LiveData<Boolean> = networkUtils.isOnline

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            val farmer = cropRecordDao.getFarmer()
            _farmerData.postValue(farmer)
            
            val lands = cropRecordDao.getAllLandRecords()
            val statusItems = lands.map { land ->
                val crop = cropRecordDao.getCropRecordByGutNo(land.gutNo)
                val status = when {
                    crop == null -> "Pending"
                    crop.isSubmitted -> "Submitted"
                    else -> "Draft"
                }
                GatStatusItem(land, status)
            }
            _gatList.postValue(statusItems)
        }
    }
}
