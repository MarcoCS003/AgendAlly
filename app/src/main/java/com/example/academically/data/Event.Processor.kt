package com.example.academically.data

import android.annotation.SuppressLint
import java.time.LocalDate
import java.time.YearMonth

class EventProcessor {
    companion object {
        /**
         * Procesa eventos para un mes utilizando el nuevo modelo de eventos
         */
        @SuppressLint("NewApi")
        fun processEvents(month: Int, year: Int, events: List<Event>): Map<Int, ProcessedEvent> {
            val processedEvents = mutableMapOf<Int, ProcessedEvent>()
            val yearMonth = YearMonth.of(year, month)

            // Mapa temporal para agrupar eventos por día
            val eventsByDay = mutableMapOf<Int, MutableList<Event>>()

            // Primero, agrupa todos los eventos por día
            for (day in 1..yearMonth.lengthOfMonth()) {
                val date = LocalDate.of(year, month, day)

                // Encontrar todos los eventos para este día
                val eventsForDay = events.filter { event ->
                    event.occursOn(date)
                }

                // Si hay eventos para este día, guardarlos en el mapa
                if (eventsForDay.isNotEmpty()) {
                    eventsByDay[day] = eventsForDay.toMutableList()
                }
            }

            // Luego, procesa cada día para crear ProcessedEvent con evento principal y adicionales
            for ((day, eventsForDay) in eventsByDay) {
                val date = LocalDate.of(year, month, day)

                if (eventsForDay.isNotEmpty()) {
                    // Tomar el primer evento como principal
                    val mainEvent = eventsForDay[0]
                    val shape = mainEvent.getShapeForDate(date)

                    // Los demás eventos se agregan como adicionales
                    val additionalEvents = if (eventsForDay.size > 1) {
                        eventsForDay.subList(1, eventsForDay.size)
                    } else {
                        emptyList()
                    }

                    // Crear el ProcessedEvent con el evento principal y los adicionales
                    processedEvents[day] = ProcessedEvent(
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
         * Convierte eventos del nuevo formato al formato compatible con la implementación actual
         */
        fun convertEventsForMonth(month: Int, events: List<Event>): List<Event> {
            val compatibleEvents = mutableListOf<Event>()

            events.forEach { event ->
                val compatibleEvent = event.getCompatibleEvent(month)
                if (compatibleEvent != null) {
                    compatibleEvents.add(compatibleEvent)
                }
            }

            return compatibleEvents
        }
    }
}

data class ProcessedEvent(
    val day: Int,
    val event: Event,               // Evento principal
    val shape: EventShape,
    val additionalEvents: List<Event> = emptyList() // Eventos adicionales
)





