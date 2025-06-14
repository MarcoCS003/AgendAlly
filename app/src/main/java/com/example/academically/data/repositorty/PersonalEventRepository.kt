package com.example.academically.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.academically.data.*
import com.example.academically.data.dao.PersonalEventDao
import com.example.academically.data.mappers.*
import com.example.academically.data.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PersonalEventRepository(private val personalEventDao: PersonalEventDao) {

    // ========== OPERACIONES DE LECTURA DE EVENTOS ==========
    @RequiresApi(Build.VERSION_CODES.O)
    val allEvents: Flow<List<PersonalEvent>> = personalEventDao.getAllVisibleEvents()
        .map { events -> events.map { it.toDomainModel() } }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEventById(eventId: Int): Flow<PersonalEvent?> =
        personalEventDao.getEventById(eventId)
            .map { it?.toDomainModel() }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEventsByType(type: PersonalEventType): Flow<List<PersonalEvent>> =
        personalEventDao.getEventsByType(type.name)
            .map { events -> events.map { it.toDomainModel() } }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEventsForDate(date: LocalDate): Flow<List<PersonalEvent>> =
        personalEventDao.getEventsForDate(date.toString())
            .map { events -> events.map { it.toDomainModel() } }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEventsBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<PersonalEvent>> =
        personalEventDao.getEventsBetweenDates(startDate.toString(), endDate.toString())
            .map { events -> events.map { it.toDomainModel() } }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEventsForMonth(year: Int, month: Int): Flow<List<PersonalEvent>> {
        val yearMonth = String.format("%04d-%02d", year, month)
        return personalEventDao.getEventsForMonth(yearMonth)
            .map { events -> events.map { it.toDomainModel() } }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getUpcomingEvents(): Flow<List<PersonalEvent>> =
        personalEventDao.getUpcomingEvents()
            .map { events -> events.map { it.toDomainModel() } }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodayEvents(): Flow<List<PersonalEvent>> =
        personalEventDao.getTodayEvents()
            .map { events -> events.map { it.toDomainModel() } }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getThisWeekEvents(): Flow<List<PersonalEvent>> =
        personalEventDao.getThisWeekEvents()
            .map { events -> events.map { it.toDomainModel() } }

    // ========== OPERACIONES ESPECÍFICAS POR TIPO ==========
    @RequiresApi(Build.VERSION_CODES.O)
    fun getPersonalEvents(): Flow<List<PersonalEvent>> =
        getEventsByType(PersonalEventType.PERSONAL)

    @RequiresApi(Build.VERSION_CODES.O)
    fun getSubscribedEvents(): Flow<List<PersonalEvent>> =
        getEventsByType(PersonalEventType.SUBSCRIBED)

    @RequiresApi(Build.VERSION_CODES.O)
    fun getHiddenEvents(): Flow<List<PersonalEvent>> =
        getEventsByType(PersonalEventType.HIDDEN)

    // ========== OPERACIONES DE ESCRITURA DE EVENTOS ==========
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertEvent(event: PersonalEvent): Long {
        val eventEntity = event.toEntity()
        val itemEntities = event.items.mapIndexed { index, item ->
            item.toEntity().copy(orderIndex = index)
        }
        val notificationEntity = event.notification?.toEntity()

        personalEventDao.insertEventWithDetails(eventEntity, itemEntities, notificationEntity)
        return eventEntity.id.toLong()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateEvent(event: PersonalEvent) {
        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val eventEntity = event.toEntity().copy(updatedAt = now)
        val itemEntities = event.items.mapIndexed { index, item ->
            item.toEntity().copy(eventId = event.id, orderIndex = index)
        }
        val notificationEntity = event.notification?.toEntity()?.copy(eventId = event.id)

        personalEventDao.updateEventWithDetails(eventEntity, itemEntities, notificationEntity)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteEvent(event: PersonalEvent) {
        val eventEntity = event.toEntity()
        personalEventDao.deleteEvent(eventEntity)
    }

    suspend fun deleteEventById(eventId: Int) {
        // Crear una entidad mínima para eliminar
        val eventEntity = PersonalEventEntity(
            id = eventId,
            title = "",
            colorIndex = 0,
            startDate = "",
            endDate = "",
            type = "PERSONAL",
            createdAt = ""
        )
        personalEventDao.deleteEvent(eventEntity)
    }

    // ========== OPERACIONES ESPECIALES DE EVENTOS ==========
    suspend fun hideEvent(eventId: Int) {
        personalEventDao.updateEventVisibility(eventId, false)
    }

    suspend fun showEvent(eventId: Int) {
        personalEventDao.updateEventVisibility(eventId, true)
    }

    suspend fun markEventAsCompleted(eventId: Int) {
        personalEventDao.updateEventCompletion(eventId, true)
    }

    suspend fun markEventAsIncomplete(eventId: Int) {
        personalEventDao.updateEventCompletion(eventId, false)
    }

    suspend fun hideInstitutionalEvent(institutionalEventId: Int) {
        personalEventDao.hideInstitutionalEvent(institutionalEventId)
    }

    suspend fun deleteInstitutionalEvent(institutionalEventId: Int) {
        personalEventDao.deleteInstitutionalEvent(institutionalEventId)
    }

    // ========== GESTIÓN DE PERFIL ESTUDIANTIL LOCAL ==========
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertStudentProfile(profile: LocalStudentProfile) {
        val profileEntity = profile.toEntity()
        personalEventDao.insertStudentProfile(profileEntity)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getStudentProfile(): Flow<LocalStudentProfile?> =
        personalEventDao.getStudentProfile()
            .map { it?.toLocalDomainModel() }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateStudentProfile(profile: LocalStudentProfile) {
        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val profileEntity = profile.toEntity().copy(updatedAt = now)
        personalEventDao.updateStudentProfile(profileEntity)
    }

    // ========== GESTIÓN DE SUSCRIPCIONES LOCALES ==========
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun subscribeToChannel(subscription: LocalStudentSubscription) {
        val subscriptionEntity = subscription.toEntity()
        personalEventDao.insertSubscription(subscriptionEntity)
    }

    fun getActiveSubscriptions(): Flow<List<LocalStudentSubscription>> =
        personalEventDao.getActiveSubscriptions()
            .map { subscriptions -> subscriptions.map { it.toLocalDomainModel() } }

    suspend fun unsubscribeFromChannel(channelId: Int) {
        personalEventDao.unsubscribeFromChannel(channelId)
        // También ocultar eventos institucionales de ese canal
        personalEventDao.hideInstitutionalEvent(channelId)
    }

    suspend fun updateChannelNotifications(channelId: Int, enabled: Boolean) {
        personalEventDao.updateChannelNotifications(channelId, enabled)
    }

    suspend fun deleteSubscription(subscription: LocalStudentSubscription) {
        val subscriptionEntity = subscription.toEntity()
        personalEventDao.deleteSubscription(subscriptionEntity)
    }

    // ========== BÚSQUEDA Y FILTROS ==========
    @RequiresApi(Build.VERSION_CODES.O)
    fun searchEvents(query: String): Flow<List<PersonalEvent>> =
        personalEventDao.searchEvents(query)
            .map { events -> events.map { it.toDomainModel() } }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getHighPriorityEvents(): Flow<List<PersonalEvent>> =
        personalEventDao.getEventsByPriorities(listOf("HIGH", "URGENT"))
            .map { events -> events.map { it.toDomainModel() } }

    fun getAllTags(): Flow<List<String>> =
        personalEventDao.getAllTags()
            .map { tagJsonList ->
                tagJsonList.flatMap { it.fromTagsJson() }.distinct()
            }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEventsByInstitutionalId(institutionalEventId: Int): Flow<List<PersonalEvent>> =
        personalEventDao.getEventsByInstitutionalId(institutionalEventId)
            .map { events -> events.map { it.toDomainModel() } }

    // ========== OPERACIONES DE CONVENIENCIA ==========
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentMonthEvents(): Flow<List<PersonalEvent>> {
        val now = LocalDate.now()
        return getEventsForMonth(now.year, now.monthValue)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEventsInDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<PersonalEvent>> =
        personalEventDao.getEventsInDateRange(startDate.toString(), endDate.toString())
            .map { events -> events.map { it.toDomainModel() } }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEventsOccurringOnDate(date: LocalDate): Flow<List<PersonalEvent>> =
        personalEventDao.getEventsOccurringOnDate(date.toString())
            .map { events -> events.map { it.toDomainModel() } }

    // ========== ESTADÍSTICAS ==========
    suspend fun getEventStatistics(): EventStatistics {
        val totalVisible = personalEventDao.getTotalVisibleEvents()
        val personalCount = personalEventDao.getEventCountByType(PersonalEventType.PERSONAL.name)
        val subscribedCount = personalEventDao.getEventCountByType(PersonalEventType.SUBSCRIBED.name)
        val hiddenCount = personalEventDao.getEventCountByType(PersonalEventType.HIDDEN.name)

        return EventStatistics(
            totalEvents = totalVisible,
            personalEvents = personalCount,
            subscribedEvents = subscribedCount,
            hiddenEvents = hiddenCount
        )
    }

    suspend fun getTodayEventCount(): Int =
        personalEventDao.getTodayEventCount()

    suspend fun getUpcomingEventCount(): Int =
        personalEventDao.getUpcomingEventCount()

    suspend fun getThisWeekEventCount(): Int =
        personalEventDao.getThisWeekEventCount()

    // ========== CREACIÓN DE EVENTOS DE EJEMPLO ==========
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createSampleEvent(
        title: String,
        description: String,
        startDate: LocalDate,
        endDate: LocalDate = startDate,
        colorIndex: Int = 0,
        type: PersonalEventType = PersonalEventType.PERSONAL,
        items: List<Pair<String, String>> = emptyList()
    ): Long {
        val event = createSamplePersonalEvent(title, description, startDate, endDate, colorIndex, type)

        val eventItems = items.mapIndexed { index, (iconName, text) ->
            PersonalEventItem(
                id = index + 1,
                personalEventId = 0,
                iconName = iconName,
                text = text,
                value = "",
                isClickable = false
            )
        }

        val finalEvent = event.copy(items = eventItems)
        return insertEvent(finalEvent)
    }

    // ========== PRECARGAR EVENTOS PARA DESARROLLO ==========
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun preloadSampleEvents() {
        try {
            val today = LocalDate.now()
            val sampleEvents = listOf(
                createSamplePersonalEvent(
                    title = "Reunion de Proyecto",
                    description = "Reunión semanal del equipo",
                    startDate = today,
                    colorIndex = 0,
                    type = PersonalEventType.PERSONAL
                ),
                createSamplePersonalEvent(
                    title = "Conferencia de IA",
                    description = "Conferencia sobre Inteligencia Artificial",
                    startDate = today.plusDays(1),
                    colorIndex = 1,
                    type = PersonalEventType.SUBSCRIBED
                ),
                createSamplePersonalEvent(
                    title = "Entrega de Proyecto",
                    description = "Fecha límite para entregar proyecto final",
                    startDate = today.plusDays(3),
                    colorIndex = 2,
                    type = PersonalEventType.PERSONAL
                ),
                createSamplePersonalEvent(
                    title = "Examen de Matemáticas",
                    description = "Examen parcial de matemáticas discretas",
                    startDate = today.plusDays(5),
                    colorIndex = 3,
                    type = PersonalEventType.PERSONAL
                ),
                createSamplePersonalEvent(
                    title = "Taller de Android",
                    description = "Taller de desarrollo móvil con Kotlin",
                    startDate = today.plusDays(7),
                    endDate = today.plusDays(9),
                    colorIndex = 4,
                    type = PersonalEventType.SUBSCRIBED
                )
            )

            sampleEvents.forEach { event ->
                insertEvent(event)
            }

            println("Precarga de ${sampleEvents.size} eventos completada exitosamente")
        } catch (e: Exception) {
            println("Error durante la precarga de eventos: ${e.message}")
        }
    }

    // ========== OPERACIONES DE LIMPIEZA ==========
    suspend fun cleanupCompletedEvents() {
        // Eliminar eventos completados antiguos (implementación futura)
        // Se podría agregar una consulta para eliminar eventos marcados como completados
        // y que tengan más de 30 días de antigüedad
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateLastSyncTime() {
        val profile = personalEventDao.getStudentProfile()
        // Actualizar tiempo de sincronización si existe perfil
        // (implementación futura cuando se integre con el API)
    }

    // ========== OPERACIONES BATCH ==========
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertMultipleEvents(events: List<PersonalEvent>) {
        events.forEach { event ->
            insertEvent(event)
        }
    }

    suspend fun deleteMultipleEvents(eventIds: List<Int>) {
        eventIds.forEach { eventId ->
            deleteEventById(eventId)
        }
    }

    suspend fun hideMultipleEvents(eventIds: List<Int>) {
        eventIds.forEach { eventId ->
            hideEvent(eventId)
        }
    }

    suspend fun showMultipleEvents(eventIds: List<Int>) {
        eventIds.forEach { eventId ->
            showEvent(eventId)
        }
    }

    // ========== OPERACIONES DE VALIDACIÓN ==========
    @RequiresApi(Build.VERSION_CODES.O)
    fun validateEvent(event: PersonalEvent): Boolean {
        return event.title.isNotBlank() &&
                !event.startDate.isAfter(event.endDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun hasConflictingEvents(
        startDate: LocalDate,
        endDate: LocalDate,
        excludeEventId: Int? = null
    ): Boolean {
        val events = getEventsInDateRange(startDate, endDate)
        // Implementar lógica para detectar conflictos
        // Por ahora retorna false (sin conflictos)
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun preloadEvents(sampleEvents: List<PersonalEvent>) {

        if (sampleEvents.isNotEmpty()) {
            sampleEvents.forEach { event ->
                try {
                    insertEvent(event as PersonalEvent)
                } catch (e: Exception) {
                    println("Error al insertar actividad: ${e.message}")
                }
            }
        } else {
            println("No se proporcionaron actividades válidas para precargar")
        }
    }
}