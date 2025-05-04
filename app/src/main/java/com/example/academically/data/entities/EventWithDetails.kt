package com.example.academically.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class EventWithDetails(
    @Embedded val event: EventEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "event_id"
    )
    val items: List<EventItemEntity> = emptyList(),

    @Relation(
        parentColumn = "id",
        entityColumn = "event_id"
    )
    val notification: EventNotificationEntity? = null
)