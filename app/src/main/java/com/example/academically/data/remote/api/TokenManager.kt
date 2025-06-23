package com.example.academically.data.remote.api

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "academic_ally_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: Flow<AuthState> = _authState.asStateFlow()

    init {
        // Check if user is already authenticated
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        val token = getToken()
        val expiresAt = getTokenExpirationDate()

        if (token != null && !isTokenExpired(expiresAt)) {
            val userId = getUserId()
            val userEmail = getUserEmail()
            val userName = getUserName()

            if (userId != null && userEmail != null && userName != null) {
                _authState.value = AuthState.Authenticated(
                    token = token,
                    userId = userId,
                    userEmail = userEmail,
                    userName = userName
                )
            }
        }
    }

    fun saveAuthData(
        token: String,
        expiresAt: String,
        userId: Int,
        userEmail: String,
        userName: String,
        userProfilePicture: String? = null
    ) {
        sharedPreferences.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_EXPIRES_AT, expiresAt)
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, userEmail)
            putString(KEY_USER_NAME, userName)
            putString(KEY_USER_PROFILE_PICTURE, userProfilePicture)
            apply()
        }

        _authState.value = AuthState.Authenticated(
            token = token,
            userId = userId,
            userEmail = userEmail,
            userName = userName
        )
    }

    fun getToken(): String? = sharedPreferences.getString(KEY_TOKEN, null)

    fun getAuthHeader(): String? {
        val token = getToken()
        return if (token != null && !isTokenExpired()) {
            "Bearer $token"
        } else null
    }

    fun getUserId(): Int? {
        val id = sharedPreferences.getInt(KEY_USER_ID, -1)
        return if (id != -1) id else null
    }

    fun getUserEmail(): String? = sharedPreferences.getString(KEY_USER_EMAIL, null)
    fun getUserName(): String? = sharedPreferences.getString(KEY_USER_NAME, null)
    fun getUserProfilePicture(): String? = sharedPreferences.getString(KEY_USER_PROFILE_PICTURE, null)

    private fun getTokenExpirationDate(): String? = sharedPreferences.getString(KEY_EXPIRES_AT, null)

    @SuppressLint("NewApi")
    fun isTokenExpired(expirationDate: String? = null): Boolean {
        val expiration = expirationDate ?: getTokenExpirationDate() ?: return true

        return try {
            val expirationDateTime = LocalDateTime.parse(expiration, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            LocalDateTime.now().isAfter(expirationDateTime)
        } catch (e: Exception) {
            true // Si no se puede parsear, consideramos que está expirado
        }
    }

    fun clearAuthData() {
        sharedPreferences.edit().clear().apply()
        _authState.value = AuthState.Unauthenticated
    }

    fun isAuthenticated(): Boolean {
        return getToken() != null && !isTokenExpired()
    }

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_EXPIRES_AT = "token_expires_at"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_PROFILE_PICTURE = "user_profile_picture"
    }
}

sealed class AuthState {
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Authenticated(
        val token: String,
        val userId: Int,
        val userEmail: String,
        val userName: String
    ) : AuthState()
    data class Error(val message: String) : AuthState()
}
