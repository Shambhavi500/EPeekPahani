package io.sc.eppCordova.data.repository

import io.sc.eppCordova.data.preferences.UserPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val prefs: UserPreferences
) {
    suspend fun saveSession(token: String, userId: String, name: String, mobile: String) {
        prefs.saveSession(token, userId, name, mobile)
    }

    suspend fun clearSession() {
        prefs.clearSession()
    }
}
