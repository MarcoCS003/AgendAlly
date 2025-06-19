package com.example.academically.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personal_events")
data class PersonalEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "short_description")
    val shortDescription: String = "",

    @ColumnInfo(name = "long_description")
    val longDescription: String = "",

    @ColumnInfo(name = "location")
    val location: String = "",

    @ColumnInfo(name = "color_index")
    val colorIndex: Int,

    @ColumnInfo(name = "start_date")
    val startDate: String, // LocalDate como String en formato ISO

    @ColumnInfo(name = "end_date")
    val endDate: String,   // LocalDate como String en formato ISO

    @ColumnInfo(name = "type")
    val type: String,      // PersonalEventType como String

    @ColumnInfo(name = "priority")
    val priority: String = "MEDIUM", // AÑADIDA: LOW, MEDIUM, HIGH, URGENT

    @ColumnInfo(name = "institutional_event_id")
    val institutionalEventId: Int? = null,

    @ColumnInfo(name = "image_path")
    val imagePath: String = "",

    @ColumnInfo(name = "tags")
    val tags: String = "[]", // JSON array de strings

    @ColumnInfo(name = "is_visible")
    val isVisible: Boolean = true,

    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false, // AÑADIDA: Para marcar eventos como completados

    @ColumnInfo(name = "shape")
    val shape: String = "RoundedFull",

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null
)