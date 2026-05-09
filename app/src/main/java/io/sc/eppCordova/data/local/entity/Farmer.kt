package io.sc.eppCordova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farmers")
data class Farmer(
    @PrimaryKey val userId: String,
    val name: String,
    val mobile: String,
    val authToken: String
)
