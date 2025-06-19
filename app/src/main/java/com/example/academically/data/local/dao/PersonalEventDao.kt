package com.example.academically.data.local.dao

import androidx.room.*
import com.example.academically.data.local.entities.EventItemEntity
import com.example.academically.data.local.entities.PersonalEventEntity
import com.example.academically.data.local.entities.PersonalEventNotificationEntity
import com.example.academically.data.local.entities.PersonalEventWithDetails
import com.example.academically.data.local.entities.StudentSubscriptionEntity
import com.example.academically.data.local.entities.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalEventDao {

    // ========== CONSULTAS BÁSICAS ==========
    @Query("SELECT * FROM personal_events WHERE id = :userId AND is_visible = 1 ORDER BY start_date ASC")
    fun getAllVisibleEvents(userId: Int = 1): Flow<List<PersonalEventEntity>>

    @Query("SELECT * FROM personal_events WHERE id = :eventId AND id = :userId")
    fun getEventById(eventId: Int, userId: Int = 1): Flow<PersonalEventEntity?>

    @Transaction
    @Query("SELECT * FROM personal_events WHERE id = :userId AND is_visible = 1 ORDER BY start_date ASC")
    fun getUserEventsWithDetails(userId: Int = 1): Flow<List<PersonalEventWithDetails>>

    @Transaction
    @Query("SELECT * FROM personal_events WHERE id = :eventId AND id = :userId")
    suspend fun getEventWithDetails(eventId: Int, userId: Int = 1): PersonalEventWithDetails?

    // ========== CONSULTAS POR FECHA ==========
    @Query("SELECT * FROM personal_events WHERE id = :userId AND is_visible = 1 AND start_date = :date ORDER BY start_date ASC")
    fun getEventsForDate(date: String, userId: Int = 1): Flow<List<PersonalEventEntity>>

    @Query("SELECT * FROM personal_events WHERE id = :userId AND is_visible = 1 AND start_date >= :startDate AND end_date <= :endDate ORDER BY start_date ASC")
    fun getEventsBetweenDates(startDate: String, endDate: String, userId: Int = 1): Flow<List<PersonalEventEntity>>

    @Query("SELECT * FROM personal_events WHERE id = :userId AND is_visible = 1 AND strftime('%Y-%m', start_date) = :yearMonth ORDER BY start_date ASC")
    fun getEventsForMonth(yearMonth: String, userId: Int = 1): Flow<List<PersonalEventEntity>>

    @Query("SELECT * FROM personal_events WHERE id = :userId AND is_visible = 1 AND date(start_date) >= date('now') ORDER BY start_date ASC LIMIT 10")
    fun getUpcomingEvents(userId: Int = 1): Flow<List<PersonalEventEntity>>

    @Query("SELECT * FROM personal_events WHERE id = :userId AND is_visible = 1 AND date(start_date) = date('now') ORDER BY start_date ASC")
    fun getTodayEvents(userId: Int = 1): Flow<List<PersonalEventEntity>>

    @Query("SELECT * FROM personal_events WHERE id = :userId AND is_visible = 1 AND date(start_date) >= date('now') AND date(start_date) <= date('now', '+7 days') ORDER BY start_date ASC")
    fun getThisWeekEvents(userId: Int = 1): Flow<List<PersonalEventEntity>>

    @Query("SELECT * FROM personal_events WHERE id = :userId AND is_visible = 1 AND start_date >= :startDate AND end_date <= :endDate ORDER BY start_date ASC")
    fun getEventsInDateRange(startDate: String, endDate: String, userId: Int = 1): Flow<List<PersonalEventEntity>>

    @Query("SELECT * FROM personal_events WHERE id = :userId AND is_visible = 1 AND (start_date <= :date AND end_date >= :date) ORDER BY start_date ASC")
    fun getEventsOccurringOnDate(date: String, userId: Int = 1): Flow<List<PersonalEventEntity>>

    // ========== CONSULTAS POR TIPO ==========
    @Query("SELECT * FROM personal_events WHERE id = :userId AND type = :type AND is_visible = 1 ORDER BY start_date ASC")
    fun getEventsByType(type: String, userId: Int = 1): Flow<List<PersonalEventEntity>>

    @Query("SELECT * FROM personal_events WHERE id = :userId AND priority IN (:priorities) AND is_visible = 1 ORDER BY start_date ASC")
    fun getEventsByPriorities(priorities: List<String>, userId: Int = 1): Flow<List<PersonalEventEntity>>

    @Query("SELECT * FROM personal_events WHERE id = :userId AND institutional_event_id = :institutionalEventId AND is_visible = 1 ORDER BY start_date ASC")
    fun getEventsByInstitutionalId(institutionalEventId: Int, userId: Int = 1): Flow<List<PersonalEventEntity>>

    // ========== BÚSQUEDA ==========
    @Query("SELECT * FROM personal_events WHERE id = :userId AND is_visible = 1 AND (title LIKE :query OR short_description LIKE :query OR long_description LIKE :query) ORDER BY start_date ASC")
    fun searchEvents(query: String, userId: Int = 1): Flow<List<PersonalEventEntity>>

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
    ): Long {
        val eventId = insertEvent(event)
        val itemsWithEventId = items.map { it.copy(eventId = eventId.toInt()) }
        if (itemsWithEventId.isNotEmpty()) {
            insertEventItems(itemsWithEventId)
        }
        notification?.let {
            insertEventNotification(it.copy(eventId = eventId.toInt()))
        }
        return eventId
    }

    // ========== OPERACIONES DE ACTUALIZACIÓN ==========
    @Update
    suspend fun updateEvent(event: PersonalEventEntity)

    @Query("DELETE FROM personal_event_items WHERE event_id = :eventId")
    suspend fun deleteEventItems(eventId: Int)

    @Query("DELETE FROM personal_event_notifications WHERE event_id = :eventId")
    suspend fun deleteEventNotification(eventId: Int)

    @Transaction
    suspend fun updateEventWithDetails(
        event: PersonalEventEntity,
        items: List<EventItemEntity>,
        notification: PersonalEventNotificationEntity?
    ) {
        updateEvent(event)
        deleteEventItems(event.id)
        deleteEventNotification(event.id)

        val itemsWithEventId = items.map { it.copy(eventId = event.id) }
        if (itemsWithEventId.isNotEmpty()) {
            insertEventItems(itemsWithEventId)
        }
        notification?.let {
            insertEventNotification(it.copy(eventId = event.id))
        }
    }

    @Query("UPDATE personal_events SET is_visible = :isVisible WHERE id = :eventId AND id = :userId")
    suspend fun updateEventVisibility(eventId: Int, isVisible: Boolean, userId: Int = 1)

    @Query("UPDATE personal_events SET is_completed = :isCompleted WHERE id = :eventId AND id = :userId")
    suspend fun updateEventCompletion(eventId: Int, isCompleted: Boolean, userId: Int = 1)

    // ========== OPERACIONES DE ELIMINACIÓN ==========
    @Delete
    suspend fun deleteEvent(event: PersonalEventEntity)

    @Query("DELETE FROM personal_events WHERE id = :eventId AND id = :userId")
    suspend fun deleteEventById(eventId: Int, userId: Int = 1)

    @Query("DELETE FROM personal_events WHERE id = :userId")
    suspend fun deleteAllUserEvents(userId: Int = 1)

    @Query("UPDATE personal_events SET is_visible = 0 WHERE institutional_event_id = :institutionalEventId AND id = :userId")
    suspend fun hideInstitutionalEvent(institutionalEventId: Int, userId: Int = 1)

    @Query("DELETE FROM personal_events WHERE institutional_event_id = :institutionalEventId AND id = :userId")
    suspend fun deleteInstitutionalEvent(institutionalEventId: Int, userId: Int = 1)

    // ========== ESTADÍSTICAS ==========
    @Query("SELECT COUNT(*) FROM personal_events WHERE id = :userId AND is_visible = 1")
    suspend fun getTotalVisibleEvents(userId: Int = 1): Int

    @Query("SELECT COUNT(*) FROM personal_events WHERE id = :userId AND type = :type AND is_visible = 1")
    suspend fun getEventCountByType(type: String, userId: Int = 1): Int

    @Query("SELECT COUNT(*) FROM personal_events WHERE id = :userId AND is_visible = 1 AND date(start_date) = date('now')")
    suspend fun getTodayEventCount(userId: Int = 1): Int

    @Query("SELECT COUNT(*) FROM personal_events WHERE id = :userId AND is_visible = 1 AND date(start_date) >= date('now')")
    suspend fun getUpcomingEventCount(userId: Int = 1): Int

    @Query("SELECT COUNT(*) FROM personal_events WHERE id = :userId AND is_visible = 1 AND date(start_date) >= date('now') AND date(start_date) <= date('now', '+7 days')")
    suspend fun getThisWeekEventCount(userId: Int = 1): Int

    // ========== OPERACIONES DE TAGS ==========
    @Query("SELECT DISTINCT tags FROM personal_events WHERE id = :userId AND is_visible = 1 AND tags != '[]'")
    fun getAllTags(userId: Int = 1): Flow<List<String>>

    // ========== OPERACIONES PARA PERFIL ESTUDIANTIL LOCAL ==========
    // Nota: Estos métodos son temporales hasta que implementes el API
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getStudentProfile(): Flow<UserProfileEntity?>

    @Update
    suspend fun updateStudentProfile(profile: UserProfileEntity)

    // ========== OPERACIONES PARA SUSCRIPCIONES LOCALES ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: StudentSubscriptionEntity)

    @Query("SELECT * FROM student_subscriptions WHERE user_id = :userId AND is_active = 1")
    fun getActiveSubscriptions(userId: Int = 1): Flow<List<StudentSubscriptionEntity>>

    @Query("UPDATE student_subscriptions SET is_active = 0 WHERE channel_id = :channelId AND user_id = :userId")
    suspend fun unsubscribeFromChannel(channelId: Int, userId: Int = 1)

    @Query("UPDATE student_subscriptions SET notifications_enabled = :enabled WHERE channel_id = :channelId AND user_id = :userId")
    suspend fun updateChannelNotifications(channelId: Int, enabled: Boolean, userId: Int = 1)

    @Delete
    suspend fun deleteSubscription(subscription: StudentSubscriptionEntity)
}