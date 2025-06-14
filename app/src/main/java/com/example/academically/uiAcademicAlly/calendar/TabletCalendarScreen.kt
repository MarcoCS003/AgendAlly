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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academically.ViewModel.EventViewModel
import com.example.academically.data.database.AcademicAllyDatabase
import com.example.academically.data.repository.PersonalEventRepository
import com.example.academically.data.*
import com.example.academically.ui.theme.ScheduleColorsProvider
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TabletCalendarScreen(
    viewModel: EventViewModel = viewModel(
        factory = EventViewModel.Factory(
            PersonalEventRepository(
                AcademicAllyDatabase.getDatabase(LocalContext.current).personalEventDao()
            )
        )
    )
) {
    var showAddEventForm by remember { mutableStateOf(false) }
    var editingEvent by remember { mutableStateOf<PersonalEvent?>(null) }

    Row(modifier = Modifier.fillMaxSize()) {
        // Panel principal del calendario
        Box(
            modifier = Modifier
                .weight(if (showAddEventForm || editingEvent != null) 0.6f else 1f)
                .fillMaxHeight()
                .padding(vertical = 15.dp)
        ) {
            TabletCalendarContent(
                viewModel = viewModel,
                onAddEventClick = { showAddEventForm = true },
                onEditEventClick = { event -> editingEvent = event }
            )

            // FAB para añadir evento
            FloatingActionButton(
                onClick = { showAddEventForm = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(50.dp)
                    .size(65.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar evento")
            }
        }

        // Panel lateral para formularios
        if (showAddEventForm || editingEvent != null) {
            Card(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface // Forzar color de superficie
                )            ) {
                Column {


                    // Contenido del formulario
                    if (editingEvent != null) {
                        EditEventScreenWithViewModel(
                            eventId = editingEvent!!.id,
                            viewModel = viewModel,
                            onBack = { editingEvent = null }
                        )
                    } else {
                        AddEventScreenWithViewModel(
                            viewModel = viewModel,
                            onBack = { showAddEventForm = false }
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TabletCalendarContent(
    viewModel: EventViewModel,
    onAddEventClick: () -> Unit,
    onEditEventClick: (PersonalEvent) -> Unit
) {
    val allEvents by viewModel.allEvents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val currentYearMonth by viewModel.currentYearMonth.collectAsState()

    val calendarProvider = SystemCalendarProvider(LocalContext.current)
    val months = calendarProvider.getMonthsData()

    val allProcessedEvents = remember(allEvents) {
        mutableMapOf<Int, Map<Int, ProcessedPersonalEvent>>().apply {
            for (month in months) {
                val monthEvents = EventProcessor.processPersonalEvents(
                    month.id,
                    currentYearMonth.year,
                    allEvents
                )
                this[month.id] = monthEvents
            }
        }
    }

    errorMessage?.let { message ->
        LaunchedEffect(message) {
            // Mostrar error
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 350.dp), // Tamaño mínimo fijo
            contentPadding = PaddingValues(24.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(months) { month ->
                val monthEvents = allProcessedEvents[month.id] ?: emptyMap()

                // Contenedor con tamaño fijo para los calendarios
                Box(
                    modifier = Modifier
                        .width(350.dp)
                        .wrapContentHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    TabletCalendarCard(
                        mes = month,
                        processedEvents = monthEvents,
                        onEditEvent = onEditEventClick
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TabletCalendarCard(
    mes: MountAcademicAlly,
    processedEvents: Map<Int, ProcessedPersonalEvent>,
    onEditEvent: (PersonalEvent) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme())
                Color(0xFF2D2D2D) // Color más oscuro para modo nocturno
            else
                Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = { expanded = !expanded },
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(16.dp)
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
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = mes.name,
                    modifier = Modifier.padding(4.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp),
                ) {
                    Text(
                        "Eventos",
                        style = TextStyle(
                            color = if (isSystemInDarkTheme())
                                Color.Gray.copy(alpha = 0.8f)
                            else
                                Color.Gray
                        ),
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.size(2.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Colapsar" else "Expandir",
                        modifier = Modifier.size(28.dp),
                        tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Encabezado de los días de la semana
            TabletTopCalendarView()

            Spacer(modifier = Modifier.height(8.dp))

            // Cuadrícula del calendario
            TabletCalendarArray(mes, processedEvents)

            // Información de eventos (solo si está expandido)
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                EventInformation(
                    processedEvents,
                    onEditEvent = onEditEvent
                )
            }
        }
    }
}

@Composable
fun TabletTopCalendarView() {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        for (day in DaysOfWeek.entries) {
            Text(
                text = day.name.take(3), // Mostrar 3 letras en tablet
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSystemInDarkTheme())
                    Color.Gray.copy(alpha = 0.8f)
                else
                    Color.Gray,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun TabletCalendarArray(
    mount: MountAcademicAlly,
    processedEvents: Map<Int, ProcessedPersonalEvent>
) {
    val today = LocalDate.now()
    val currentDay = today.dayOfMonth
    val currentMonth = today.monthValue

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (week in mount.weeks) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (day in week) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f), // Mantener proporción cuadrada
                        contentAlignment = Alignment.Center
                    ) {
                        if (day == 0) {
                            // Espacio vacío
                        } else {
                            val isToday = day == currentDay && mount.id == currentMonth
                            val processedEvent = processedEvents[day]

                            if (processedEvent != null) {
                                if (processedEvent.additionalEvents.isNotEmpty()) {
                                    TabletMultiEventDayView(
                                        dayNumber = day.toString(),
                                        processedEvent = processedEvent,
                                        isToday = isToday
                                    )
                                } else {
                                    TabletDayWithEvent(
                                        dayNumber = day.toString(),
                                        event = processedEvent.event,
                                        shape = processedEvent.shape,
                                        isToday = isToday
                                    )
                                }
                            } else {
                                TabletDayView(
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
}

@Composable
fun TabletDayView(
    dayNumber: String,
    isToday: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .then(
                if (isToday && dayNumber.isNotEmpty()) {
                    Modifier
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dayNumber,
            color = if (isSystemInDarkTheme()) Color.White else Color.Black,
            fontSize = 14.sp
        )
    }
}

@Composable
fun TabletDayWithEvent(
    dayNumber: String,
    event: PersonalEvent,
    shape: EventShape,
    isToday: Boolean = false
) {
    Box(
        modifier = Modifier.size(50.dp),
        contentAlignment = Alignment.Center
    ) {
        val colors = ScheduleColorsProvider.getColors()

        // Fondo del evento
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    if (colors.isNotEmpty())
                        colors[event.colorIndex % colors.size]
                    else
                        Color.Gray,
                    shape.toShape()
                )
        )

        // Contorno si es hoy
        if (isToday) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = shape.toShape()
                    )
            )
        }

        Text(
            text = dayNumber,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 14.sp
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TabletMultiEventDayView(
    dayNumber: String,
    processedEvent: ProcessedPersonalEvent,
    isToday: Boolean = false
) {
    val totalEvents = 1 + processedEvent.additionalEvents.size
    val allEvents = listOf(processedEvent.event) + processedEvent.additionalEvents
    val shape = processedEvent.shape
    val colors = ScheduleColorsProvider.getColors()

    if (colors.isEmpty()) {
        TabletDayView(dayNumber = dayNumber, isToday = isToday)
        return
    }

    Box(
        modifier = Modifier.size(56.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(48.dp)
                .clip(shape.toShape())
        ) {
            drawPieChartWithColors(allEvents, totalEvents, colors, isMultiDay = true)
        }

        if (isToday) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = shape.toShape()
                    )
            )
        }

        Text(
            text = dayNumber,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            ),
            color = Color.White
        )
    }
}