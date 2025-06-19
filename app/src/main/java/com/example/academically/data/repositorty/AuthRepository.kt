package com.example.academically.data.repositorty


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.academically.data.local.dao.UserProfileDao
import com.example.academically.data.model.User
import com.example.academically.data.model.UserRole
import com.example.academically.data.local.entities.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.ExperimentalTime


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



@Singleton
class AuthRepository @Inject constructor(
    private val userProfileDao: UserProfileDao
) {

    // Obtener perfil del usuario actual
    fun getCurrentUserProfile(): Flow<User?> {
        return userProfileDao.getUserProfile().map { entity ->
            entity?.toUser()
        }
    }
    // Verificar si hay usuario logueado
    suspend fun isUserLoggedIn(): Boolean {
        return userProfileDao.isUserLoggedIn()
    }

    // Obtener token válido
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getValidAuthToken(): String? {
        val currentTime = LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        return userProfileDao.getValidAuthToken(currentTime)
    }

    // Iniciar sesión con credenciales
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loginWithCredentials(email: String, password: String): Result<User> {
        return try {
            // TODO: Aquí iría la llamada al backend real
            // Por ahora simulamos validación básica

            if (email.isBlank() || password.isBlank()) {
                return Result.failure(Exception("Email y contraseña son requeridos"))
            }

            if (password.length < 6) {
                return Result.failure(Exception("Contraseña debe tener al menos 6 caracteres"))
            }

            // Simulamos una respuesta exitosa
            val user = createMockUser(email)
            saveUserProfile(user, "mock_token_${System.currentTimeMillis()}", "2025-12-31T23:59:59")

            Log.d("AuthRepository", "Login exitoso para: $email")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en login: ${e.message}")
            Result.failure(e)
        }
    }

    // Registrar nuevo usuario
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun registerUser(name: String, email: String, password: String): Result<User> {
        return try {
            // Validaciones básicas
            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                return Result.failure(Exception("Todos los campos son requeridos"))
            }

            if (name.length < 2) {
                return Result.failure(Exception("El nombre debe tener al menos 2 caracteres"))
            }

            if (password.length < 6) {
                return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
            }

            // TODO: Verificar si el email ya existe en el backend

            // Crear usuario
            val user = createMockUser(email, name)
            saveUserProfile(user, "mock_token_${System.currentTimeMillis()}", "2025-12-31T23:59:59")

            Log.d("AuthRepository", "Registro exitoso para: $email")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en registro: ${e.message}")
            Result.failure(e)
        }
    }

        // Iniciar sesión con Google
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loginWithGoogle(idToken: String): Result<User> {
        return try {
            if (idToken.isBlank()) {
                return Result.failure(Exception("Token de Google inválido"))
            }

            // TODO: Verificar el idToken con Google y el backend

            // Por ahora simulamos
            val user = createMockUser("google@example.com", "Usuario Google")
            saveUserProfile(user, "google_token_${System.currentTimeMillis()}", "2025-12-31T23:59:59")

            Log.d("AuthRepository", "Login con Google exitoso")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en Google login: ${e.message}")
            Result.failure(e)
        }
    }

    // Guardar perfil de usuario después del login
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun saveUserProfile(user: User, token: String, expiresAt: String) {
        val entity = user.toEntity().copy(
            authToken = token,
            tokenExpiresAt = expiresAt,
            lastLoginAt = LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        userProfileDao.insertUserProfile(entity)
    }

    // Cerrar sesión
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun logout() {
        try {
            val currentTime = LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .toString()
            userProfileDao.logout(currentTime)
        } catch (e: Exception) {
            // Log o manejo del error
            Log.e("Logout", "Error al cerrar sesión", e)
        }
    }

    // Actualizar configuraciones
    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        userProfileDao.updateNotificationsEnabled(enabled)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateSyncSettings(enabled: Boolean) {
        val syncTime = if (enabled) {
            LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } else null
        userProfileDao.updateSyncSettings(enabled, syncTime)
    }

    // Actualizar perfil
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateUserProfile(user: User) {
        val entity = user.toEntity().copy(
            updatedAt = LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        userProfileDao.updateUserProfile(entity)
    }

    // Función auxiliar para crear usuario mock
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createMockUser(email: String, name: String = "Usuario"): User {
        val now = LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        return User(
            id = 1, // Solo un perfil por app
            googleId = "mock_google_id_${System.currentTimeMillis()}",
            email = email,
            name = name,
            profilePicture = null,
            role = UserRole.STUDENT,
            isActive = true,
            createdAt = now,
            lastLoginAt = now
        )
    }

    // ✅ NUEVO: Verificar si existe un perfil
    suspend fun hasUserProfile(): Boolean {
        return try {
            userProfileDao.isUserLoggedIn()
        } catch (e: Exception) {
            false
        }
    }


    suspend fun getCurrentUser(): User? {
        return try {
            userProfileDao
                .getUserProfile()
                .map { it?.toUser() }
                .firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

}