package com.example.academically.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "channels_cache",
    foreignKeys = [
        ForeignKey(
            entity = OrganizationEntity::class,
            parentColumns = ["id"],
            childColumns = ["organization_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["organization_id"]),
        Index(value = ["organization_id", "acronym"], unique = true)
    ]
)
data class ChannelEntity(
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "organization_id")
    val organizationId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "acronym")
    val acronym: String,

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "type")
    val type: String, // CAREER, DEPARTMENT, etc.

    @ColumnInfo(name = "email")
    val email: String? = null,

    @ColumnInfo(name = "phone")
    val phone: String? = null,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "cached_at")
    val cachedAt: String,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null
)