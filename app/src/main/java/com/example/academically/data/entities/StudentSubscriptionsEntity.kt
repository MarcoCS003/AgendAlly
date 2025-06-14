package com.example.academically.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "student_subscriptions",
    foreignKeys = [
        ForeignKey(
            entity = StudentProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["student_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["student_id", "channel_id"], unique = true)]
)
data class StudentSubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "student_id")
    val studentId: Int = 1, // Referencia al perfil único

    @ColumnInfo(name = "channel_id")
    val channelId: Int,

    @ColumnInfo(name = "channel_name")
    val channelName: String,

    @ColumnInfo(name = "organization_name")
    val organizationName: String,

    @ColumnInfo(name = "subscribed_at")
    val subscribedAt: String,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "notifications_enabled")
    val notificationsEnabled: Boolean = true
)