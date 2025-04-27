package com.example.academically.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "schedule_times",
    foreignKeys = [
        ForeignKey(
            entity = ScheduleEntity::class,
            parentColumns = ["id"],
            childColumns = ["schedule_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("schedule_id")]
)
data class ScheduleTimeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "schedule_id")
    val scheduleId: Int,

    @ColumnInfo(name = "day")
    val day: String, // Almacenaremos el día como String (nombre del enum)

    @ColumnInfo(name = "hour_start")
    val hourStart: String, // Tiempo en formato HH:mm

    @ColumnInfo(name = "hour_end")
    val hourEnd: String // Tiempo en formato HH:mm
)
