package io.sc.eppCordova.domain.model

import io.sc.eppCordova.data.local.entity.LandRecord

data class GatStatusItem(
    val landRecord: LandRecord,
    val status: String // Pending, Draft, Submitted, Verified
)
