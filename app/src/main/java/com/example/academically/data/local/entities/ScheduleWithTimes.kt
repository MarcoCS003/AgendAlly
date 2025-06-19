package com.example.academically.data.local.entities


import androidx.room.Embedded
import androidx.room.Relation
import com.example.academically.data.local.entities.ScheduleEntity
import com.example.academically.data.local.entities.ScheduleTimeEntity

data class ScheduleWithTimes(
    @Embedded val schedule: ScheduleEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "schedule_id"
    )
    val times: List<ScheduleTimeEntity>
)