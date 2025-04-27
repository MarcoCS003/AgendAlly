package com.example.academically.data.repositorty

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.academically.data.Schedule
import com.example.academically.data.dao.ScheduleDao
import com.example.academically.data.mappers.toDomainModel
import com.example.academically.data.mappers.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScheduleRepository(private val scheduleDao: ScheduleDao) {

    @RequiresApi(Build.VERSION_CODES.O)
    val allSchedulesWithTimes: Flow<List<Schedule>> = scheduleDao.getAllSchedulesWithTimes()
        .map { schedulesWithTimes ->
            schedulesWithTimes.map { it.toDomainModel() }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getScheduleWithTimes(scheduleId: Int): Flow<Schedule> =
        scheduleDao.getScheduleWithTimes(scheduleId)
            .map { it.toDomainModel() }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertSchedule(schedule: Schedule) {
        val scheduleEntity = schedule.toEntity()
        val timeEntities = schedule.times.map { it.toEntity(0) } // El ID se asignará después
        scheduleDao.insertScheduleWithTimes(scheduleEntity, timeEntities)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateSchedule(schedule: Schedule) {
        val scheduleEntity = schedule.toEntity()
        val timeEntities = schedule.times.map { it.toEntity(schedule.id) }
        scheduleDao.updateScheduleWithTimes(scheduleEntity, timeEntities)
    }

    suspend fun deleteSchedule(schedule: Schedule) {
        scheduleDao.deleteSchedule(schedule.toEntity())
    }

    suspend fun deleteAllSchedules() {
        scheduleDao.deleteAllSchedules()
    }
}