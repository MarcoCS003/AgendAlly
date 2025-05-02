package com.example.academically.uiAcademicAlly

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.academically.R
import com.example.academically.data.*
import java.time.LocalDate

enum class DaysOfWeek {
    DOMINGO, LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO;
}

@Composable
fun CalendarAppScreen(
    mounts: List<MountAcademicAlly> = emptyList(),
    currentMonthIndex: Int = 0,
    processedEvents: Map<Int, Map<Int, ProcessedEvent>> = emptyMap(),
    onAddEventClick: () -> Unit
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
                    processedEvents = monthEvents
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
            onClick = {
                onAddEventClick()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(65.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar evento")
        }
    }
}

@Composable
fun CalendarCard(
    mes: MountAcademicAlly,
    processedEvents: Map<Int, ProcessedEvent>
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
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(8.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
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

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Colapsar" else "Expandir"
                    )
                }
            }

            // Encabezado de los días de la semana
            TopCalendarView()

            // Cuadrícula del calendario
            CalendarArray(mes, processedEvents)

            // Información de eventos (solo si está expandido)
            if (expanded) {
                EventInformation(processedEvents)
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
            .padding(8.dp)
    ) {
        for (day in DaysOfWeek.entries) {
            Text(text = "${day.name.first()}", fontSize = 10.sp)
        }
    }
}


@Composable
fun CalendarArray(
    mount: MountAcademicAlly,
    processedEvents: Map<Int, ProcessedEvent>
) {
    Column {
        for (week in mount.weeks) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (day in week) {
                    if (day == 0) {
                        // Espacio vacío para días que no existen en este mes
                        DayView(dayNumber = "")
                    } else {
                        // Verificamos si hay un evento para este día
                        val processedEvent = processedEvents[day]
                        if (processedEvent != null) {
                            // Si hay eventos adicionales, usamos MultiEventDayView
                            if (processedEvent.additionalEvents.isNotEmpty()) {
                                MultiEventDayView(
                                    dayNumber = day.toString(),
                                    processedEvent = processedEvent
                                )
                            } else {
                                // Si solo hay un evento, usamos la visualización normal
                                DayWithEvent(
                                    dayNumber = day.toString(),
                                    event = processedEvent.event,
                                    shape = processedEvent.shape
                                )
                            }
                        } else {
                            // Día normal sin evento
                            DayView(dayNumber = day.toString())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayView(dayNumber: String) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = dayNumber)
    }
}

@Composable
fun DayWithEvent(
    dayNumber: String,
    event: Event,
    shape: EventShape,
    modifier: Modifier = Modifier.size(40.dp),
    additionalEvents: List<Event> = emptyList()
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
            modifier = modifier
        )
    } else {
        // Si solo hay un evento, usamos la visualización normal
        Box(
            modifier = modifier
                .background(event.color, shape.toShape()),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = dayNumber,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun EventInformation(processedEvents: Map<Int, ProcessedEvent>) {
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

// Mostrar el calendario...

// Si hay un evento seleccionado, mostrar la tarjeta
    selectedEvent?.let { event ->
        EventDetailCard(
            event = event,
            onDismiss = { selectedEvent = null },
            onDelete = { /* Implementar eliminación */ },
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
                EventButton(event = event, onClick = {selectedEvent = event})
            }
        }
    }
}

/**
 * Botón para mostrar un evento en la lista de eventos
 */
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
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
        ) {
            // Indicador de color
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(event.color, RoundedCornerShape(4.dp))
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

                // Si existe descripción corta, la mostramos
                if (event.shortDescription.isNotEmpty()) {
                    Text(
                        text = event.shortDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

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



@SuppressLint("NewApi")
@Preview(showSystemUi = true)
@Composable
fun CalendarPreview() {
    // Crear eventos de muestra
    val events = listOf(
        // Eventos originales convertidos al nuevo formato
        Event(
            id = 1,
            color = Color(0xFF80DEEA),
            title = "Suspension de clases",
            startDate = LocalDate.of(2025, 1, 1),
            endDate = LocalDate.of(2025, 1, 1),
            mesID = 1
        ),
        Event(
            id = 2,
            color = Color(0xFFFFF59D),
            title = "Suspension de clases",
            startDate = LocalDate.of(2025, 2, 3),
            endDate = LocalDate.of(2025, 2, 3),
            mesID = 1
        ),
        Event(
            id = 3,
            color = Color(0xFFFFAB91),
            title = "Suspension de clases",
            startDate = LocalDate.of(2025, 3, 17),
            endDate = LocalDate.of(2025, 3, 17),
            mesID = 1
        ),
        Event(
            id = 4,
            color = Color(0xFFC5E1A5),
            title = "Periodo vacacional",
            startDate = LocalDate.of(2025, 1, 1),
            endDate = LocalDate.of(2025, 1, 8),
            mesID = 1
        ),
        Event(
            id = 5,
            color = Color(0xFFB39DDB),
            title = "Periodo vacacional",
            startDate = LocalDate.of(2025, 4, 14),
            endDate = LocalDate.of(2025, 4, 25),
            mesID = 1
        ),
        Event(
            id = 6,
            color = Color(0xFFFFCC80),
            title = "Inicio de clases",
            startDate = LocalDate.of(2025, 1, 28),
            endDate = LocalDate.of(2025, 1, 28),
            mesID = 5,
            shape = EventShape.Circle
        ),
        Event(
            id = 7,
            color = Color(0xFFCE93D8),
            title = "Mi cumpleaños",
            startDate = LocalDate.of(2025, 2, 28),
            endDate = LocalDate.of(2025, 2, 28),
            mesID = 2,
            shape = EventShape.Circle
        ),
        // Evento que abarca varios meses
        Event(
            id =8 ,
            color = Color(0xFF90CAF9),
            title = "Suspension de clases",
            startDate = LocalDate.of(2025, 5, 1),
            endDate = LocalDate.of(2025, 5, 1),
            mesID = 1
        ),Event(
            id = 9,
            color = Color(0xFFF48FB1),
            title = "Suspension de clases",
            startDate = LocalDate.of(2025, 5, 5),
            endDate = LocalDate.of(2025, 5, 5),
            mesID = 1
        ),Event(
            id = 10,
            color = Color(0xFF81D4FA),
            title = "Suspension de clases",
            startDate = LocalDate.of(2025, 5, 15),
            endDate = LocalDate.of(2025, 5, 15),
            mesID = 1
        ),
        Event(
            id = 11,
            color = Color(0xFFFFD54F),
            title = "Suspension de clases",
            startDate = LocalDate.of(2025, 5, 15),
            endDate = LocalDate.of(2025, 5, 15),
            mesID = 1
        ),
        Event(
            id = 12,
            color = Color(0xFF4DB6AC),
            title = "Suspension de clases",
            startDate = LocalDate.of(2025, 5, 15),
            endDate = LocalDate.of(2025, 5, 15),
            mesID = 1
        ),
        Event(
            id = 13,
            color = Color(0xFF9575CD),
            title = "Fin de clases",
            startDate = LocalDate.of(2025, 5, 30),
            endDate = LocalDate.of(2025, 5, 30),
            mesID = 1
        ),
        Event(
            id = 14,
            title = "Convocatoria Servicio Social",
            shortDescription = "Registro para servicio",
            longDescription = "Estimado estudiante de TICs si le interesa realizar su servicio social durante el periodo Diciembre 2024 - Junio 2025 guardar esta información Coordinación Instruccional de tutorías Desarrollo Académico.",
            location = "Edificio 6",
            imagePath = R.drawable.seminario.toString(),
            startDate = LocalDate.of(2025, 11, 28),
            endDate = LocalDate.of(2025, 11, 29),
            category = EventCategory.CAREER,
            color = Color(0xFFE57373), // Cian
            items = listOf(
                EventItem(1, Icons.Default.Person, "Coordinación Instruccional de tutorías"),
                EventItem(2, Icons.Default.Call, "123456789")
            ),
            notification = EventNotification(
                id = 1,
                time = 86400000, // 1 día
                title = "Recordatorio",
                message = "Convocatoria Servicio Social mañana",
                isEnabled = true
            )
        )
    )

    // Crear proveedor de calendario
    val calendarProvider = SystemCalendarProvider(LocalContext.current)

    // Obtener datos de los meses
    val months = calendarProvider.getMonthsData()

    // Mes actual
    val currentMonthIndex = calendarProvider.getCurrentMonthIndex()

    // Procesar eventos para todos los meses
    val allProcessedEvents = mutableMapOf<Int, Map<Int, ProcessedEvent>>()

    // Preparar eventos procesados para cada mes
    for (month in months) {
        val monthEvents = EventProcessor.processEvents(
            month.id,
            2025,
            events
        )
        allProcessedEvents[month.id] = monthEvents
    }

    // Renderizar calendario
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        CalendarAppScreen(
            mounts = months,
            currentMonthIndex = currentMonthIndex,
            processedEvents = allProcessedEvents,
            onAddEventClick = {}
        )
    }
}

@Composable
fun MultiEventDayView(
    dayNumber: String,
    processedEvent: ProcessedEvent,
    modifier: Modifier = Modifier
) {
    val totalEvents = 1 + processedEvent.additionalEvents.size
    val allEvents = listOf(processedEvent.event) + processedEvent.additionalEvents

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Dibujamos el pastel de eventos
        Canvas (modifier = Modifier.fillMaxSize()) {
            drawPieChart(allEvents, totalEvents)
        }

        // Texto del día sobre el pastel
        Text(
            text = dayNumber,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

/**
 * Extension function para dibujar un pastel de eventos en el Canvas
 */
private fun DrawScope.drawPieChart(events: List<Event>, totalSlices: Int) {
    
    val sweepAngle = 360f / totalSlices

    events.forEachIndexed { index, event ->
        val startAngle = index * sweepAngle+90

        drawArc(
            color = event.color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = true,
            topLeft = Offset(0f, 0f),
            size = Size(size.width, size.height),
        )
    }
}