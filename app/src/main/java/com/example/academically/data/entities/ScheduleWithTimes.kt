package com.example.academically.data.entities


import androidx.room.Embedded
import androidx.room.Relation

data class ScheduleWithTimes(
    @Embedded val schedule: ScheduleEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "schedule_id"
    )
    val times: List<ScheduleTimeEntity>
)