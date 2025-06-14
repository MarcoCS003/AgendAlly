package com.example.academically.data.entities

import androidx.room.Embedded
import androidx.room.Relation

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