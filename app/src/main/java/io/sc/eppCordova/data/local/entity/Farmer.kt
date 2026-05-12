package io.sc.eppCordova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farmers")
data class Farmer(
    @PrimaryKey val userId: String,
    val name: String,
    val mobile: String,
    val authToken: String,
    val farmerId: String = "",
    val gender: String = "",
    val dateOfBirth: String = "",
    val category: String = "",
    val aadhaarMasked: String = "",
    val state: String = "",
    val district: String = "",
    val taluka: String = "",
    val village: String = "",
    val pincode: String = "",
    val khasraNumber: String = "",
    val landHoldingHa: String = "",
    val landType: String = "",
    val soilType: String = "",
    val irrigationSource: String = "",
    val primaryCrop: String = "",
    val secondaryCrop: String = "",
    val hasKcc: String = "",
    val kccBank: String = "",
    val pmKisanBeneficiary: String = "",
    val mgnregaLinked: String = "",
    val registrationDate: String = "",
    val status: String = ""
)

