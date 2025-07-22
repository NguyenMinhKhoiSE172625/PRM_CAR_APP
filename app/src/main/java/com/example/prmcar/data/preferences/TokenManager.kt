package com.example.prmcar.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token_preferences")

class TokenManager(private val context: Context) {
    
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_TYPE_KEY = stringPreferencesKey("user_type")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    // Save token
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
        }
    }

    // Save user info
    suspend fun saveUserInfo(email: String, userType: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_TYPE_KEY] = userType
        }
    }

    // Save userId
    suspend fun saveUserId(userId: Int) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId.toString()
        }
    }

    // *** NEW: Asynchronous function to get token ***
    suspend fun getToken(): String? {
        return context.dataStore.data.first()[ACCESS_TOKEN_KEY]
    }

    // Get token as Flow
    fun getTokenFlow(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }
    }

    // Get user email
    fun getUserEmailFlow(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_EMAIL_KEY]
        }
    }

    // Get user type
    fun getUserTypeFlow(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_TYPE_KEY]
        }
    }

    // Get userId as Flow
    fun getUserIdFlow(): Flow<Int?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]?.toIntOrNull()
        }
    }

    // Get userId (suspend)
    suspend fun getUserId(): Int? {
        return context.dataStore.data.first()[USER_ID_KEY]?.toIntOrNull()
    }

    // Clear all data (logout)
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    // Check if user is logged in
    fun isLoggedInFlow(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY] != null
        }
    }

    fun getTokenWithBearer(): String? {
        val token = runBlocking {
            context.dataStore.data.first()[ACCESS_TOKEN_KEY]
        }
        return token?.let { "Bearer $it" }
    }
} 