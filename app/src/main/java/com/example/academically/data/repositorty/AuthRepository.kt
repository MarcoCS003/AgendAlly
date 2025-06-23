package com.example.academically.data.repositorty


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.academically.data.local.dao.UserProfileDao
import com.example.academically.data.model.User
import com.example.academically.data.model.UserRole
import com.example.academically.data.local.entities.UserProfileEntity
import com.example.academically.data.remote.api.AuthApiService
import com.example.academically.data.remote.api.GoogleAuthManager
import com.example.academically.data.remote.api.GoogleAuthRequest
import com.example.academically.data.remote.api.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.ExperimentalTime

@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val googleAuthManager: GoogleAuthManager,
    private val tokenManager: TokenManager,
    private val userProfileDao: UserProfileDao
) {

    // Obtener perfil del usuario actual desde BD local
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentUserProfile(): Flow<User?> {
        return userProfileDao.getUserProfile().map { entity ->
            entity?.toUser()
        }
    }

    // Verificar si hay usuario logueado
    suspend fun isUserLoggedIn(): Boolean {
        return tokenManager.isAuthenticated()
    }

    // Login con Google OAuth
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loginWithGoogle(): Result<User> {
        return try {
            // 1. Obtener ID Token de Google
            val googleIdTokenResult = googleAuthManager.signInWithGoogle()
            if (googleIdTokenResult.isFailure) {
                return Result.failure(googleIdTokenResult.exceptionOrNull() ?: Exception("Error obteniendo token de Google"))
            }

            val idToken = googleIdTokenResult.getOrThrow()

            // 2. Enviar token al backend
            val authRequest = GoogleAuthRequest(idToken = idToken)
            val response = authApiService.authenticateWithGoogle(authRequest)

            if (response.isSuccessful && response.body()?.success == true) {
                val authResponse = response.body()!!
                val apiUser = authResponse.user!!

                // 3. Guardar datos de autenticación
                tokenManager.saveAuthData(
                    token = authResponse.token!!,
                    expiresAt = authResponse.expiresAt!!,
                    userId = apiUser.id,
                    userEmail = apiUser.email,
                    userName = apiUser.name,
                    userProfilePicture = apiUser.profilePicture
                )

                // 4. Guardar usuario en BD local
                val user = User(
                    id = apiUser.id,
                    googleId = apiUser.googleId,
                    email = apiUser.email,
                    name = apiUser.name,
                    profilePicture = apiUser.profilePicture,
                    role = UserRole.valueOf(apiUser.role),
                    isActive = apiUser.isActive,
                    createdAt = apiUser.createdAt,
                    lastLoginAt = apiUser.lastLoginAt
                )

                userProfileDao.insertUserProfile(user.toEntity())

                Result.success(user)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Error en autenticación"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Login mock para desarrollo
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun mockLogin(email: String = "test@estudiante.tecnm.mx"): Result<User> {
        return try {
            val response = authApiService.mockLogin(email = email)

            if (response.isSuccessful && response.body()?.success == true) {
                val authResponse = response.body()!!
                val apiUser = authResponse.user!!

                // Guardar datos de autenticación
                tokenManager.saveAuthData(
                    token = authResponse.token!!,
                    expiresAt = authResponse.expiresAt!!,
                    userId = apiUser.id,
                    userEmail = apiUser.email,
                    userName = apiUser.name,
                    userProfilePicture = apiUser.profilePicture
                )

                // Guardar usuario en BD local
                val user = User(
                    id = apiUser.id,
                    googleId = apiUser.googleId,
                    email = apiUser.email,
                    name = apiUser.name,
                    profilePicture = apiUser.profilePicture,
                    role = UserRole.valueOf(apiUser.role),
                    isActive = apiUser.isActive,
                    createdAt = apiUser.createdAt,
                    lastLoginAt = apiUser.lastLoginAt
                )

                userProfileDao.insertUserProfile(user.toEntity())

                Result.success(user)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Error en login mock"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Cerrar sesión
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun logout(): Result<Boolean> {
        return try {
            val authHeader = tokenManager.getAuthHeader()
            if (authHeader != null) {
                // Notificar al backend
                authApiService.logout(authHeader)
            }

            // Limpiar datos locales
            tokenManager.clearAuthData()
            userProfileDao.deleteUserProfile()

            Result.success(true)
        } catch (e: Exception) {
            // Aunque falle el backend, limpiar datos locales
            tokenManager.clearAuthData()
            userProfileDao.deleteUserProfile()
            Result.success(true)
        }
    }

    // Verificar y refrescar token si es necesario
    suspend fun ensureValidToken(): String? {
        if (tokenManager.isTokenExpired()) {
            // Si el token está expirado, podríamos intentar refrescarlo
            // Por ahora, solo retornamos null para forzar re-login
            return null
        }
        return tokenManager.getAuthHeader()
    }

    // Obtener estado de autenticación
    fun getAuthState() = tokenManager.authState
}

fun UserProfileEntity.toUser(): User {
    return User(
        id = this.id,
        googleId = this.googleId,
        email = this.email,
        name = this.name,
        profilePicture = this.profilePicture,
        role = UserRole.valueOf(this.role),
        isActive = this.isActive,
        createdAt = this.createdAt,
        lastLoginAt = this.lastLoginAt
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun User.toEntity(): UserProfileEntity {
    val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    return UserProfileEntity(
        id = this.id,
        googleId = this.googleId,
        email = this.email,
        name = this.name,
        profilePicture = this.profilePicture,
        role = this.role.name,
        isActive = this.isActive,
        notificationsEnabled = true, // Valor por defecto
        syncEnabled = false,
        authToken = null,
        tokenExpiresAt = null,
        createdAt = this.createdAt.ifEmpty { now },
        lastLoginAt = this.lastLoginAt,
        lastSyncAt = null,
        updatedAt = now
    )
}

