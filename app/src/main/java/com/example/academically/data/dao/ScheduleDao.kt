package com.example.academically.data.dao

import androidx.room.*
import com.example.academically.data.entities.ScheduleEntity
import com.example.academically.data.entities.ScheduleTimeEntity
import com.example.academically.data.entities.ScheduleWithTimes
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    // Obtener todos los horarios con sus tiempos
    @Transaction
    @Query("SELECT * FROM schedules")
    fun getAllSchedulesWithTimes(): Flow<List<ScheduleWithTimes>>

    // Obtener un horario específico con sus tiempos
    @Transaction
    @Query("SELECT * FROM schedules WHERE id = :scheduleId")
    fun getScheduleWithTimes(scheduleId: Int): Flow<ScheduleWithTimes>

    // Insertar un nuevo horario
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ScheduleEntity): Long

    // Insertar tiempos de horario
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleTimes(times: List<ScheduleTimeEntity>)

    // Actualizar un horario
    @Update
    suspend fun updateSchedule(schedule: ScheduleEntity)

    // Eliminar un horario (esto también eliminará sus tiempos por la relación en cascada)
    @Delete
    suspend fun deleteSchedule(schedule: ScheduleEntity)

    // Eliminar todos los horarios
    @Query("DELETE FROM schedules")
    suspend fun deleteAllSchedules()

    // Eliminar tiempos específicos de un horario
    @Query("DELETE FROM schedule_times WHERE schedule_id = :scheduleId")
    suspend fun deleteScheduleTimes(scheduleId: Int)

    // Transacción para insertar un horario completo con sus tiempos
    @Transaction
    suspend fun insertScheduleWithTimes(schedule: ScheduleEntity, times: List<ScheduleTimeEntity>) {
        val scheduleId = insertSchedule(schedule)
        val timesWithScheduleId = times.map { it.copy(scheduleId = scheduleId.toInt()) }
        insertScheduleTimes(timesWithScheduleId)
    }

    // Transacción para actualizar un horario completo con sus tiempos
    @Transaction
    suspend fun updateScheduleWithTimes(schedule: ScheduleEntity, times: List<ScheduleTimeEntity>) {
        updateSchedule(schedule)
        deleteScheduleTimes(schedule.id)
        insertScheduleTimes(times.map { it.copy(scheduleId = schedule.id) })
    }
}