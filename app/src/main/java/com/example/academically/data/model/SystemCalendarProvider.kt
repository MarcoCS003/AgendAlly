package com.example.academically.data.model

import android.annotation.SuppressLint
import android.content.Context
import com.example.academically.uiAcademicAlly.calendar.DaysOfWeek
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
                    currentWeek[i] = 0
                }
            }
            weeks.add(currentWeek.toList())
        }

        return weeks
    }

    /**
     * Obtiene información específica de un mes
     */
    fun getMonthData(year: Int, month: Int): MountAcademicAlly? {
        return try {
            val monthsData = getMonthsData(year)
            monthsData.find { it.id == month }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Verifica si una fecha está en el mes actual
     */
    fun isCurrentMonth(year: Int, month: Int): Boolean {
        val now = LocalDate.now()
        return now.year == year && now.monthValue == month
    }

    /**
     * Verifica si un día es el día actual
     */
    fun isToday(year: Int, month: Int, day: Int): Boolean {
        val now = LocalDate.now()
        return now.year == year && now.monthValue == month && now.dayOfMonth == day
    }

    /**
     * Obtiene el nombre del mes en español
     */
    fun getMonthName(month: Int): String {
        return Month.of(month).getDisplayName(TextStyle.FULL, Locale("es", "ES"))
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    /**
     * Obtiene el nombre del día de la semana en español
     */
    fun getDayName(dayOfWeek: DayOfWeek): String {
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}

// ========== UTILIDADES PARA EVENTOS PERSONALES ==========
class PersonalEventCalendarProcessor {
    companion object {
        /**
         * Procesa eventos personales para mostrar en el calendario
         */
        @SuppressLint("NewApi")
        fun processEventsForCalendar(
            month: Int,
            year: Int,
            events: List<PersonalEvent>
        ): Map<Int, ProcessedPersonalEvent> {
            return EventProcessor.processPersonalEvents(month, year, events)
        }

        /**
         * Obtiene eventos para una fecha específica
         */
        @SuppressLint("NewApi")
        fun getEventsForDate(
            date: LocalDate,
            events: List<PersonalEvent>
        ): List<PersonalEvent> {
            return EventProcessor.getEventsForDate(date, events)
        }

        /**
         * Filtra eventos por tipo para el calendario
         */
        fun filterEventsByTypeForCalendar(
            events: List<PersonalEvent>,
            showPersonal: Boolean = true,
            showSubscribed: Boolean = true,
            showHidden: Boolean = false
        ): List<PersonalEvent> {
            return events.filter { event ->
                when (event.type) {
                    PersonalEventType.PERSONAL -> showPersonal
                    PersonalEventType.SUBSCRIBED -> showSubscribed
                    PersonalEventType.HIDDEN -> showHidden
                    PersonalEventType.ACADEMIC -> TODO()
                }
            }
        }

        /**
         * Cuenta eventos por día en un mes
         */
        @SuppressLint("NewApi")
        fun getEventCountPerDay(
            month: Int,
            year: Int,
            events: List<PersonalEvent>
        ): Map<Int, Int> {
            val yearMonth = YearMonth.of(year, month)
            val eventCounts = mutableMapOf<Int, Int>()

            for (day in 1..yearMonth.lengthOfMonth()) {
                val date = LocalDate.of(year, month, day)
                val eventsForDay = events.count { event ->
                    event.occursOn(date) && event.isVisible
                }
                if (eventsForDay > 0) {
                    eventCounts[day] = eventsForDay
                }
            }

            return eventCounts
        }

        /**
         * Obtiene el color predominante de eventos en un día
         */
        @SuppressLint("NewApi")
        fun getDominantColorForDay(
            date: LocalDate,
            events: List<PersonalEvent>
        ): Int? {
            val eventsForDay = events.filter { event ->
                event.occursOn(date) && event.isVisible
            }

            return if (eventsForDay.isNotEmpty()) {
                // Priorizar eventos institucionales, luego por orden de creación
                val prioritizedEvent = eventsForDay.sortedWith(
                    compareBy<PersonalEvent> { event ->
                        when (event.type) {
                            PersonalEventType.SUBSCRIBED -> 0
                            PersonalEventType.PERSONAL -> 1
                            PersonalEventType.HIDDEN -> 2
                            PersonalEventType.ACADEMIC -> TODO()
                        }
                    }.thenBy { it.startDate }
                ).first()

                prioritizedEvent.colorIndex
            } else {
                null
            }
        }
    }
}