package com.example.academically.data
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
enum class UserRole {
    SUPER_ADMIN,    // Jefe que autoriza admins
    ADMIN,          // Admin de organización específica
    STUDENT         // Estudiante que consume eventos
}

@Serializable
data class User(
    val id: Int,
    val googleId: String,           // ID de Google OAuth
    val email: String,
    val name: String,
    val profilePicture: String? = null,
    val role: UserRole,
    val isActive: Boolean = true,
    val createdAt: String,
    val lastLoginAt: String? = null
)
