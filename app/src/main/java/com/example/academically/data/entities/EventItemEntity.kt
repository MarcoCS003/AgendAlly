package com.example.academically.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "event_items",
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = ["id"],
            childColumns = ["event_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EventItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "event_id")
    val eventId: Int,

    @ColumnInfo(name = "icon_name")
    val iconName: String, // Nombre del icono para recuperarlo después

    @ColumnInfo(name = "text")
    val text: String
)