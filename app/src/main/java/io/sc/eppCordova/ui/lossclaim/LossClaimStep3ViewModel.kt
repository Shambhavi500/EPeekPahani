package io.sc.eppCordova.ui.lossclaim

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.sc.eppCordova.domain.model.AiFrameRecord
import io.sc.eppCordova.domain.model.GpsPoint
import io.sc.eppCordova.domain.model.VideoClipRecord
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

enum class SurveyMode { ONLINE, OFFLINE }

@HiltViewModel
class LossClaimStep3ViewModel @Inject constructor() : ViewModel() {

    private val _surveyMode = MutableLiveData<SurveyMode>(SurveyMode.ONLINE)
    val surveyMode: LiveData<SurveyMode> = _surveyMode

    private val _currentStep = MutableLiveData<Int>(1)
    val currentStep: LiveData<Int> = _currentStep

    private val _videoClips = MutableLiveData<MutableList<VideoClipRecord>>(mutableListOf())
    val videoClips: LiveData<MutableList<VideoClipRecord>> = _videoClips

    private val _aiFrames = MutableLiveData<MutableList<AiFrameRecord>>(mutableListOf())
    val aiFrames: LiveData<MutableList<AiFrameRecord>> = _aiFrames

    private val _gpsTrail = MutableLiveData<MutableList<GpsPoint>>(mutableListOf())
    val gpsTrail: LiveData<MutableList<GpsPoint>> = _gpsTrail

    private var gpsJob: Job? = null

    fun setSurveyMode(isOnline: Boolean) {
        _surveyMode.value = if (isOnline) SurveyMode.ONLINE else SurveyMode.OFFLINE
    }

    fun startGpsTracking() {
        if (gpsJob?.isActive == true) return
        gpsJob = viewModelScope.launch {
            while (isActive) {
                // Mock GPS point
                val lat = 19.9975 + (Random.nextDouble() - 0.5) * 0.001
                val lon = 73.7898 + (Random.nextDouble() - 0.5) * 0.001
                val point = GpsPoint(lat, lon, System.currentTimeMillis())
                val list = _gpsTrail.value ?: mutableListOf()
                list.add(point)
                _gpsTrail.postValue(list)
                delay(5000)
            }
        }
    }

    fun stopGpsTracking() {
        gpsJob?.cancel()
        gpsJob = null
    }

    fun recordClip(step: Int, uri: String, duration: Int) {
        val lat = _gpsTrail.value?.lastOrNull()?.lat ?: 19.9975
        val lon = _gpsTrail.value?.lastOrNull()?.lon ?: 73.7898
        val clip = VideoClipRecord(
            stepNumber = step,
            fileUri = uri,
            durationSeconds = duration,
            gpsLat = lat,
            gpsLon = lon,
            timestamp = System.currentTimeMillis(),
            geoFenceStatus = "PASS"
        )
        val list = _videoClips.value ?: mutableListOf()
        list.add(clip)
        _videoClips.value = list
        
        // Mock AI analysis
        if (_surveyMode.value == SurveyMode.ONLINE) {
            val damageClass = listOf("MILD", "MODERATE", "SEVERE").random()
            val damagePct = when(damageClass) {
                "MILD" -> Random.nextInt(10, 30)
                "MODERATE" -> Random.nextInt(31, 60)
                else -> Random.nextInt(61, 95)
            }
            val aiFrame = AiFrameRecord(
                frameUri = uri, // Using video uri as frame uri for mock
                damagePercent = damagePct,
                damageClass = damageClass,
                confidence = Random.nextFloat() * 0.2f + 0.75f,
                gpsLat = lat,
                gpsLon = lon,
                timestamp = System.currentTimeMillis()
            )
            val aiList = _aiFrames.value ?: mutableListOf()
            aiList.add(aiFrame)
            _aiFrames.value = aiList
        }

        if (step < 6) {
            _currentStep.value = step + 1
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopGpsTracking()
    }
}
