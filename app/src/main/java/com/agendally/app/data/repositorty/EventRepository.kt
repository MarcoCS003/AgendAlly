package com.agendally.app.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.agendally.app.data.Event
import com.agendally.app.data.dao.EventDao
import com.agendally.app.data.entities.EventEntity
import com.agendally.app.data.mappers.toDomainModel
import com.agendally.app.data.mappers.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class EventRepository(private val eventDao: EventDao) {
    @RequiresApi(Build.VERSION_CODES.O)
    val allEvents: Flow<List<Event>> = eventDao.getAllEvents()
        .map { events ->
            events.map { it.toDomainModel() }
        }
    @RequiresApi(Build.VERSION_CODES.O)
    val allEventsIncludingHidden: Flow<List<Event>> = eventDao.getAllEventsIncludingHidden()
        .map { events ->
            events.map { it.toDomainModel() }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEventById(eventId: Int): Flow<Event> =
        eventDao.getEventById(eventId)
            .map { it.toDomainModel() }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEventsBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<Event>> =
        eventDao.getEventsBetweenDates(startDate.toString(), endDate.toString())
            .map { events ->
                events.map { it.toDomainModel() }
            }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertEvent(event: Event) {
        val eventEntity = event.toEntity()
        val itemEntities = event.items.map { it.toEntity(0) }
        val notificationEntity = event.notification?.toEntity(0)

        eventDao.insertEventWithDetails(eventEntity, itemEntities, notificationEntity)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateEvent(event: Event) {
        val eventEntity = event.toEntity()
        val itemEntities = event.items.map { it.toEntity(event.id) }
        val notificationEntity = event.notification?.toEntity(event.id)

        eventDao.updateEventWithDetails(eventEntity, itemEntities, notificationEntity)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteEvent(event: Event) {
        eventDao.deleteEvent(event.toEntity())
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun hideEvent(event: Event){
        eventDao.hideEvent(event.id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun preloadEvents(sampleEvents: List<Event>) {
        // Asegurémonos de que estamos recibiendo eventos y no cualquier objeto
        if (sampleEvents.isNotEmpty()) {
            // Insertamos cada evento de la lista en la base de datos
            sampleEvents.forEach { event ->
                try {
                    // Usar el método insertEvent existente
                    insertEvent(event as Event)
                } catch (e: Exception) {
                    // Manejar cualquier error de inserción
                    println("Error al insertar evento: ${e.message}")
                }
            }
            println("Precarga de ${sampleEvents.size} eventos completada")
        } else {
            println("No se proporcionaron eventos válidos para precargar")
        }
    }
    fun getEventsByOrganizationFlow(organizationId: Int): Flow<List<EventEntity>> {
        return eventDao.getEventsByOrganizationFlow(organizationId)
    }


    suspend fun deleteCalendarEventsByOrganization(organizationId: Int) {
        eventDao.deleteCalendarEventsByOrganization(organizationId)
    }
}