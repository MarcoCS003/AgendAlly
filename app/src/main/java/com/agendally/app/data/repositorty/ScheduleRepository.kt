package com.agendally.app.data.repositorty

import android.os.Build
import androidx.annotation.RequiresApi
import com.agendally.app.data.Schedule
import com.agendally.app.data.dao.ScheduleDao
import com.agendally.app.data.mappers.toDomainModel
import com.agendally.app.data.mappers.toEntity
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun preloadSchedules(sampleSchedules: List<Schedule>) {
        if (sampleSchedules.isNotEmpty()) {
            sampleSchedules.forEach { schedule ->
                try {
                    insertSchedule(schedule as Schedule)
                } catch (e: Exception) {
                    println("Error al insertar actividad: ${e.message}")
                }
            }
        } else {
            println("No se proporcionaron actividades válidas para precargar")
        }

    }
}