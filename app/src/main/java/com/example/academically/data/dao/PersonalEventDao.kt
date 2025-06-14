package com.example.academically.data.dao

import androidx.room.*
import com.example.academically.data.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalEventDao {

    // ========== CONSULTAS BÁSICAS ==========
    @Transaction
    @Query("SELECT * FROM personal_events WHERE is_visible = 1 ORDER BY start_date ASC")
    fun getAllVisibleEvents(): Flow<List<PersonalEventWithDetails>>

    @Transaction
    @Query("SELECT * FROM personal_events WHERE id = :eventId")
    fun getEventById(eventId: Int): Flow<PersonalEventWithDetails?>

    @Transaction
    @Query("SELECT * FROM personal_events WHERE type = :type AND is_visible = 1 ORDER BY start_date ASC")
    fun getEventsByType(type: String): Flow<List<PersonalEventWithDetails>>

    // ========== CONSULTAS POR FECHA ==========
    @Transaction
    @Query("SELECT * FROM personal_events WHERE start_date <= :date AND end_date >= :date AND is_visible = 1 ORDER BY start_date ASC")
    fun getEventsForDate(date: String): Flow<List<PersonalEventWithDetails>>

    @Transaction
    @Query("SELECT * FROM personal_events WHERE start_date >= :startDate AND start_date <= :endDate AND is_visible = 1 ORDER BY start_date ASC")
    fun getEventsBetweenDates(startDate: String, endDate: String): Flow<List<PersonalEventWithDetails>>

    @Transaction
    @Query("SELECT * FROM personal_events WHERE substr(start_date, 1, 7) = :yearMonth AND is_visible = 1 ORDER BY start_date ASC")
    fun getEventsForMonth(yearMonth: String): Flow<List<PersonalEventWithDetails>>

    @Transaction
    @Query("SELECT * FROM personal_events WHERE start_date >= date('now') AND is_visible = 1 ORDER BY start_date ASC LIMIT 10")
    fun getUpcomingEvents(): Flow<List<PersonalEventWithDetails>>

    // ========== CONSULTAS POR PRIORIDAD Y FILTROS ==========
    @Transaction
    @Query("SELECT * FROM personal_events WHERE priority IN (:priorities) AND is_visible = 1 ORDER BY start_date ASC")
    fun getEventsByPriorities(priorities: List<String>): Flow<List<PersonalEventWithDetails>>

    @Transaction
    @Query("SELECT * FROM personal_events WHERE institutional_event_id = :institutionalEventId")
    fun getEventsByInstitutionalId(institutionalEventId: Int): Flow<List<PersonalEventWithDetails>>

    @Query("SELECT DISTINCT tags FROM personal_events WHERE tags != '[]' AND tags != '' AND tags IS NOT NULL")
    fun getAllTags(): Flow<List<String>>

    // ========== OPERACIONES DE INSERCIÓN ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: PersonalEventEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventItems(items: List<EventItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventNotification(notification: PersonalEventNotificationEntity)

    @Transaction
    suspend fun insertEventWithDetails(
        event: PersonalEventEntity,
        items: List<EventItemEntity>,
        notification: PersonalEventNotificationEntity?
    ) {
        val eventId = insertEvent(event).toInt()

        // Insertar items con el eventId correcto
        val itemsWithEventId = items.map { it.copy(eventId = eventId) }
        if (itemsWithEventId.isNotEmpty()) {
            insertEventItems(itemsWithEventId)
        }

        // Insertar notificación con el eventId correcto
        notification?.let {
            insertEventNotification(it.copy(eventId = eventId))
        }
    }

    // ========== OPERACIONES DE ACTUALIZACIÓN ==========
    @Update
    suspend fun updateEvent(event: PersonalEventEntity)

    @Query("UPDATE personal_events SET is_visible = :isVisible WHERE id = :eventId")
    suspend fun updateEventVisibility(eventId: Int, isVisible: Boolean)

    @Query("UPDATE personal_events SET is_completed = :isCompleted WHERE id = :eventId")
    suspend fun updateEventCompletion(eventId: Int, isCompleted: Boolean)

    @Query("UPDATE personal_events SET is_visible = 0 WHERE institutional_event_id = :institutionalEventId")
    suspend fun hideInstitutionalEvent(institutionalEventId: Int)

    @Transaction
    suspend fun updateEventWithDetails(
        event: PersonalEventEntity,
        items: List<EventItemEntity>,
        notification: PersonalEventNotificationEntity?
    ) {
        updateEvent(event)

        // Eliminar items y notificación existentes
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

    // ========== OPERACIONES DE ELIMINACIÓN ==========
    @Delete
    suspend fun deleteEvent(event: PersonalEventEntity)

    @Query("DELETE FROM personal_event_items WHERE event_id = :eventId")
    suspend fun deleteEventItems(eventId: Int)

    @Query("DELETE FROM personal_event_notifications WHERE event_id = :eventId")
    suspend fun deleteEventNotification(eventId: Int)

    @Query("DELETE FROM personal_events WHERE institutional_event_id = :institutionalEventId")
    suspend fun deleteInstitutionalEvent(institutionalEventId: Int)

    // ========== GESTIÓN DE PERFIL ESTUDIANTIL ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentProfile(profile: StudentProfileEntity)

    @Query("SELECT * FROM student_profile WHERE id = 1")
    fun getStudentProfile(): Flow<StudentProfileEntity?>

    @Update
    suspend fun updateStudentProfile(profile: StudentProfileEntity)

    // ========== GESTIÓN DE SUSCRIPCIONES ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: StudentSubscriptionEntity)

    @Query("SELECT * FROM student_subscriptions WHERE is_active = 1")
    fun getActiveSubscriptions(): Flow<List<StudentSubscriptionEntity>>

    @Query("UPDATE student_subscriptions SET is_active = 0 WHERE channel_id = :channelId")
    suspend fun unsubscribeFromChannel(channelId: Int)

    @Query("UPDATE student_subscriptions SET notifications_enabled = :enabled WHERE channel_id = :channelId")
    suspend fun updateChannelNotifications(channelId: Int, enabled: Boolean)

    @Delete
    suspend fun deleteSubscription(subscription: StudentSubscriptionEntity)

    // ========== CONSULTAS DE ESTADÍSTICAS ==========
    @Query("SELECT COUNT(*) FROM personal_events WHERE is_visible = 1")
    suspend fun getTotalVisibleEvents(): Int

    @Query("SELECT COUNT(*) FROM personal_events WHERE type = :type AND is_visible = 1")
    suspend fun getEventCountByType(type: String): Int

    @Query("SELECT COUNT(*) FROM personal_events WHERE start_date >= date('now') AND is_visible = 1")
    suspend fun getUpcomingEventCount(): Int

    @Query("SELECT COUNT(*) FROM personal_events WHERE start_date = date('now') AND is_visible = 1")
    suspend fun getTodayEventCount(): Int

    // ========== CONSULTAS ADICIONALES ÚTILES ==========
    @Transaction
    @Query("SELECT * FROM personal_events WHERE is_visible = 1 AND start_date BETWEEN date('now') AND date('now', '+7 days') ORDER BY start_date ASC")
    fun getThisWeekEvents(): Flow<List<PersonalEventWithDetails>>

    @Transaction
    @Query("SELECT * FROM personal_events WHERE is_visible = 1 AND start_date = date('now') ORDER BY start_date ASC")
    fun getTodayEvents(): Flow<List<PersonalEventWithDetails>>

    @Query("SELECT COUNT(*) FROM personal_events WHERE is_visible = 1 AND start_date BETWEEN date('now') AND date('now', '+7 days')")
    suspend fun getThisWeekEventCount(): Int

    // ========== BÚSQUEDA ==========
    @Transaction
    @Query("SELECT * FROM personal_events WHERE (title LIKE '%' || :searchQuery || '%' OR short_description LIKE '%' || :searchQuery || '%') AND is_visible = 1 ORDER BY start_date ASC")
    fun searchEvents(searchQuery: String): Flow<List<PersonalEventWithDetails>>

    // ========== CONSULTAS POR RANGO DE FECHAS MÁS ESPECÍFICAS ==========
    @Transaction
    @Query("SELECT * FROM personal_events WHERE start_date >= :startDate AND end_date <= :endDate AND is_visible = 1 ORDER BY start_date ASC")
    fun getEventsInDateRange(startDate: String, endDate: String): Flow<List<PersonalEventWithDetails>>

    @Transaction
    @Query("SELECT * FROM personal_events WHERE (start_date <= :date AND end_date >= :date) AND is_visible = 1 ORDER BY start_date ASC")
    fun getEventsOccurringOnDate(date: String): Flow<List<PersonalEventWithDetails>>
}