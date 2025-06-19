package com.example.academically.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.academically.ui.theme.DarkThemeScheduleColors
import com.example.academically.ui.theme.LightThemeScheduleColors
import com.example.academically.uiAcademicAlly.calendar.DaysOfWeek
import java.time.LocalTime

data class ScheduleTime(
    val day: DaysOfWeek,
    val hourStart: LocalTime,
    val hourEnd: LocalTime
)

data class Schedule(
    val id: Int,
    val colorIndex: Int, // Reemplaza color: Color con colorIndex: Int
    val name: String,
    val place: String,
    val teacher: String,
    val times: List<ScheduleTime>
) {
    // Propiedad para obtener el color según el tema actual
    @Composable
    fun getColor(): Color {
        val isDarkTheme = isSystemInDarkTheme()
        val colors = if (isDarkTheme) DarkThemeScheduleColors else LightThemeScheduleColors
        return colors.getOrElse(colorIndex) { colors.first() }
    }
}

object SampleScheduleData {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getSampleSchedules(): List<Schedule> {
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
}

