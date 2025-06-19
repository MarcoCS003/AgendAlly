package com.example.academically.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.example.academically.data.local.entities.EventItemEntity
import com.example.academically.data.local.entities.PersonalEventEntity
import com.example.academically.data.local.entities.PersonalEventNotificationEntity

data class PersonalEventWithDetails(
    @Embedded val event: PersonalEventEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "event_id"
    )
    val items: List<EventItemEntity> = emptyList(),

    @Relation(
        parentColumn = "id",
        entityColumn = "event_id"
    )
    val notification: PersonalEventNotificationEntity? = null
)