package com.example.academically.data.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.academically.data.local.entities.UserProfileEntity
import com.example.academically.data.model.User
import com.example.academically.data.model.UserRole
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
        notificationsEnabled = true,
        syncEnabled = false,
        authToken = null,
        tokenExpiresAt = null,
        createdAt = this.createdAt.ifEmpty { now },
        lastLoginAt = this.lastLoginAt,
        lastSyncAt = null,
        updatedAt = now
    )
}