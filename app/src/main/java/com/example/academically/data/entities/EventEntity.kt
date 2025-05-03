package com.example.academically.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
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
    val startDate: String, // Almacenado como string en formato ISO

    @ColumnInfo(name = "end_date")
    val endDate: String, // Almacenado como string en formato ISO

    @ColumnInfo(name = "category_id")
    val categoryId: Int,

    @ColumnInfo(name = "image_path")
    val imagePath: String = "",

    @ColumnInfo(name = "shape")
    val shape: String = "RoundedFull" // Almacenado como string
)