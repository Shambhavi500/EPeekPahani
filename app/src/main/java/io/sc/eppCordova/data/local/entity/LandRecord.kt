package io.sc.eppCordova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "land_records")
data class LandRecord(
    @PrimaryKey val gutNo: String,
    val khataNo: String,
    val ownerName: String,
    val areaHectares: Double,
    val villageId: Int,
    val boundaryPolygonJson: String? = null
)
