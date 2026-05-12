package io.sc.eppCordova.data.repository

import io.sc.eppCordova.data.local.CsvParserService
import io.sc.eppCordova.data.local.entity.Farmer
import io.sc.eppCordova.data.preferences.UserPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val prefs: UserPreferences,
    private val csvParserService: CsvParserService
) {
    suspend fun saveSession(token: String, userId: String, name: String, mobile: String) {
        prefs.saveSession(token, userId, name, mobile)
    }

    suspend fun clearSession() {
        prefs.clearSession()
    }
    
    suspend fun findFarmerByMobile(mobile: String): Farmer? {
        return csvParserService.getFarmerByMobile(mobile)
    }
}
