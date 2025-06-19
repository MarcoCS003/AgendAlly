package com.example.academically.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_profile",
    indices = [Index(value = ["google_id"], unique = true)]
)
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1, // Solo un perfil por app

    @ColumnInfo(name = "google_id")
    val googleId: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "profile_picture")
    val profilePicture: String? = null,

    @ColumnInfo(name = "role")
    val role: String, // Guardamos como String para serialización

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "notifications_enabled")
    val notificationsEnabled: Boolean = true,

    @ColumnInfo(name = "sync_enabled")
    val syncEnabled: Boolean = false,

    @ColumnInfo(name = "auth_token")
    val authToken: String? = null, // JWT del backend

    @ColumnInfo(name = "token_expires_at")
    val tokenExpiresAt: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "last_login_at")
    val lastLoginAt: String? = null,

    @ColumnInfo(name = "last_sync_at")
    val lastSyncAt: String? = null,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null
) {

}