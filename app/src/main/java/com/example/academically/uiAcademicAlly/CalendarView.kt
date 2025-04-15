package com.example.academically.uiAcademicAlly

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.academically.data.Calanderio
import com.example.academically.data.Event
import com.example.academically.data.EventProcessor
import com.example.academically.data.EventShape
import com.example.academically.data.Eventos
import com.example.academically.data.Mount
import com.example.academically.data.ProcessedEvent

enum class DaysOfWeek {
    DOMINGO, LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO;
}

@Composable
fun CalendarAppScreen(mounts: List<Mount> = Calanderio.mounts, onClickAdd: () -> Unit = {}) {
    Box {
        // Usamos LazyColumn en lugar de Column+ScrollState para mejor rendimiento
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            items(mounts) { month ->
                CalendarCard(mes = month)
            }
        }

        // Botones flotantes
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxSize()
        ) {
            FloatingActionButton(
                onClick = onClickAdd,
                modifier = Modifier
                    .size(85.dp)
                    .padding(end = 15.dp, bottom = 15.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar evento")
            }

            FloatingActionButton(
                onClick = onClickAdd,
                modifier = Modifier
                    .size(85.dp)
                    .padding(end = 15.dp, bottom = 15.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
        }
    }
}

@Composable
fun CalendarCard(mes: Mount) {
    // Preprocesamos los eventos para este mes para mejorar el rendimiento
    val processedEvents = remember(mes.id) {
        EventProcessor.processEventsForMonth(
            mes,
            Eventos.listEvents,
            Eventos.listEventsAlone
        )
    }

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .width(325.dp)
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(4.dp)
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
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
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
                EventInformation(mes)
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
    mount: Mount,
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
                            // Renderizamos el día con el evento
                            DayWithEvent(
                                dayNumber = day.toString(),
                                event = processedEvent.event,
                                shape = processedEvent.shape
                            )
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
    shape: EventShape
) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(event.color, shape.toShape()),
        contentAlignment = Alignment.Center
    ) {
        // Usamos color blanco para mejor contraste con fondos de colores
        Text(
            text = dayNumber,
            style = TextStyle(color = Color.White)
        )
    }
}

@Composable
fun EventInformation(mount: Mount) {
    // Agrupamos todos los eventos de este mes para mostrarlos
    val eventsForMonth = remember(mount.id) {
        val events = mutableListOf<Event>()

        // Agregamos eventos de un solo día
        Eventos.listEventsAlone
            .filter { it.mesID == mount.id }
            .forEach { events.add(it) }

        // Agregamos eventos de múltiples días
        Eventos.listEvents
            .filter { it.mesID == mount.id }
            .forEach { events.add(it) }

        events
    }

    Column(Modifier.padding(8.dp)) {
        if (eventsForMonth.isEmpty()) {
            Text(
                text = "No hay eventos para este mes",
                modifier = Modifier.padding(4.dp),
                style = TextStyle(color = Color.Gray)
            )
        } else {
            for (event in eventsForMonth) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)
                ) {
                    // Indicador de color para el evento
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(event.color, RoundedCornerShape(4.dp))
                    )

                    // Descripción del evento
                    Text(
                        text = event.description,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewComsable(){
    CalendarAppScreen()
}