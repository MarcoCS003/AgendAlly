package com.example.academically

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import com.example.academically.data.Event
import com.example.academically.data.EventCategory
import com.example.academically.data.EventItem
import com.example.academically.data.EventNotification
import com.example.academically.data.EventShape
import com.example.academically.data.Schedule
import com.example.academically.data.ScheduleTime
import com.example.academically.data.database.AcademicAllyDatabase
import com.example.academically.data.repositorty.ScheduleRepository
import com.example.academically.data.repository.EventRepository
import com.example.academically.uiAcademicAlly.calendar.DaysOfWeek
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale.Category

class AcademicAllyApplication : Application() {
    private val database by lazy { AcademicAllyDatabase.getDatabase(this) }
    private val eventRepository by lazy { EventRepository(database.eventDao()) }
    private val scheduleRepository by lazy { ScheduleRepository(database.scheduleDao()) }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        // Ejecutar en un ámbito de corrutina
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Verificar si la base de datos está vacía
                if (eventRepository.allEvents.first().isEmpty()) {
                    // Si está vacía, precargar datos
                    val sampleEvents = getSampleEvents()
                    eventRepository.preloadEvents(sampleEvents)
                    println("Se han cargado ${sampleEvents.size} eventos de muestra")
                } else {
                    println("La base de datos ya contiene eventos, no se realiza precarga")
                }
                if(scheduleRepository.allSchedulesWithTimes.first().isEmpty()){
                    val sampleSchedules = preloadActivities()
                    scheduleRepository.preloadSchedules(sampleSchedules)
                    println("Se han cargado ${sampleSchedules.size} actividades de muestra")
                } else{
                    println("La base de datos ya contiene actividades, no se realiza precarga")
                }



            } catch (e: Exception) {
                println("Error al verificar/cargar eventos: ${e.message}")

            }

        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun preloadActivities(): List<Schedule> {
        return listOf(
            Schedule(
                id = 1,
                colorIndex = 1, // Amarillo
                name = "ADMINISTRACIÓN GERENCIAL",
                place = "Aula: 07A",
                teacher = "MARIA JUANA CONTRERAS GUZMAN",
                times = listOf(
                    ScheduleTime(DaysOfWeek.LUNES, LocalTime.of(9, 0), LocalTime.of(11, 0)),
                    ScheduleTime(DaysOfWeek.MIERCOLES, LocalTime.of(9, 0), LocalTime.of(11, 0)),
                )
            ),
            Schedule(
                id = 2,
                colorIndex = 2, // Morado claro
                name = "ADMINISTRACIÓN DE PROYECTOS",
                place = "Aula: 07D",
                teacher = "ANA MARIA SOSA PINTLE",
                times = listOf(
                    ScheduleTime(DaysOfWeek.MARTES, LocalTime.of(9, 0), LocalTime.of(11, 0)),
                    ScheduleTime(DaysOfWeek.JUEVES, LocalTime.of(9, 0), LocalTime.of(11, 0)),
                    ScheduleTime(DaysOfWeek.VIERNES, LocalTime.of(10, 0), LocalTime.of(11, 0))
                )
            ),
            Schedule(
                id = 3,
                colorIndex =3, // Cian claro
                name = "SISTEMAS WEB Y SERVICIOS ORACLE",
                place = "Aula: 36L8",
                teacher = "BEATRIZ PEREZ ROJAS",
                times = listOf(
                    ScheduleTime(DaysOfWeek.MARTES, LocalTime.of(11, 0), LocalTime.of(13, 0)),
                    ScheduleTime(DaysOfWeek.JUEVES, LocalTime.of(11, 0), LocalTime.of(13, 0)),
                    ScheduleTime(DaysOfWeek.VIERNES, LocalTime.of(12, 0), LocalTime.of(13, 0))
                )
            ),
            Schedule(
                id = 4,
                colorIndex = 4, // Salmón
                name = "CUBOS OLAP PARA INTELIGENCIA EMPRESARIAL",
                place = "Aula: 36L3",
                teacher = "JOSÉ OMAR RAMÍREZ MARTHA",
                times = listOf(
                    ScheduleTime(DaysOfWeek.LUNES, LocalTime.of(13, 0), LocalTime.of(15, 0)),
                    ScheduleTime(DaysOfWeek.MIERCOLES, LocalTime.of(13, 0), LocalTime.of(15, 0)),
                    ScheduleTime(DaysOfWeek.VIERNES, LocalTime.of(13, 0), LocalTime.of(14, 0))
                )
            ),
            Schedule(
                id = 5,
                colorIndex = 5, // Verde claro
                name = "CIBERSEGURIDAD",
                place = "Aula: 36L8",
                teacher = "ANDRÉS MUÑOZ FLORES",
                times = listOf(
                    ScheduleTime(DaysOfWeek.MARTES, LocalTime.of(15, 0), LocalTime.of(17, 0)),
                    ScheduleTime(DaysOfWeek.JUEVES, LocalTime.of(15, 0), LocalTime.of(17, 0)),
                    ScheduleTime(DaysOfWeek.VIERNES, LocalTime.of(16, 0), LocalTime.of(17, 0))
                )
            )
        )
    }
    // Método para obtener eventos de ejemplo
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getSampleEvents(): List<Event> {
        return listOf(
            Event(
                id = 0, // El ID se generará automáticamente
                title = "Días inhábiles",
                colorIndex = 13, // Índice del color
                startDate = LocalDate.of(2025, 1, 1),
                endDate = LocalDate.of(2025, 1, 1),
                category = EventCategory.INSTITUTIONAL
            ), Event(
                id = 2,
                colorIndex = 13,
                title = "Días inhábiles",
                startDate = LocalDate.of(2025, 2, 3),
                endDate = LocalDate.of(2025, 2, 3),
                category = EventCategory.INSTITUTIONAL

            ),
            Event(
                id = 3,
                colorIndex = 10,
                title = "Cumpleaños",
                startDate = LocalDate.of(2025, 2, 28),
                endDate = LocalDate.of(2025, 2, 28),
                category = EventCategory.PERSONAL
            ),
            Event(
                id = 4,
                colorIndex = 6,
                title = "Periodo vacacional ",
                startDate = LocalDate.of(2025, 7, 7),
                endDate = LocalDate.of(2025, 7, 31),
                category = EventCategory.INSTITUTIONAL
            ),

             Event(
                id = 5,
                colorIndex = 5,
                title = "Actividades intersemestrales",
                startDate = LocalDate.of(2025, 6, 9),
                endDate = LocalDate.of(2025, 7, 4),
                category = EventCategory.INSTITUTIONAL
            ),
            Event(
                id = 6,
                colorIndex = 5,
                title = "Actividades intersemestrales",
                startDate = LocalDate.of(2025, 1, 8),
                endDate = LocalDate.of(2025, 1, 17),
                category = EventCategory.INSTITUTIONAL
            ),

            Event(
                id = 7,
                colorIndex = 14,
                title = "Inicio de clases",
                startDate = LocalDate.of(2025, 1, 26),
                endDate = LocalDate.of(2025, 1, 26),
                category = EventCategory.INSTITUTIONAL
            ),

            Event(
                id = 8,
                colorIndex = 13,
                title = "Días inhábiles",
                startDate = LocalDate.of(2025, 3, 17),
                endDate = LocalDate.of(2025, 3, 17),
                category = EventCategory.INSTITUTIONAL

            ),
            Event(
                id = 9,
                colorIndex = 6,
                title = "Periodo vacacional",
                startDate = LocalDate.of(2025, 1, 1),
                endDate = LocalDate.of(2025, 1, 7),
                category = EventCategory.INSTITUTIONAL
            ),
            Event(
                id = 10,
                colorIndex = 6,
                title = "Periodo vacacional",
                startDate = LocalDate.of(2025, 4, 14),
                endDate = LocalDate.of(2025, 4, 25),
                category = EventCategory.INSTITUTIONAL
            ),
            Event(
                id = 11,
                colorIndex = 5,
                title = "Inicio de clases",
                startDate = LocalDate.of(2025, 1, 26),
                endDate = LocalDate.of(2025, 1, 26),
                category = EventCategory.INSTITUTIONAL,
                shape = EventShape.Circle
            ),
            // Evento que abarca varios meses
            Event(
                id = 12,
                colorIndex = 13,
                title = "Días inhábiles",
                startDate = LocalDate.of(2025, 5, 1),
                endDate = LocalDate.of(2025, 5, 1),
                category = EventCategory.INSTITUTIONAL
            ), Event(
                id = 13,
                colorIndex =13,
                title = "Días inhábiles",
                startDate = LocalDate.of(2025, 5, 5),
                endDate = LocalDate.of(2025, 5, 5),
                category = EventCategory.INSTITUTIONAL
            ),
            Event(
                id = 14,
                colorIndex = 13,
                title = "Días inhábiles",
                startDate = LocalDate.of(2025, 5, 15),
                endDate = LocalDate.of(2025, 5, 15),
                category = EventCategory.INSTITUTIONAL
            ),
            Event(
                id = 15,
                colorIndex = 14,
                title = "Fin de clases",
                startDate = LocalDate.of(2025, 5, 30),
                endDate = LocalDate.of(2025, 5, 30),
                category = EventCategory.INSTITUTIONAL
            ),
            Event(
                id = 16,
                colorIndex = 4,
                title = "Incripciones",
                startDate = LocalDate.of(2025, 1, 20),
                endDate = LocalDate.of(2025, 1, 21),
                category = EventCategory.INSTITUTIONAL
            ),
            Event(
                id = 17,
                colorIndex = 11,
                title = "Reinscripciones",
                startDate = LocalDate.of(2025, 1, 22),
                endDate = LocalDate.of(2025, 1, 24),
                category = EventCategory.INSTITUTIONAL
            ),
            Event(
                id = 18,
                colorIndex = 7,
                title = "Entrega de calificaciones",
                startDate = LocalDate.of(2025, 6, 5),
                endDate = LocalDate.of(2025, 6, 6),
                category = EventCategory.INSTITUTIONAL
            ),

            Event(
                id = 19,
                title = "Convocatoria Servicio Social",
                shortDescription = "Registro para servicio",
                longDescription = "Estimado estudiante de TICs si le interesa realizar su servicio social durante el periodo Diciembre 2024 - Junio 2025 guardar esta información Coordinación Instruccional de tutorías Desarrollo Académico.",
                location = "Edificio 6",
                imagePath = R.drawable.seminario.toString(),
                startDate = LocalDate.of(2025, 11, 28),
                endDate = LocalDate.of(2025, 11, 29),
                category = EventCategory.CAREER,
                colorIndex = 12,
                notification = EventNotification(
                    id = 1,
                    time = 86400000, // 1 día
                    title = "Recordatorio",
                    message = "Convocatoria Servicio Social mañana",
                    isEnabled = true
                )
            )
            // ... más eventos
        )
    }
}