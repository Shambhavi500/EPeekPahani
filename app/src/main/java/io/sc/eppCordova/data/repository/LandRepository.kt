package io.sc.eppCordova.data.repository

import io.sc.eppCordova.data.local.dao.LandRecordDao
import io.sc.eppCordova.data.local.entity.LandRecord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LandRepository @Inject constructor(
    private val dao: LandRecordDao
) {
    suspend fun getLandRecordsByVillage(villageId: Int): List<LandRecord> =
        dao.getLandRecordsByVillage(villageId)

    suspend fun insertAll(records: List<LandRecord>) = dao.insertAll(records)
}
