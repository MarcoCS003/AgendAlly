package com.agendally.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.agendally.app.data.entities.EventEntity
import com.agendally.app.data.entities.EventItemEntity
import com.agendally.app.data.entities.EventNotificationEntity
import com.agendally.app.data.entities.EventWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Transaction
    @Query("SELECT * FROM events WHERE is_active = 1")
    fun getAllEvents(): Flow<List<EventWithDetails>>

    @Transaction
    @Query("SELECT * FROM events")  // Sin filtro, obtiene todos
    fun getAllEventsIncludingHidden(): Flow<List<EventWithDetails>>
    @Transaction
    @Query("SELECT * FROM events WHERE id = :eventId")
    fun getEventById(eventId: Int): Flow<EventWithDetails>

    @Transaction
    @Query("SELECT * FROM events WHERE start_date >= :startDate AND end_date <= :endDate AND is_active = 1")
    fun getEventsBetweenDates(startDate: String, endDate: String): Flow<List<EventWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventItems(items: List<EventItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventNotification(notification: EventNotificationEntity)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Transaction
    suspend fun insertEventWithDetails(
        event: EventEntity,
        items: List<EventItemEntity>,
        notification: EventNotificationEntity?
    ) : Unit {
        val eventId = insertEvent(event).toInt()

        val itemsWithEventId = items.map { it.copy(eventId = eventId) }
        if (itemsWithEventId.isNotEmpty()) {
            insertEventItems(itemsWithEventId)
        }

        notification?.let {
            insertEventNotification(it.copy(eventId = eventId))
        }
    }

    @Transaction
    suspend fun updateEventWithDetails(
        event: EventEntity,
        items: List<EventItemEntity>,
        notification: EventNotificationEntity?
    ) : Unit {
        updateEvent(event)

        // Eliminar items y notificación actuales
        deleteEventItems(event.id)
        deleteEventNotification(event.id)

        // Insertar nuevos items y notificación
        val itemsWithEventId = items.map { it.copy(eventId = event.id) }
        if (itemsWithEventId.isNotEmpty()) {
            insertEventItems(itemsWithEventId)
        }

        notification?.let {
            insertEventNotification(it.copy(eventId = event.id))
        }
    }

    @Query("DELETE FROM event_items WHERE event_id = :eventId")
    suspend fun deleteEventItems(eventId: Int)

    @Query("DELETE FROM event_notifications WHERE event_id = :eventId")
    suspend fun deleteEventNotification(eventId: Int)


    @Query("UPDATE events SET is_active= -is_active WHERE id = :eventId")
    suspend fun hideEvent(eventId: Int)


    // AGREGAR - Query para obtener eventos por organización
    @Query("SELECT * FROM events WHERE organization_id = :organizationId")
    fun getEventsByOrganizationFlow(organizationId: Int): Flow<List<EventEntity>>

    // AGREGAR - Query para eliminar eventos del calendario por organización

    // Opción 2: Si identificas eventos del calendario por tipo
    @Query("DELETE FROM events WHERE category_id = 'CALENDAR_EVENT' AND organization_id = :organizationId")
    suspend fun deleteCalendarEventsByOrganization(organizationId: Int)

    // Opción 3: Eliminar TODOS los eventos de esa organización
    @Query("DELETE FROM events WHERE organization_id = :organizationId")
    suspend fun deleteAllEventsByOrganization(organizationId: Int)


}