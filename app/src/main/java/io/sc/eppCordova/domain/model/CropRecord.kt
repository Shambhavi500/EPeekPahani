package io.sc.eppCordova.domain.model

data class CropRecord(
    val id: Long = 0,
    val referenceId: String? = null,
    val userId: String,
    val khataNo: String,
    val gutNo: String,
    val season: String,
    val cropType: String,
    val cropName: String,
    val sowDate: String,
    val photo1Uri: String,
    val photo2Uri: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val timestamp: Long,
    val isSynced: Boolean = false
)
