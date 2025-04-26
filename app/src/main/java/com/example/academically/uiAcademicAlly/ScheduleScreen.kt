package com.example.academically.uiAcademicAlly


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.academically.data.SampleScheduleData
import com.example.academically.data.Schedule
import com.example.academically.data.ScheduleTime
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

enum class ViewMode {
    DAILY, WEEKLY
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleScreen(
    schedules: List<Schedule>,
    modifier: Modifier = Modifier,
    onAddActivity: () -> Unit = {}
) {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var viewMode by remember { mutableStateOf(ViewMode.DAILY) }
    val (startHour, endHour) = remember(schedules) {
        calculateHourRange(schedules)
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddActivity
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir actividad")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header con navegación adaptable según el modo de vista
            when (viewMode) {
                ViewMode.DAILY -> {
                    DayNavigationHeader(
                        currentDate = currentDate,
                        onPreviousDay = { currentDate = currentDate.minusDays(1) },
                        onNextDay = { currentDate = currentDate.plusDays(1) },
                        onViewModeChange = { viewMode = ViewMode.WEEKLY }
                    )
                }
                ViewMode.WEEKLY -> {
                    WeekNavigationHeader(
                        currentDate = currentDate,
                        onPreviousWeek = { currentDate = currentDate.minusWeeks(1) },
                        onNextWeek = { currentDate = currentDate.plusWeeks(1) },
                        onViewModeChange = { viewMode = ViewMode.DAILY }
                    )
                }
            }

            // Vista de horario con animación
            AnimatedContent(
                targetState = viewMode,
                transitionSpec = {
                    if (targetState == ViewMode.WEEKLY) {
                        slideInHorizontally { width -> width } togetherWith
                                slideOutHorizontally { width -> -width }
                    } else {
                        slideInHorizontally { width -> -width } togetherWith
                                slideOutHorizontally { width -> width }
                    }
                },
                label = "view_mode_animation"
            ) { mode ->
                when (mode) {
                    ViewMode.DAILY -> {
                        val currentDay = convertDayOfWeekToDaysOfWeek(currentDate.dayOfWeek)
                        DailyScheduleView(
                            schedules = schedules,
                            currentDay = currentDay,
                            startHour = startHour,
                            endHour = endHour
                        )
                    }
                    ViewMode.WEEKLY -> {
                        WeeklyScheduleView(
                            schedules = schedules,
                            currentDate = currentDate,
                            startHour = startHour,
                            endHour = endHour
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun calculateHourRange(schedules: List<Schedule>): Pair<Int, Int> {
    if (schedules.isEmpty()) return Pair(9, 17) // Valores por defecto

    var minHour = 24
    var maxHour = 0

    schedules.forEach { schedule ->
        schedule.times.forEach { time ->
            minHour = minOf(minHour, time.hourStart.hour)
            maxHour = maxOf(maxHour, time.hourEnd.hour)
        }
    }

    // Asegurarse de que hay al menos un rango mínimo
    return Pair(minHour, maxHour)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayNavigationHeader(
    currentDate: LocalDate,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onViewModeChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp),
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

        IconButton(onClick = onViewModeChange) {
            Icon(
                Icons.Default.CalendarViewWeek,
                modifier = Modifier.size(38.dp).padding(horizontal = 5.dp),
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
    startHour: Int,
    endHour: Int,
    modifier: Modifier = Modifier
) {
    val hourHeight = 100
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        // Fondo con líneas de horas
        TimeGridBackground(hourHeight, startHour, endHour)

        // Contenido principal
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Columna de horas
            TimeColumn(hourHeight, startHour, endHour)

            // Columna de eventos
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // Filtrar y posicionar horarios para el día actual
                val daySchedules = schedules.mapNotNull { schedule ->
                    schedule.times.find { it.day == currentDay }?.let { time ->
                        schedule to time
                    }
                }

                daySchedules.forEach { (schedule, time) ->
                    PositionedScheduleCard(
                        schedule = schedule,
                        time = time,
                        hourHeight = hourHeight,
                        baseHour = startHour
                    )
                }
            }
        }
    }
}

@Composable
fun TimeGridBackground(
    hourHeight: Int,
    startHour: Int,
    endHour: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        for (hour in startHour..endHour) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(hourHeight.dp)
            ) {
                HorizontalDivider(
                    modifier = Modifier.align(Alignment.TopCenter),
                    color = Color.LightGray.copy(alpha = 0.3f)
                )
            }
        }
    }
}


@Composable
fun TimeColumn(
    hourHeight: Int,
    startHour: Int,
    endHour: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(50.dp)
            .fillMaxHeight()
    ) {
        for (hour in startHour..endHour) {
            Text(
                text = String.format(Locale.getDefault(), "%02d:00", hour),
                modifier = Modifier
                    .height(hourHeight.dp)
                    .padding(end = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PositionedScheduleCard(
    schedule: Schedule,
    time: ScheduleTime,
    hourHeight: Int,
    baseHour: Int,
    modifier: Modifier = Modifier
) {
    val minutesPerPixel = hourHeight / 60f

    // Calcular la posición desde la hora base
    val startMinutesFromBase = (time.hourStart.hour - baseHour) * 60 + time.hourStart.minute
    val topOffset = (startMinutesFromBase * minutesPerPixel).dp

    // Calcular la duración
    val durationMinutes = (time.hourEnd.hour * 60 + time.hourEnd.minute) -
            (time.hourStart.hour * 60 + time.hourStart.minute)
    val cardHeight = (durationMinutes.times(minutesPerPixel)).dp

    ScheduleCard(
        schedule = schedule,
        time = time,
        modifier = modifier
            .padding(start = 4.dp, end = 4.dp)
            .offset(y = topOffset)
            .height(cardHeight)
            .fillMaxWidth()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleCard(
    schedule: Schedule,
    time: ScheduleTime,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = schedule.color.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Text(
                text = schedule.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = schedule.place,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 12.sp,
                color = Color.Black
            )

            // Mostrar horario específico
            Text(
                text = "${time.hourStart.toString().substring(0, 5)} - ${time.hourEnd.toString().substring(0, 5)}",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = Color.Black.copy(alpha = 0.7f)
            )

            if (time.hourEnd.hour - time.hourStart.hour > 1) { // Mostrar profesor si hay espacio
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Docente: ${schedule.teacher}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// Función auxiliar para convertir DayOfWeek a DaysOfWeek
@RequiresApi(Build.VERSION_CODES.O)
private fun convertDayOfWeekToDaysOfWeek(dayOfWeek: DayOfWeek): DaysOfWeek {
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