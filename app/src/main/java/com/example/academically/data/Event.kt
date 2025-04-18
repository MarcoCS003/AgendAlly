package com.example.academically.data

import android.annotation.SuppressLint
import android.graphics.drawable.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.example.academically.uiAcademicAlly.DaysOfWeek
import java.time.LocalDate
import java.time.YearMonth

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

data class Event(
    val id: Int,
    val color: Color,
    val description: String,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val mesID: Int? = null,
    val shape: EventShape = EventShape.RoundedFull
) {
    /**
     * Verifica si este evento ocurre en una fecha específica
     */
    @SuppressLint("NewApi")
    fun occursOn(date: LocalDate): Boolean {
        return when {
            // Evento con fechas en formato nuevo
            startDate != null && endDate != null ->
                (date.isEqual(startDate) || date.isAfter(startDate)) &&
                        (date.isEqual(endDate) || date.isBefore(endDate))
            // Compatibilidad con formato anterior (sólo mes)
            mesID != null ->
                date.monthValue == mesID
            // No hay coincidencia
            else -> false
        }
    }

    /**
     * Obtiene la forma adecuada para mostrar este evento en el calendario
     */
    @SuppressLint("NewApi")
    fun getShapeForDate(date: LocalDate): EventShape {
        // Para eventos de un solo día, usar la forma definida
        if (startDate != null && endDate != null && startDate.isEqual(endDate)) {
            return shape
        }

        // Para eventos de varios días
        return when {
            startDate != null && endDate != null -> {
                when {
                    date.isEqual(startDate) -> EventShape.RoundedStart
                    date.isEqual(endDate) -> EventShape.RoundedEnd
                    else -> EventShape.RoundedMiddle
                }
            }
            else -> shape
        }
    }

    /**
     * Crea una versión compatible hacia atrás de este evento para un mes específico
     */
    @SuppressLint("NewApi")
    fun getCompatibleEvent(month: Int): Event? {
        // Si el evento ya está en formato antiguo y es para este mes, devolverlo igual
        if (mesID != null && mesID == month) {
            return this
        }

        // Para eventos en formato nuevo, convertirlos a formato antiguo si aplican a este mes
        if (startDate == null || endDate == null) {
            return null
        }

        val startInMonth = startDate.monthValue == month
        val endInMonth = endDate.monthValue == month
        val isRelevantToMonth =
            startInMonth || endInMonth ||
                    isMonthBetween(month, startDate, endDate)

        // Si el evento es relevante para este mes, crear copia con el mesID establecido
        return if (isRelevantToMonth) {
            this.copy(mesID = month)
        } else {
            null
        }
    }

    /**
     * Genera la lista de días del mes para este evento
     */
    @SuppressLint("NewApi")
    fun getDaysInMonth(month: Int, year: Int): List<Int> {
        // Si el evento no tiene fechas, retornar lista vacía
        if (startDate == null || endDate == null) {
            return emptyList()
        }

        val startInMonth = startDate.monthValue == month && startDate.year == year
        val endInMonth = endDate.monthValue == month && endDate.year == year

        return when {
            // Evento de un solo día en este mes
            startInMonth && startDate == endDate -> {
                listOf(startDate.dayOfMonth)
            }
            // Evento que abarca varios días solo en este mes
            startInMonth && endInMonth -> {
                (startDate.dayOfMonth..endDate.dayOfMonth).toList()
            }
            // Evento que comienza en este mes pero termina en otro
            startInMonth && !endInMonth -> {
                val lastDay = YearMonth.of(year, month).lengthOfMonth()
                (startDate.dayOfMonth..lastDay).toList()
            }
            // Evento que termina en este mes pero comenzó en otro
            !startInMonth && endInMonth -> {
                (1..endDate.dayOfMonth).toList()
            }
            // Evento que pasa por todo el mes (comienza antes y termina después)
            !startInMonth && !endInMonth &&
                    isMonthBetween(month, startDate, endDate) -> {
                val lastDay = YearMonth.of(year, month).lengthOfMonth()
                (1..lastDay).toList()
            }
            // No aplica a este mes
            else -> emptyList()
        }
    }

    /**
     * Verifica si un mes está entre las fechas de inicio y fin del evento
     */
    @SuppressLint("NewApi")
    private fun isMonthBetween(month: Int, start: LocalDate, end: LocalDate): Boolean {
        val year = start.year
        val targetYearMonth = YearMonth.of(year, month)
        val startYearMonth = YearMonth.of(start.year, start.month)
        val endYearMonth = YearMonth.of(end.year, end.month)

        return (targetYearMonth.isAfter(startYearMonth) || targetYearMonth.equals(startYearMonth)) &&
                (targetYearMonth.isBefore(endYearMonth) || targetYearMonth.equals(endYearMonth))
    }

    companion object {
        /**
         * Crea un evento que ocurre en un solo día
         */
        fun singleDay(
            id: Int,
            date: LocalDate,
            description: String,
            color: Color,
            shape: EventShape = EventShape.RoundedFull
        ): Event {
            return Event(
                id = id,
                startDate = date,
                endDate = date,
                description = description,
                color = color,
                shape = shape
            )
        }

        /**
         * Crea un evento que abarca varios días
         */
        fun multiDay(
            id: Int,
            startDate: LocalDate,
            endDate: LocalDate,
            description: String,
            color: Color
        ): Event {
            return Event(
                id = id,
                startDate = startDate,
                endDate = endDate,
                description = description,
                color = color
            )
        }
    }
}

class Items (val icon:Icon, val string: String)