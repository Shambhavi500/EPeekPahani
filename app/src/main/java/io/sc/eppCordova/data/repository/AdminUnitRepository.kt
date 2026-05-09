package io.sc.eppCordova.data.repository

import io.sc.eppCordova.data.local.dao.AdminUnitDao
import io.sc.eppCordova.data.local.entity.AdminUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminUnitRepository @Inject constructor(
    private val dao: AdminUnitDao
) {
    suspend fun getAll(): List<AdminUnit> = dao.getAll()

    suspend fun insertAll(units: List<AdminUnit>) = dao.insertAll(units)

    suspend fun clearAll() = dao.clearAll()
}
