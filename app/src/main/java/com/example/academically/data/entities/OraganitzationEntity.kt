package com.example.academically.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "organizations")
data class OrganizationEntity(
    @PrimaryKey
    @ColumnInfo(name = "organization_id")
    val organizationID: Int,

    @ColumnInfo(name = "acronym")
    val acronym: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "phone")
    val phone: String,

    @ColumnInfo(name = "student_number")
    val studentNumber: Int,

    @ColumnInfo(name = "teacher_number")
    val teacherNumber: Int,

    @ColumnInfo(name = "logo_url")
    val logoUrl: String? = null,

    @ColumnInfo(name = "web_site")
    val webSite: String? = null,

    @ColumnInfo(name = "facebook")
    val facebook: String? = null,

    @ColumnInfo(name = "instagram")
    val instagram: String? = null,

    @ColumnInfo(name = "twitter")
    val twitter: String? = null,

    @ColumnInfo(name = "youtube")
    val youtube: String? = null,

    @ColumnInfo(name = "linkedin")
    val linkedin: String? = null,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: String? = null,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null
)