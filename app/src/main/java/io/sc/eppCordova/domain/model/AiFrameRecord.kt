package io.sc.eppCordova.domain.model

data class AiFrameRecord(
    val frameUri: String,
    val damagePercent: Int,
    val damageClass: String,
    val confidence: Float,
    val gpsLat: Double,
    val gpsLon: Double,
    val timestamp: Long
)
