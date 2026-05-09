package io.sc.eppCordova.domain.model

data class VideoClipRecord(
    val stepNumber: Int,
    val fileUri: String,
    val durationSeconds: Int,
    val gpsLat: Double,
    val gpsLon: Double,
    val timestamp: Long,
    val geoFenceStatus: String
)
