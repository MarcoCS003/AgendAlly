package com.example.academically.uiAcademicAlly.institute

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.academically.data.Event
import com.example.academically.data.EventCategory
import java.time.LocalDate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.ui.graphics.Color
import com.example.academically.R
import com.example.academically.data.EventItem
import com.example.academically.data.EventNotification

enum class EventTab {
    INSTITUTE,
    CAREER
}

@Composable
fun EventBlogScreen(
    events: List<Event>,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(EventTab.INSTITUTE) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    // Filtrar eventos según la pestaña seleccionada
    val filteredEvents = remember(selectedTab, events) {
        when (selectedTab) {
            EventTab.INSTITUTE -> events.filter { it.category == EventCategory.INSTITUTIONAL }
            EventTab.CAREER -> events.filter { it.category == EventCategory.CAREER }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tabs de Instituto y Carrera
        EventTabRow(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        // Lista de eventos
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredEvents) { event ->
                EventCardBlog(
                    event = event,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedEvent = event }
                )
            }

            // Añadir espacio al final
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Mostrar diálogo de detalle si hay un evento seleccionado
    selectedEvent?.let { event ->
        EventDetailCardBlog(
            event = event,
            onDismiss = { selectedEvent = null }
        )
    }
}

@Composable
fun EventTabRow(
    selectedTab: EventTab,
    onTabSelected: (EventTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        // Tab de Instituto
        TabButton(
            text = "Instituto",
            isSelected = selectedTab == EventTab.INSTITUTE,
            onClick = { onTabSelected(EventTab.INSTITUTE) },
            modifier = Modifier.weight(1f)
        )

        // Separador vertical
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(24.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                .align(Alignment.CenterVertically)
        )

        // Tab de Carrera
        TabButton(
            text = "Carrera",
            isSelected = selectedTab == EventTab.CAREER,
            onClick = { onTabSelected(EventTab.CAREER) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun EventBlogScreenPreview() {
    MaterialTheme {
        EventBlogScreen(
            events = listOf(
                Event(
                    id = 1,
                    title = "INNOVATECNMN 2025",
                    shortDescription = "Registro para estudiantes lider",
                    longDescription = "Cumbre nacional de desarrollo tecnológico,\n" +
                            "investigación e innovación INOVATECNM\u2028 \u2028Dirigida al estudiantado inscrito al periodo Enero-Junio 2025 personal docente y de investigación del Instituto Tecnológico de Puebla\n" +
                            "\n" +
                            "5 eventos simultáneos:\n" +
                            "Certamen de Proyectos\n" +
                            "HackaTec\n" +
                            "Cortometraje de InnvAcción\n" +
                            "Retos de Transformacionales\n" +
                            "\n" +
                            "Local : 23 de Mayo\n" +
                            "Regional : Septiembre 2025\n" +
                            "Nacional : Noviembre 2025\n" +
                            "\n" +
                            "Criterios de Evaluación\n" +
                            "Memoria Tecnica \n" +
                            "Prototipo \n" +
                            "lo que señale el modelo de operación\n" +
                            "del innovatecnm de 2025 de acuerdo a cada evento/categoría correspondiente  ",
                    location = "Edificio 53",
                    imagePath = R.drawable.inovatecnm.toString(),
                    startDate = LocalDate.of(2025, 11, 28),
                    endDate = LocalDate.of(2025, 11, 29),
                    category = EventCategory.INSTITUTIONAL,
                    color = Color(0xFF00BCD4), // Cian
                    items = listOf(
                        EventItem(
                            1,
                            Icons.Default.AttachFile,
                            "Inovatecm.2025.pdf"
                        ),
                        EventItem(2, Icons.Default.Call, "123456789")
                    ),
                    notification = EventNotification(
                        id = 1,
                        time = 86400000, // 1 día
                        title = "Recordatorio",
                        message = "Convocatoria Servicio Social mañana",
                        isEnabled = true
                    )),
                Event(
                    id = 2,
                    title = "Congreso Internacional en agua limpia y saneamiento del TECNM",
                    shortDescription = "Registro para estudiantes",
                    longDescription = "Participa en el 1er. Congreso Internacional de Agua Limpia y Saneamiento del TECNM",
                    location = "Modalidad Híbrida",
                    startDate = LocalDate.of(2025, 9, 25),
                    endDate = LocalDate.of(2025, 9, 26),
                    category = EventCategory.INSTITUTIONAL,
                    imagePath = R.drawable.congreso.toString(),
                    color = Color(0xFF2196F3)
                ),
                Event(
                    id = 3,
                    title = "Concurso de Programación 2025",
                    shortDescription = "Para estudiantes de TICS",
                    longDescription = "Invitación a los estudiantes de TICS a participar en el concurso de programación de 2025 sin costo",
                    location = "Edificio 36",
                    startDate = LocalDate.of(2025, 4, 28),
                    endDate = LocalDate.of(2025, 4, 28),
                    category = EventCategory.CAREER,
                    imagePath = R.drawable.concurso.toString(),
                    color = Color(0xFF4CAF50),
                    items = listOf(
                        EventItem(
                            1,
                            Icons.Default.AttachFile,
                            "ConcursoProgramacion.2025.pdf"
                        ),
                        EventItem(2, Icons.Default.AccessTime, "8:30-15:30")
                    ),
                ),
                Event(
                    id = 4,
                    title = "Jornadas de TICS 2025",
                    shortDescription = "Conferencias internacionales",
                    longDescription = "Participa en las jornadas de TICS del año 2025 con conferencistas internacionales, estaremos enfocados en el auge de la inteligencia artificial, ciencia de datos y las tecnologías emergentes para el desarrollo web.",
                    location = "Edificio 53",
                    startDate = LocalDate.of(2025, 9, 15),
                    endDate = LocalDate.of(2025, 9, 15),
                    category = EventCategory.CAREER,
                    color = Color(0xFF4CAF50)
                ),
                Event(
                    id = 5,
                    title = "Plática de Servicio Social",
                    shortDescription = "Información importante",
                    longDescription = "Información sobre los requisitos y proceso para realizar el servicio social",
                    startDate = LocalDate.of(2025, 5, 10),
                    endDate = LocalDate.of(2025, 5, 10),
                    category = EventCategory.CAREER,
                    color = Color(0xFFFFAB00),
                    notification = EventNotification(
                        id = 1,
                        time = 86400000,
                        title = "Recordatorio",
                        message = "Plática de servicio social mañana",
                        isEnabled = true
                    )
                )
            )
        )
    }
}