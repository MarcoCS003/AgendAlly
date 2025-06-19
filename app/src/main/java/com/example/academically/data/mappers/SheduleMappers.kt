package com.example.academically.data.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.academically.data.model.Schedule
import com.example.academically.data.model.ScheduleTime
import com.example.academically.data.local.entities.ScheduleEntity
import com.example.academically.data.local.entities.ScheduleTimeEntity
import com.example.academically.data.local.entities.ScheduleWithTimes
import com.example.academically.uiAcademicAlly.calendar.DaysOfWeek
import java.time.LocalTime

// Conversiones de Schedule a Entity
fun Schedule.toEntity(): ScheduleEntity {
    return ScheduleEntity(
        id = this.id,
        colorIndex = this.colorIndex,
        name = this.name,
        place = this.place,
        teacher = this.teacher
    )
}

// Conversiones de ScheduleTime a Entity
@RequiresApi(Build.VERSION_CODES.O)
fun ScheduleTime.toEntity(scheduleId: Int): ScheduleTimeEntity {
    return ScheduleTimeEntity(
        scheduleId = scheduleId,
        day = this.day.name,
        hourStart = this.hourStart.toString(), // Formato HH:mm
        hourEnd = this.hourEnd.toString()
    )
}

// Conversiones de Entity a modelo de dominio
@RequiresApi(Build.VERSION_CODES.O)
fun ScheduleWithTimes.toDomainModel(): Schedule {
    return Schedule(
        id = this.schedule.id,
        colorIndex = this.schedule.colorIndex,
        name = this.schedule.name,
        place = this.schedule.place,
        teacher = this.schedule.teacher,
        times = this.times.map { it.toDomainModel() }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
private fun ScheduleTimeEntity.toDomainModel(): ScheduleTime {
    return ScheduleTime(
        day = DaysOfWeek.valueOf(this.day),
        hourStart = LocalTime.parse(this.hourStart),
        hourEnd = LocalTime.parse(this.hourEnd)
    )
}


