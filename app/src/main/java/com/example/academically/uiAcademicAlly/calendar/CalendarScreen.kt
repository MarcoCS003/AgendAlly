package com.example.academically.uiAcademicAlly.calendar

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academically.ViewModel.EventViewModel
import com.example.academically.data.*
import com.example.academically.data.database.AcademicAllyDatabase
import com.example.academically.data.repository.EventRepository
import com.example.academically.ui.theme.ScheduleColorsProvider

import java.time.LocalDate

enum class DaysOfWeek {
    DOMINGO, LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO;
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreenWithViewModel(
    viewModel: EventViewModel = viewModel(
        factory = EventViewModel.Factory(
            EventRepository(
                AcademicAllyDatabase.getDatabase(LocalContext.current).eventDao()
            )
        )
    ),
    onAddEventClick: () -> Unit = {},
    onEditEventClick: (Event) -> Unit = {} // Parámetro para la navegación
) {
    // Crear dependencias para el ViewModel
    val context = LocalContext.current
    val database = AcademicAllyDatabase.getDatabase(context)
    val repository = EventRepository(database.eventDao())
    // Obtener estados del ViewModel
    val allEvents by viewModel.allEvents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val currentYearMonth by viewModel.currentYearMonth.collectAsState()

    // Crear proveedor de calendario
    val calendarProvider = SystemCalendarProvider(LocalContext.current)

    // Obtener datos de los meses
    val months = calendarProvider.getMonthsData()

    // Mes actual
    val currentMonthIndex = calendarProvider.getCurrentMonthIndex()

    // Procesar eventos para todos los meses
    val allProcessedEvents = remember(allEvents) {
        mutableMapOf<Int, Map<Int, ProcessedEvent>>().apply {
            for (month in months) {
                val monthEvents = EventProcessor.processEvents(
                    month.id,
                    currentYearMonth.year,
                    allEvents
                )
                this[month.id] = monthEvents
            }
        }
    }

    // Mostrar mensaje de error si existe
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            // Aquí puedes mostrar un SnackBar u otro componente para mostrar el error
        }
    }

    // Pantalla principal de calendario
    Box {
        if (isLoading) {
            // Mostrar indicador de carga
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center)
            )
        } else {
            // Mostrar el calendario con los eventos
            CalendarAppScreen(
                mounts = months,
                currentMonthIndex = currentMonthIndex,
                processedEvents = allProcessedEvents,
                onAddEventClick = onAddEventClick,  // Pasar la función de navegación
                onEditEventClick = onEditEventClick
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarAppScreen(
    mounts: List<MountAcademicAlly> = emptyList(),
    currentMonthIndex: Int = 0,
    processedEvents: Map<Int, Map<Int, ProcessedEvent>> = emptyMap(),
    onAddEventClick: () -> Unit,
    onEditEventClick: (Event) -> Unit = {}
) {
    Box {
        // Estado para el scroll de LazyColumn
        val lazyListState = rememberLazyListState()

        // Scroll inicial al mes actual
        LaunchedEffect(currentMonthIndex) {
            if (mounts.isNotEmpty() && currentMonthIndex < mounts.size) {
                lazyListState.scrollToItem(currentMonthIndex)
            }
        }

        // LazyColumn con todos los meses
        LazyColumn(
            state = lazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            // Mostrar todos los meses
            items(mounts) { month ->
                // Obtener eventos procesados para este mes
                val monthEvents = processedEvents[month.id] ?: emptyMap()

                CalendarCard(
                    mes = month,
                    processedEvents = monthEvents,
                    onEditEvent = onEditEventClick
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Espacio adicional al final para el FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Botón para añadir evento
        FloatingActionButton(
            onClick = onAddEventClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(65.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar evento")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarCard(
    mes: MountAcademicAlly,
    processedEvents: Map<Int, ProcessedEvent>,
    onEditEvent: (Event) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme())
                MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
            else
                Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        onClick = { expanded = !expanded },
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(8.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
        ) {
            // Título del mes con botón de expansión
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = mes.name,
                    modifier = Modifier.padding(4.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = 2.dp),
                ) {
                    Text("Eventos", style = TextStyle(color = Color.Gray), fontSize = 12.sp)
                    Spacer(Modifier.size(2.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Colapsar" else "Expandir",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Encabezado de los días de la semana
            TopCalendarView()

            // Cuadrícula del calendario
            CalendarArray(mes, processedEvents)

            // Información de eventos (solo si está expandido)
            if (expanded) {
                EventInformation(
                    processedEvents,
                    onEditEvent = onEditEvent
                )
            }
        }
    }
}

@Composable
fun TopCalendarView() {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        for (day in DaysOfWeek.entries) {
            Text(text = "${day.name.first()}", fontSize = 10.sp)
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun CalendarArray(
    mount: MountAcademicAlly,
    processedEvents: Map<Int, ProcessedEvent>
) {
    // Obtener fecha actual para destacar el día actual
    val today = LocalDate.now()
    val currentDay = today.dayOfMonth
    val currentMonth = today.monthValue

    Column {
        for (week in mount.weeks) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (day in week) {
                    if (day == 0) {
                        // Espacio vacío para días que no existen en este mes
                        DayView(dayNumber = "")
                    } else {
                        // Determinar si este es el día actual
                        val isToday = day == currentDay && mount.id == currentMonth

                        // Verificamos si hay un evento para este día
                        val processedEvent = processedEvents[day]
                        if (processedEvent != null) {
                            // Si hay eventos adicionales, usamos MultiEventDayView
                            if (processedEvent.additionalEvents.isNotEmpty()) {
                                MultiEventDayView(
                                    dayNumber = day.toString(),
                                    processedEvent = processedEvent,
                                    isToday = isToday
                                )
                            } else {
                                // Si solo hay un evento, usamos la visualización normal
                                DayWithEvent(
                                    dayNumber = day.toString(),
                                    event = processedEvent.event,
                                    shape = processedEvent.shape,
                                    isToday = isToday
                                )
                            }
                        } else {
                            // Día normal sin evento
                            DayView(
                                dayNumber = day.toString(),
                                isToday = isToday
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayView(
    dayNumber: String,
    isToday: Boolean = false
) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp),
        contentAlignment = Alignment.Center
    ) {
        // Si es hoy, añadir un círculo de contorno
        if (isToday && dayNumber.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
        }

        Text(text = dayNumber)
    }
}

@Composable
fun DayWithEvent(
    dayNumber: String,
    event: Event,
    shape: EventShape,
    modifier: Modifier = Modifier.size(40.dp),
    additionalEvents: List<Event> = emptyList(),
    isToday: Boolean = false
) {
    // Si hay eventos adicionales, usamos la visualización tipo pastel
    if (additionalEvents.isNotEmpty()) {
        MultiEventDayView(
            dayNumber = dayNumber,
            processedEvent = ProcessedEvent(
                day = dayNumber.toIntOrNull() ?: 0,
                event = event,
                shape = shape,
                additionalEvents = additionalEvents
            ),
            modifier = modifier,
            isToday = isToday
        )
    } else {
        // Si solo hay un evento, usamos la visualización normal con posible contorno
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            val colors = ScheduleColorsProvider.getColors()
            // Fondo del evento con la forma correspondiente
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        if (colors.isNotEmpty())
                            colors[event.colorIndex % colors.size]
                        else
                            Color.Gray,
                        shape.toShape()
                    )
            )

            // Si es hoy, añadir un contorno que sigue la forma del evento
            if (isToday) {
                Box(
                    modifier = Modifier
                        .size(38.dp) // Ligeramente más grande para el contorno
                        .border(
                            width = 3.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = shape.toShape()
                        )
                )
            }

            Text(
                text = dayNumber,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventInformation(
    processedEvents: Map<Int, ProcessedEvent>,
    onEditEvent: (Event) -> Unit = {}
) {
    // Extraemos todos los eventos incluyendo los adicionales
    val allEvents = mutableListOf<Event>()

    processedEvents.values.forEach { processedEvent ->
        allEvents.add(processedEvent.event)
        allEvents.addAll(processedEvent.additionalEvents)
    }

    // Filtramos eventos duplicados por ID
    val uniqueEvents = allEvents.distinctBy { it.id }

    // En tu composable principal:
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    // Si hay un evento seleccionado, mostrar la tarjeta
    selectedEvent?.let { event ->
        EventDetailCardWithViewModel(
            event = event,
            onDismiss = { selectedEvent = null },
            onEditEvent = onEditEvent
        )
    }

    Column(Modifier.padding(2.dp)) {
        Text(
            text = "Eventos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (uniqueEvents.isEmpty()) {
            Text(
                text = "No hay eventos para este mes",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        } else {
            for (event in uniqueEvents) {
                EventButton(event = event, onClick = { selectedEvent = event })
            }
        }
    }
}

@Composable
fun EventButton(
    event: Event,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 1.dp,
            pressedElevation = 4.dp
        ),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            val colors = ScheduleColorsProvider.getColors()
            // Indicador de color
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        if (colors.isNotEmpty())
                            colors[event.colorIndex % colors.size]
                        else
                            Color.Gray,
                        RoundedCornerShape(4.dp)
                    )
            )

            // Descripción del evento
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                // Título del evento (anteriormente era description)
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val date = formatEventDate(event.startDate, event.endDate)
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Si existe ubicación, la mostramos
                if (event.location.isNotEmpty()) {
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun MultiEventDayView(
    dayNumber: String,
    processedEvent: ProcessedEvent,
    modifier: Modifier = Modifier,
    isToday: Boolean = false
) {
    val totalEvents = 1 + processedEvent.additionalEvents.size
    val allEvents = listOf(processedEvent.event) + processedEvent.additionalEvents

    // Obtener forma del evento principal para mantener consistencia
    val shape = processedEvent.shape

    // Obtener los colores aquí, en el composable
    val colors = ScheduleColorsProvider.getColors()

    // Si no hay colores disponibles, mostrar un día normal
    if (colors.isEmpty()) {
        DayView(dayNumber = dayNumber, isToday = isToday)
        return
    }

    Box(
        modifier = modifier
            .size(40.dp),
        contentAlignment = Alignment.Center
    ) {
        // Dibujamos el pastel de eventos con los colores reales, usando la forma del evento principal
        Canvas(modifier = Modifier
            .size(36.dp)
            .clip(shape.toShape())) {
            // Usar un lambda para tener acceso al DrawScope
            drawPieChartWithColors(allEvents, totalEvents, colors, isMultiDay = true)
        }

        // Si es hoy, añadir un contorno que sigue la forma del evento
        if (isToday) {
            Box(
                modifier = Modifier
                    .size(38.dp) // Ligeramente más grande para el contorno
                    .border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = shape.toShape()
                    )
            )
        }

        // Texto del día sobre el pastel
        Text(
            text = dayNumber,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

// Función auxiliar mejorada que maneja correctamente errores potenciales
private fun DrawScope.drawPieChartWithColors(
    events: List<Event>,
    totalSlices: Int,
    colors: List<Color>,
    isMultiDay: Boolean = false
) {
    // Verificación de seguridad
    if (events.isEmpty() || totalSlices <= 0 || colors.isEmpty()) return

    val sweepAngle = 360f / totalSlices

    events.forEachIndexed { index, event ->
        // Asegurarse de que no nos pasemos del número de sectores
        if (index >= totalSlices) return@forEachIndexed

        // Cambiar el ángulo inicial a 180 para eventos de varios días
        val startAngle = index * sweepAngle + (if (isMultiDay) 180 else 90)

        // Verificación de seguridad para colorIndex
        val safeColorIndex = event.colorIndex.coerceIn(0, colors.size - 1)

        drawArc(
            color = colors[safeColorIndex],
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = true,
            topLeft = Offset(0f, 0f),
            size = Size(size.width, size.height),
        )
    }
}