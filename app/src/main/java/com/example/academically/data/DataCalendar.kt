package com.example.academically.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.example.academically.uiAcademicAlly.DaysOfWeek

// Clase para definir formas personalizadas para diferentes tipos de eventos
sealed class EventShape {
    object Circle : EventShape()
    object RoundedStart : EventShape()
    object RoundedMiddle : EventShape()
    object RoundedEnd : EventShape()
    object RoundedFull : EventShape()

    fun toShape(): Shape {
        return when (this) {
            is Circle -> CircleShape
            is RoundedStart -> RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp)
            is RoundedMiddle -> RoundedCornerShape(0.dp)
            is RoundedEnd -> RoundedCornerShape(topEnd = 14.dp, bottomEnd = 14.dp)
            is RoundedFull -> RoundedCornerShape(14.dp)
        }
    }
}

// Modelo de datos mejorado para representar eventos
data class Event(
    val id: Int,
    val color: Color,
    val dayAlone: Int? = null,
    val days: List<Int>? = null,
    val mesID: Int,
    val description: String,
    val shape: EventShape = EventShape.RoundedFull  // Forma predeterminada
)

data class Mount(val id: Int, val dayStart: DaysOfWeek, val name: String, val weeks: List<List<Int>>)

// Clase auxiliar para eventos procesados para mejorar rendimiento
data class ProcessedEvent(
    val day: Int,
    val event: Event,
    val shape: EventShape
)

// Clase de utilidad para preprocesar eventos por mes
class EventProcessor {
    companion object {
        fun processEventsForMonth(month: Mount, events: List<Event>, eventsAlone: List<Event>): Map<Int, ProcessedEvent> {
            val processedEvents = mutableMapOf<Int, ProcessedEvent>()

            // Procesar eventos que duran varios días
            events.filter { it.mesID == month.id }.forEach { event ->
                event.days?.forEach { day ->
                    val shape = when {
                        day == event.days.first() -> EventShape.RoundedStart
                        day == event.days.last() -> EventShape.RoundedEnd
                        else -> EventShape.RoundedMiddle
                    }
                    processedEvents[day] = ProcessedEvent(day, event, shape)
                }
            }

            // Procesar eventos de un solo día
            eventsAlone.filter { it.mesID == month.id }.forEach { event ->
                event.dayAlone?.let { day ->
                    processedEvents[day] = ProcessedEvent(day, event, EventShape.RoundedFull)
                }
            }

            return processedEvents
        }
    }
}

// Clase de eventos con datos de ejemplo
object Eventos {
    val listEvents = listOf(
        Event(1, Color.Blue, days = (8..12).toList(), mesID = 1, description = "Fecha de pago de reinscripcion"),
        Event(2, Color.Green, days = (15..19).toList(), mesID = 1, description = "Carga de materias"),
        Event(3, Color.Green, days = (22..26).toList(), mesID = 1, description = "Carga de materias"),
        Event(4, Color.Yellow, days = (25..29).toList(), mesID = 3, description = "Periodo vacacional"),
        Event(5, Color.Yellow, days = (1..5).toList(), mesID = 4, description = "Periodo vacacional"),
        Event(6, Color.Yellow, days = (1..5).toList(), mesID = 7, description = "Periodo vacacional"),
        Event(7, Color.Yellow, days = (8..12).toList(), mesID = 7, description = "Periodo vacacional"),
        Event(8, Color.Yellow, days = (15..19).toList(), mesID = 7, description = "Periodo vacacional"),
        Event(9, Color.Yellow, days = (22..26).toList(), mesID = 7, description = "Periodo vacacional")
    )

    val listEventsAlone = listOf(
        Event(10, Color.Black, 1, mesID = 5, description = "Suspencion de clases"),
        Event(11, Color.Black, 15, mesID = 5, description = "Suspencion de clases"),
        Event(12, Color.Black, 18, mesID = 3, description = "Suspencion de clases"),
        Event(13, Color.Cyan, 3, mesID = 1, description = "Inicio de labores administrativas"),
        Event(14, Color.Red, 29, mesID = 1, description = "Inicio de clases"),
        Event(15, Color.Black, 5, mesID = 2, description = "Suspencion de clases"),
        Event(16, Color.Red, 31, mesID = 5, description = "Fin de clases"),
        Event(17, Color.Magenta, 28, mesID = 2, description = "Mi cumpleaños"),
        Event(18, Color.Magenta, 14, mesID = 6, description = "Cumpleaños de papá"),
        Event(19, Color.DarkGray, 28, mesID = 6, description = "fin de labores administrativas"),
        Event(20, Color.Cyan, 29, mesID = 7, description = "Inicio de labores administrativas")
    )
}

object Calanderio {
    val mounts = listOf(
        Mount(1, DaysOfWeek.LUNES, "Enero", listOf(
            listOf(0, 1, 2, 3, 4, 5, 6),
            listOf(7, 8, 9, 10, 11, 12, 13),
            listOf(14, 15, 16, 17, 18, 19, 20),
            listOf(21, 22, 23, 24, 25, 26, 27),
            listOf(28, 29, 30, 31, 0, 0, 0)
        )),
        Mount(2, DaysOfWeek.JUEVES, "Febrero",
            listOf(
                listOf(0, 0, 0, 0, 1, 2, 3),
                (4..10).toList(),
                (11..17).toList(),
                (18..24).toList(),
                listOf(25, 26, 27, 28, 29, 0, 0)
            )
        ),
        // Los demás meses se omiten por brevedad...
    )
}