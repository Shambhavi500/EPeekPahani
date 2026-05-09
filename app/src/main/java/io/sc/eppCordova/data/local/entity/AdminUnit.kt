package io.sc.eppCordova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_units")
data class AdminUnit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val division: String,
    val district: String,
    val taluka: String,
    val village: String
)
