package com.example.academically.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_profile")
data class StudentProfileEntity(
    @PrimaryKey
    val id: Int = 1, // Singleton, solo un perfil por app

    @ColumnInfo(name = "google_user_id")
    val googleUserId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "profile_picture")
    val profilePicture: String? = null,

    @ColumnInfo(name = "notifications_enabled")
    val notificationsEnabled: Boolean = true,

    @ColumnInfo(name = "sync_enabled")
    val syncEnabled: Boolean = false,

    @ColumnInfo(name = "last_sync_at")
    val lastSyncAt: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null
)
