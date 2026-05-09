package io.sc.eppCordova.domain.model

import io.sc.eppCordova.data.local.entity.CropRecord
import io.sc.eppCordova.data.local.entity.LandRecord

data class CropRegistrationWithGat(
    val cropRecord: CropRecord,
    val landRecord: LandRecord?
)
