package com.example.academically.data.model

import android.annotation.SuppressLint
import java.time.LocalDate
import java.time.YearMonth

class EventProcessor {
    companion object {
        /**
         * Procesa eventos personales para un mes
         */
        @SuppressLint("NewApi")
        fun processPersonalEvents(month: Int, year: Int, events: List<PersonalEvent>): Map<Int, ProcessedPersonalEvent> {
            val processedEvents = mutableMapOf<Int, ProcessedPersonalEvent>()
            val yearMonth = YearMonth.of(year, month)

            // Mapa temporal para agrupar eventos por día
            val eventsByDay = mutableMapOf<Int, MutableList<PersonalEvent>>()

            // Primero, agrupa todos los eventos por día
            for (day in 1..yearMonth.lengthOfMonth()) {
                val date = LocalDate.of(year, month, day)

                // Encontrar todos los eventos para este día
                val eventsForDay = events.filter { event ->
                    event.occursOn(date) && event.isVisible
                }

                // Si hay eventos para este día, guardarlos en el mapa
                if (eventsForDay.isNotEmpty()) {
                    eventsByDay[day] = eventsForDay.toMutableList()
                }
            }

            // Luego, procesa cada día para crear ProcessedPersonalEvent
            for ((day, eventsForDay) in eventsByDay) {
                val date = LocalDate.of(year, month, day)

                if (eventsForDay.isNotEmpty()) {
                    // Ordenar eventos por prioridad y hora
                    val sortedEvents = eventsForDay.sortedWith(
                        compareBy<PersonalEvent> { event ->
                            when (event.type) {
                                PersonalEventType.SUBSCRIBED -> 0  // Eventos institucionales primero
                                PersonalEventType.PERSONAL -> 1   // Eventos personales después
                                PersonalEventType.HIDDEN -> 2     // Eventos ocultos al final
                                PersonalEventType.ACADEMIC -> TODO()
                            }
                        }.thenBy { it.startDate }
                    )

                    // Tomar el primer evento como principal
                    val mainEvent = sortedEvents[0]
                    val shape = mainEvent.getShapeForDate(date)

                    // Los demás eventos se agregan como adicionales
                    val additionalEvents = if (sortedEvents.size > 1) {
                        sortedEvents.subList(1, sortedEvents.size)
                    } else {
                        emptyList()
                    }

                    // Crear el ProcessedPersonalEvent
                    processedEvents[day] = ProcessedPersonalEvent(
                        day = day,
                        event = mainEvent,
                        shape = shape,
                        additionalEvents = additionalEvents
                    )
                }
            }

            return processedEvents
        }

        /**
         * Filtra eventos por tipo
         */
        fun filterEventsByType(events: List<PersonalEvent>, type: PersonalEventType): List<PersonalEvent> {
            return events.filter { it.type == type && it.isVisible }
        }

        /**
         * Obtiene eventos para una fecha específica
         */
        @SuppressLint("NewApi")
        fun getEventsForDate(date: LocalDate, events: List<PersonalEvent>): List<PersonalEvent> {
            return events.filter { event ->
                event.occursOn(date) && event.isVisible
            }.sortedWith(
                compareBy<PersonalEvent> { event ->
                    when (event.type) {
                        PersonalEventType.SUBSCRIBED -> 0
                        PersonalEventType.PERSONAL -> 1
                        PersonalEventType.HIDDEN -> 2
                        PersonalEventType.ACADEMIC -> TODO()
                    }
                }.thenBy { it.startDate }
            )
        }

        /**
         * Obtiene eventos próximos (siguientes 7 días)
         */
        @SuppressLint("NewApi")
        fun getUpcomingEvents(events: List<PersonalEvent>): List<PersonalEvent> {
            val today = LocalDate.now()
            val nextWeek = today.plusDays(7)

            return events.filter { event ->
                event.isVisible &&
                        ((event.startDate.isEqual(today) || event.startDate.isAfter(today)) &&
                                (event.startDate.isBefore(nextWeek) || event.startDate.isEqual(nextWeek)))
            }.sortedBy { it.startDate }
        }

        /**
         * Cuenta eventos por tipo para estadísticas
         */
        fun getEventStatistics(events: List<PersonalEvent>): EventStatistics {
            val visibleEvents = events.filter { it.isVisible }

            return EventStatistics(
                totalEvents = visibleEvents.size,
                personalEvents = visibleEvents.count { it.type == PersonalEventType.PERSONAL },
                subscribedEvents = visibleEvents.count { it.type == PersonalEventType.SUBSCRIBED },
                hiddenEvents = events.count { !it.isVisible }
            )
        }
    }
}

data class ProcessedPersonalEvent(
    val day: Int,
    val event: PersonalEvent,               // Evento principal
    val shape: EventShape,
    val additionalEvents: List<PersonalEvent> = emptyList() // Eventos adicionales
)

data class EventStatistics(
    val totalEvents: Int,
    val personalEvents: Int,
    val subscribedEvents: Int,
    val hiddenEvents: Int
)