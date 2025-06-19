package com.example.academically.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ChannelWithOrganization(
    @Embedded val channel: ChannelEntity,
    @Relation(
        parentColumn = "organization_id",
        entityColumn = "id"
    )
    val organization: OrganizationEntity
)