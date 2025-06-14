package com.example.academically

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.academically.data.PersonalEvent
import com.example.academically.data.PersonalEventItem
import com.example.academically.data.PersonalEventNotification
import com.example.academically.data.PersonalEventType
import com.example.academically.data.Schedule
import com.example.academically.data.ScheduleTime
import com.example.academically.data.database.AcademicAllyDatabase
import com.example.academically.data.repositorty.ScheduleRepository
import com.example.academically.data.repository.PersonalEventRepository
import com.example.academically.uiAcademicAlly.calendar.DaysOfWeek
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AcademicAllyApplication : Application() {
    private val database by lazy { AcademicAllyDatabase.getDatabase(this) }
    private val eventRepository by lazy { PersonalEventRepository(database.personalEventDao()) }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getSampleEvents(): List<PersonalEvent> {
        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        return listOf(
            PersonalEvent(
                id = 0, // El ID se generará automáticamente
                title = "Días inhábiles",
                shortDescription = "Día no laborable",
                longDescription = "Día inhábil según el calendario académico",
                location = "",
                colorIndex = 13,
                startDate = LocalDate.of(2025, 1, 1),
                endDate = LocalDate.of(2025, 1, 1),
                type = PersonalEventType.SUBSCRIBED,
                institutionalEventId = 1,
                imagePath = "",
                items = emptyList(),
                notification = null,
                isVisible = true,
                createdAt = now,
                updatedAt = null
            ),
            PersonalEvent(
                id = 0,
                title = "Días inhábiles",
                shortDescription = "Día no laborable",
                longDescription = "Día inhábil según el calendario académico",
                location = "",
                colorIndex = 13,
                startDate = LocalDate.of(2025, 2, 3),
                endDate = LocalDate.of(2025, 2, 3),
                type = PersonalEventType.SUBSCRIBED,
                institutionalEventId = 2,
                imagePath = "",
                items = emptyList(),
                notification = null,
                isVisible = true,
                createdAt = now,
                updatedAt = null
            ),
            PersonalEvent(
                id = 0,
                title = "Cumpleaños",
                shortDescription = "Mi cumpleaños",
                longDescription = "Celebración de cumpleaños personal",
                location = "Casa",
                colorIndex = 10,
                startDate = LocalDate.of(2025, 2, 28),
                endDate = LocalDate.of(2025, 2, 28),
                type = PersonalEventType.PERSONAL,
                institutionalEventId = null,
                imagePath = "",
                items = listOf(
                    PersonalEventItem(
                        id = 1,
                        personalEventId = 0,
                        iconName = "celebration",
                        text = "Fiesta",
                        value = "18:00 hrs",
                        isClickable = false
                    )
                ),
                notification = PersonalEventNotification(
                    id = 1,
                    personalEventId = 0,
                    time = 86400000, // 1 día antes
                    title = "Recordatorio",
                    message = "Mañana es tu cumpleaños!",
                    isEnabled = true
                ),
                isVisible = true,
                createdAt = now,
                updatedAt = null
            ),
            PersonalEvent(
                id = 0,
                title = "Periodo vacacional",
                shortDescription = "Vacaciones de verano",
                longDescription = "Periodo vacacional según calendario académico",
                location = "Instituto",
                colorIndex = 6,
                startDate = LocalDate.of(2025, 7, 7),
                endDate = LocalDate.of(2025, 7, 31),
                type = PersonalEventType.SUBSCRIBED,
                institutionalEventId = 3,
                imagePath = "",
                items = emptyList(),
                notification = null,
                isVisible = true,
                createdAt = now,
                updatedAt = null
            ),
            PersonalEvent(
                id = 0,
                title = "Actividades intersemestrales",
                shortDescription = "Actividades entre semestres",
                longDescription = "Periodo de actividades intersemestrales",
                location = "Instituto",
                colorIndex = 5,
                startDate = LocalDate.of(2025, 6, 9),
                endDate = LocalDate.of(2025, 7, 4),
                type = PersonalEventType.SUBSCRIBED,
                institutionalEventId = 4,
                imagePath = "",
                items = emptyList(),
                notification = null,
                isVisible = true,
                createdAt = now,
                updatedAt = null
            ),
            PersonalEvent(
                id = 0,
                title = "Inicio de clases",
                shortDescription = "Primer día de clases",
                longDescription = "Inicio del periodo académico",
                location = "Instituto Tecnológico de Puebla",
                colorIndex = 14,
                startDate = LocalDate.of(2025, 1, 26),
                endDate = LocalDate.of(2025, 1, 26),
                type = PersonalEventType.SUBSCRIBED,
                institutionalEventId = 5,
                imagePath = "",
                items = listOf(
                    PersonalEventItem(
                        id = 1,
                        personalEventId = 0,
                        iconName = "school",
                        text = "Horario",
                        value = "07:00 - 15:00",
                        isClickable = false
                    ),
                    PersonalEventItem(
                        id = 2,
                        personalEventId = 0,
                        iconName = "location",
                        text = "Lugar",
                        value = "Aulas asignadas",
                        isClickable = false
                    )
                ),
                notification = PersonalEventNotification(
                    id = 2,
                    personalEventId = 0,
                    time = 43200000, // 12 horas antes
                    title = "Inicio de clases",
                    message = "Mañana inician las clases",
                    isEnabled = true
                ),
                isVisible = true,
                createdAt = now,
                updatedAt = null
            ),
            PersonalEvent(
                id = 0,
                title = "Convocatoria Servicio Social",
                shortDescription = "Registro para servicio social",
                longDescription = "Estimado estudiante de TICs si le interesa realizar su servicio social durante el periodo Diciembre 2024 - Junio 2025 guardar esta información Coordinación Instruccional de tutorías Desarrollo Académico.",
                location = "Edificio 6",
                colorIndex = 12,
                startDate = LocalDate.of(2025, 11, 28),
                endDate = LocalDate.of(2025, 11, 29),
                type = PersonalEventType.SUBSCRIBED,
                institutionalEventId = 6,
                imagePath = "", // Aquí iría R.drawable.seminario.toString() si tienes la imagen
                items = listOf(
                    PersonalEventItem(
                        id = 1,
                        personalEventId = 0,
                        iconName = "person",
                        text = "Coordinación",
                        value = "Tutorías Desarrollo Académico",
                        isClickable = false
                    ),
                    PersonalEventItem(
                        id = 2,
                        personalEventId = 0,
                        iconName = "schedule",
                        text = "Periodo",
                        value = "Diciembre 2024 - Junio 2025",
                        isClickable = false
                    )
                ),
                notification = PersonalEventNotification(
                    id = 3,
                    personalEventId = 0,
                    time = 86400000, // 1 día antes
                    title = "Recordatorio",
                    message = "Convocatoria Servicio Social mañana",
                    isEnabled = true
                ),
                isVisible = true,
                createdAt = now,
                updatedAt = null
            ),
            PersonalEvent(
                id = 0,
                title = "Examen Final Matemáticas",
                shortDescription = "Examen final de matemáticas discretas",
                longDescription = "Examen final correspondiente a la materia de matemáticas discretas",
                location = "Aula 101",
                colorIndex = 8,
                startDate = LocalDate.of(2025, 6, 15),
                endDate = LocalDate.of(2025, 6, 15),
                type = PersonalEventType.PERSONAL,
                institutionalEventId = null,
                imagePath = "",
                items = listOf(
                    PersonalEventItem(
                        id = 1,
                        personalEventId = 0,
                        iconName = "schedule",
                        text = "Hora",
                        value = "08:00 - 10:00",
                        isClickable = false
                    ),
                    PersonalEventItem(
                        id = 2,
                        personalEventId = 0,
                        iconName = "person",
                        text = "Profesor",
                        value = "Dr. García",
                        isClickable = false
                    )
                ),
                notification = PersonalEventNotification(
                    id = 4,
                    personalEventId = 0,
                    time = 7200000, // 2 horas antes
                    title = "Examen próximo",
                    message = "Tu examen de matemáticas es en 2 horas",
                    isEnabled = true
                ),
                isVisible = true,
                createdAt = now,
                updatedAt = null
            )
        )
    }}