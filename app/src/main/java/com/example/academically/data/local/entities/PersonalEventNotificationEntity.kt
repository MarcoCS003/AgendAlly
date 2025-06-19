package com.example.academically.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.academically.data.local.entities.PersonalEventEntity

@Entity(
    tableName = "personal_event_notifications",
    foreignKeys = [
        ForeignKey(
            entity = PersonalEventEntity::class,
            parentColumns = ["id"],
            childColumns = ["event_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["event_id"])]
)
data class PersonalEventNotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "event_id")
    val eventId: Int,

    @ColumnInfo(name = "minutes_before")
    val minutesBefore: Int, // Convertiremos milisegundos a minutos

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "message")
    val message: String,

    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = true,

    @ColumnInfo(name = "notification_type")
    val notificationType: String = "LOCAL" // LOCAL, PUSH, etc.
)