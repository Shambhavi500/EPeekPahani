package io.sc.eppCordova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crop_records")
data class CropRecord(
    @PrimaryKey(autoGenerate = true) val cropId: Int = 0,
    val userId: String,
    val khataNo: String,
    val gutNo: String,
    val season: String,
    val cropName: String = "",
    val cropType: String,
    val sowDate: String,
    val harvestDate: String,
    val areaHectares: Double = 0.0,
    val photo1Uri: String,
    val photo2Uri: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val isSubmitted: Boolean = false,
    val aiMatchStatus: String = "PENDING",
    val aiDetectedCrop: String? = null,
    val aiConfidence: Float? = null,
    val photo3Uri: String? = null,
    val certificateId: String? = null
)
