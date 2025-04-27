package com.example.academically.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "color")
    val color: Int, // Se almacenará como un entero representando el color

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "place")
    val place: String,

    @ColumnInfo(name = "teacher")
    val teacher: String
)