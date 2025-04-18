package com.example.academically.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.academically.uiAcademicAlly.DaysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

import java.time.format.TextStyle
import java.util.Locale



data class MountAcademicAlly(
    val id: Int,
    val dayStart: DaysOfWeek,
    val name: String,
    val weeks: List<List<Int>>
)

@SuppressLint("NewApi")
class SystemCalendarProvider(private val context: Context) {

    /**
     * Obtiene el año actual
     */
    fun getCurrentYear(): Int {
        return LocalDate.now().year
    }

    /**
     * Obtiene el mes actual (1-12)
     */

    fun getCurrentMonth(): Int {
        return LocalDate.now().monthValue
    }

    /**
     * Obtiene el índice del mes actual (0-11) para uso en listas
     */
    fun getCurrentMonthIndex(): Int {
        return LocalDate.now().monthValue - 1
    }

    /**
     * Genera los datos de todos los meses del año
     */
    @SuppressLint("NewApi")
    fun getMonthsData(year: Int = getCurrentYear()): List<MountAcademicAlly> {
        val months = mutableListOf<MountAcademicAlly>()

        for (monthNum in 1..12) {
            val yearMonth = YearMonth.of(year, monthNum)
            val firstDayOfMonth = yearMonth.atDay(1)
            val firstDayOfWeek = mapToDaysOfWeek(firstDayOfMonth.dayOfWeek)

            val monthName = Month.of(monthNum).getDisplayName(TextStyle.FULL, Locale("es", "ES"))
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

            months.add(
                MountAcademicAlly(
                    id = monthNum,
                    dayStart = firstDayOfWeek,
                    name = monthName,
                    weeks = generateWeeksForMonth(yearMonth)
                )
            )
        }

        return months
    }

    /**
     * Convierte DayOfWeek de Java a nuestro enum DaysOfWeek
     */
    private fun mapToDaysOfWeek(dayOfWeek: DayOfWeek): DaysOfWeek {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> DaysOfWeek.LUNES
            DayOfWeek.TUESDAY -> DaysOfWeek.MARTES
            DayOfWeek.WEDNESDAY -> DaysOfWeek.MIERCOLES
            DayOfWeek.THURSDAY -> DaysOfWeek.JUEVES
            DayOfWeek.FRIDAY -> DaysOfWeek.VIERNES
            DayOfWeek.SATURDAY -> DaysOfWeek.SABADO
            DayOfWeek.SUNDAY -> DaysOfWeek.DOMINGO
        }
    }

    /**
     * Genera la estructura de semanas para un mes
     */
    private fun generateWeeksForMonth(yearMonth: YearMonth): List<List<Int>> {
        val firstDayOfMonth = yearMonth.atDay(1)
        val lastDayOfMonth = yearMonth.atEndOfMonth()

        // Ajuste para que la semana empiece en domingo (0)
        // El valor numérico de dayOfWeek en Java time es: 1 = lunes, ..., 7 = domingo
        // Convertimos para que sea: 0 = domingo, 1 = lunes, ..., 6 = sábado
        val firstDayOffset = (firstDayOfMonth.dayOfWeek.value % 7)

        val weeks = mutableListOf<List<Int>>()
        var currentWeek = MutableList(7) { 0 }
        var day = 1

        // Llenar los días antes del inicio del mes con 0
        for (i in 0 until firstDayOffset) {
            currentWeek[i] = 0
        }

        // Llenar los días del mes
        for (i in firstDayOffset until 7) {
            if (day <= lastDayOfMonth.dayOfMonth) {
                currentWeek[i] = day++
            } else {
                break
            }
        }

        weeks.add(currentWeek.toList())

        // Procesar las semanas restantes
        while (day <= lastDayOfMonth.dayOfMonth) {
            currentWeek = MutableList(7) { 0 }
            for (i in 0 until 7) {
                if (day <= lastDayOfMonth.dayOfMonth) {
                    currentWeek[i] = day++
                } else {
                    // Rellenar con 0 el resto de la semana
                    currentWeek[i] = 0
                }
            }
            weeks.add(currentWeek.toList())
        }

        return weeks
    }
}

class EventConverter {
    companion object {
        /**
         * Convierte eventos del formato antiguo al nuevo formato
         */
        @SuppressLint("NewApi")
        fun convertToNewFormat(events: List<Event>, year: Int = LocalDate.now().year): List<Event> {
            val newEvents = mutableListOf<Event>()

            events.forEach { event ->
                // Si ya está en el nuevo formato, no convertir
                if (event.startDate != null && event.endDate != null) {
                    newEvents.add(event)
                    return@forEach
                }

                // Si tiene mesID pero no tiene startDate/endDate, crear fechas
                if (event.mesID != null) {
                    val month = event.mesID
                    val days = event.getDaysInMonth(month, year)

                    if (days.isNotEmpty()) {
                        newEvents.add(Event(
                            id = event.id,
                            startDate = LocalDate.of(year, month, days.first()),
                            endDate = LocalDate.of(year, month, days.last()),
                            description = event.description,
                            color = event.color,
                            shape = event.shape
                        ))
                    } else {
                        // Si no hay días específicos, hacemos que el evento dure todo el mes
                        val lastDayOfMonth = YearMonth.of(year, month).lengthOfMonth()
                        newEvents.add(Event(
                            id = event.id,
                            startDate = LocalDate.of(year, month, 1),
                            endDate = LocalDate.of(year, month, lastDayOfMonth),
                            description = event.description,
                            color = event.color,
                            shape = event.shape
                        ))
                    }
                }
            }

            return newEvents
        }
    }
}