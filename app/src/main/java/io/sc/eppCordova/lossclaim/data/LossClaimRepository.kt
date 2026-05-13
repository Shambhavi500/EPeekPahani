package io.sc.eppCordova.lossclaim.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LossClaimRepository(private val dao: LossClaimDao) {

    suspend fun getFarmerByMobile(mobile: String): FarmerEntity? {
        // Mocking CSV fetch here for now if not in DB
        var farmer = dao.getFarmerByMobile(mobile)
        if (farmer == null) {
            farmer = FarmerEntity(
                mobileNumber = mobile,
                farmerName = "Ramesh Kumar",
                village = "Shirur",
                taluka = "Shirur",
                district = "Pune",
                gatNumber = "102",
                crop = "Soybean",
                area = "2.3 Acre",
                insuranceStatus = true
            )
            dao.insertFarmer(farmer)
        }
        return farmer
    }

    suspend fun saveLossClaim(claim: LossClaimEntity): Long {
        return dao.insertLossClaim(claim)
    }

    suspend fun getUnsyncedClaims(): List<LossClaimEntity> {
        return dao.getUnsyncedClaims()
    }

    suspend fun markClaimSynced(id: Int) {
        dao.markClaimAsSynced(id)
    }
}