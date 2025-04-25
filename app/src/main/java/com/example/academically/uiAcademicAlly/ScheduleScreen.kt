package com.example.academically.uiAcademicAlly


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.academically.data.Schedule
import com.example.academically.data.SampleScheduleData
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleScreen(
    schedules: List<Schedule>,
    modifier: Modifier = Modifier
) {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    val currentDay = remember(currentDate) {
        DaysOfWeek.entries.find { it.name == currentDate.dayOfWeek.name }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header con navegación de días
        DayNavigationHeader(
            currentDate = currentDate,
            onPreviousDay = { currentDate = currentDate.minusDays(1) },
            onNextDay = { currentDate = currentDate.plusDays(1) },
            onWeekView = { /* Implementar después */ }
        )

        // Vista de horario diario
        AnimatedContent(
            targetState = currentDate,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { width -> width } togetherWith
                            slideOutHorizontally { width -> -width }
                } else {
                    slideInHorizontally { width -> -width } togetherWith
                            slideOutHorizontally { width -> width }
                }
            }, label = ""
        ) { date ->
            DailyScheduleView(
                schedules = schedules,
                currentDay = currentDay ?: DaysOfWeek.LUNES
            )
            currentDate = date
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayNavigationHeader(
    currentDate: LocalDate,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onWeekView: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousDay) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Día anterior")
        }

        Text(
            text = currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
                .replaceFirstChar { it.uppercase() },
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        IconButton(onClick = onNextDay) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Siguiente día")
        }

        IconButton(onClick = onWeekView) {
            Icon(
                Icons.Default.CalendarViewWeek,
                modifier = Modifier.size(42.dp),
                contentDescription = "Vista semanal"
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyScheduleView(
    schedules: List<Schedule>,
    currentDay: DaysOfWeek,
    modifier: Modifier = Modifier
) {
    // Filtrar horarios para el día actual
    val daySchedules = schedules.mapNotNull { schedule ->
        schedule.times.find { it.day == currentDay }?.let { time ->
            schedule to time
        }
    }.sortedBy { it.second.hourStart }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Columna de horas
        TimeColumn()

        // Columna de eventos
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            // Crear una fila por cada hora desde las 7:00 hasta las 20:00
            for (hour in 7..20) {
                item {
                    HourRow(
                        hour = hour,
                        daySchedules = daySchedules,
                        currentDay = currentDay
                    )
                }
            }
        }
    }
}

@Composable
fun TimeColumn(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(50.dp)
            .fillMaxHeight()
    ) {
        for (hour in 7..20) {
            Text(
                text = String.format("%02d:00", hour),
                modifier = Modifier
                    .height(100.dp)
                    .padding(end = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourRow(
    hour: Int,
    daySchedules: List<Pair<Schedule, com.example.academically.data.ScheduleTime>>,
    currentDay: DaysOfWeek,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        // Línea divisoria de hora
        HorizontalDivider(
            modifier = Modifier.align(Alignment.TopCenter),
            color = Color.LightGray.copy(alpha = 0.5f)
        )

        // Eventos que comienzan en esta hora
        daySchedules.filter { (_, time) ->
            time.hourStart.hour == hour
        }.forEach { (schedule, time) ->
            val startMinutes = time.hourStart.minute
            val durationMinutes = (time.hourEnd.hour - time.hourStart.hour) * 60 +
                    (time.hourEnd.minute - time.hourStart.minute)

            ScheduleCard(
                schedule = schedule,
                time = time,
                modifier = Modifier
                    .offset(y = (startMinutes * 1).dp) // Ajustar según minutos
                    .height((durationMinutes * 1).dp) // Altura basada en duración
            )
        }
    }
}

@Composable
fun ScheduleCard(
    schedule: Schedule,
    time: com.example.academically.data.ScheduleTime,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = schedule.color.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                text = schedule.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                fontSize = 14.sp
            )

            Text(
                text = schedule.place,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp
            )

            Text(
                text = "Docente: ${schedule.teacher}",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ScheduleScreenPreview() {
    MaterialTheme {
        ScheduleScreen(
            schedules = SampleScheduleData.getSampleSchedules()
        )
    }
}