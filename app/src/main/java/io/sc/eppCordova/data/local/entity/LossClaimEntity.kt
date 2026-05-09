package io.sc.eppCordova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loss_claims")
data class LossClaimEntity(
    @PrimaryKey val claimId: String,
    val farmerId: String,
    val registrationId: String,
    val gatNumber: String,
    val lossType: String,
    val incidentDate: String,
    val reportedAffectedAreaHa: Double,
    val surveyMode: String,
    val geoFenceStatus: String,
    val videoClipsJson: String,
    val aiFramesJson: String,
    val gpsTrailJson: String,
    val weatherCorrelationScore: Int?,
    val ndviDrop: Double?,
    val damageSeverity: String?,
    val fraudRiskScore: Int?,
    val recommendedCompensationPct: Int?,
    val status: String,
    val isSubmitted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
