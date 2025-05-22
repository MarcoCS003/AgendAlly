package com.example.academically.data

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.academically.ui.theme.DarkThemeScheduleColors
import com.example.academically.ui.theme.LightThemeScheduleColors
import java.time.LocalDate
import java.time.YearMonth

data class EventCategory(
    val id: Int,
    val name: String,
    val color: Color
) {
    companion object {
        val INSTITUTIONAL = EventCategory(1, "Institucional", Color(0xFF2196F3)) // Azul
        val CAREER = EventCategory(2, "Carrera", Color(0xFF4CAF50)) // Verde
        val PERSONAL = EventCategory(3, "Personal", Color(0xFFFF9800)) // Naranja
    }
}

data class EventItem(
    val id: Int,
    val icon: ImageVector,
    val text: String
)

data class EventNotification(
    val id: Int,
    val time: Long, // Tiempo en milisegundos antes del evento
    val title: String,
    val message: String,
    val isEnabled: Boolean = true
)

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
    val title: String,
    val shortDescription: String = "",
    val longDescription: String = "",
    val location: String = "",
    val colorIndex: Int, // Cambiado de color: Color
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val category: EventCategory = EventCategory.PERSONAL,
    val imagePath: String = "",
    val items: List<EventItem> = emptyList(),
    val notification: EventNotification? = null,
    val mesID: Int? = null,
    val shape: EventShape = EventShape.RoundedFull
) {
    // Resto del código...

    // Puedes añadir una propiedad computada para retrocompatibilidad
    val color: Color
        @Composable
        get() {
            val colors = if (isSystemInDarkTheme()) DarkThemeScheduleColors else LightThemeScheduleColors
            return colors[colorIndex % colors.size]
        }


    @SuppressLint("NewApi")
    fun occursOn(date: LocalDate): Boolean {
        return when {

            startDate != null && endDate != null ->
                (date.isEqual(startDate) || date.isAfter(startDate)) &&
                        (date.isEqual(endDate) || date.isBefore(endDate))
            mesID != null ->
                date.monthValue == mesID
            else -> false
        }
    }


    @SuppressLint("NewApi")
    fun getShapeForDate(date: LocalDate): EventShape {

        if (startDate != null && endDate != null && startDate.isEqual(endDate)) {
            return shape
        }


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


    @SuppressLint("NewApi")
    fun getDaysInMonth(month: Int, year: Int): List<Int> {
        if (startDate == null || endDate == null) {
            return emptyList()
        }

        val startInMonth = startDate.monthValue == month && startDate.year == year
        val endInMonth = endDate.monthValue == month && endDate.year == year

        return when {

            startInMonth && startDate == endDate -> {
                listOf(startDate.dayOfMonth)
            }

            startInMonth && endInMonth -> {
                (startDate.dayOfMonth..endDate.dayOfMonth).toList()
            }
            startInMonth && !endInMonth -> {
                val lastDay = YearMonth.of(year, month).lengthOfMonth()
                (startDate.dayOfMonth..lastDay).toList()
            }
            !startInMonth && endInMonth -> {
                (1..endDate.dayOfMonth).toList()
            }

            !startInMonth && !endInMonth &&
                    isMonthBetween(month, startDate, endDate) -> {
                val lastDay = YearMonth.of(year, month).lengthOfMonth()
                (1..lastDay).toList()
            }
            else -> emptyList()
        }
    }


    @SuppressLint("NewApi")
    private fun isMonthBetween(month: Int, start: LocalDate, end: LocalDate): Boolean {
        val year = start.year
        val targetYearMonth = YearMonth.of(year, month)
        val startYearMonth = YearMonth.of(start.year, start.month)
        val endYearMonth = YearMonth.of(end.year, end.month)

        return (targetYearMonth.isAfter(startYearMonth) || targetYearMonth.equals(startYearMonth)) &&
                (targetYearMonth.isBefore(endYearMonth) || targetYearMonth.equals(endYearMonth))
    }


}

