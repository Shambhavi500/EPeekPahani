package io.sc.eppCordova.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import io.sc.eppCordova.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = Constants.PREFS_NAME)

class UserPreferences @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val KEY_TOKEN = stringPreferencesKey(Constants.PREF_TOKEN)
        val KEY_USER_ID = stringPreferencesKey(Constants.PREF_USER_ID)
        val KEY_NAME = stringPreferencesKey(Constants.PREF_NAME)
        val KEY_MOBILE = stringPreferencesKey(Constants.PREF_MOBILE)
        val KEY_LANGUAGE = stringPreferencesKey(Constants.PREF_LANGUAGE)
    }

    suspend fun saveSession(token: String, userId: String, name: String, mobile: String) {
        dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_USER_ID] = userId
            prefs[KEY_NAME] = name
            prefs[KEY_MOBILE] = mobile
        }
    }

    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_TOKEN)
            prefs.remove(KEY_USER_ID)
            prefs.remove(KEY_NAME)
            prefs.remove(KEY_MOBILE)
        }
    }

    fun getToken(): Flow<String?> = dataStore.data.map { it[KEY_TOKEN] }
    
    fun isLoggedIn(): Flow<Boolean> = dataStore.data.map { it[KEY_TOKEN] != null }
    
    fun getLanguage(): Flow<String> = dataStore.data.map { it[KEY_LANGUAGE] ?: "mr" }
    
    suspend fun setLanguage(lang: String) {
        dataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = lang
        }
    }
}
