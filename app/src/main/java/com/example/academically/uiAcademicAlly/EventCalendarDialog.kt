package com.example.academically.uiAcademicAlly

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.academically.R
import com.example.academically.data.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Tarjeta de detalles del evento que se muestra al seleccionar un evento
 */
@Composable
fun EventDetailCard(
    event: Event,
    onDismiss: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(

                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                // Encabezado con categoría y título
                EventHeader(event)

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Descripción larga si existe
                if (event.longDescription.isNotEmpty()) {
                    Text(
                        text = event.longDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Imagen del Evento
                if (event.imagePath.isNotEmpty()) {
                    Image(
                        painter = painterResource(id = event.imagePath.toInt()),
                        contentDescription = "Imagen del evento",
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp)
                    )
                    Spacer(Modifier.padding(5.dp))
                }

                // Información de fecha
                EventInfoItem(
                    icon = Icons.Default.DateRange,
                    text = formatEventDate(event.startDate, event.endDate)
                )

                // Ubicación si existe
                if (event.location.isNotEmpty()) {
                    EventInfoItem(
                        icon = Icons.Default.LocationOn,
                        text = event.location
                    )
                }

                // Espacio para elementos adicionales
                if (event.items.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Mostrar cada item del evento
                    event.items.forEach { item ->
                        EventInfoItem(
                            icon = item.icon,
                            text = item.text
                        )
                    }
                }

                // Botón de notificación si existe
                if (event.notification != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    NotificationButton(event.notification)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Botón de eliminar
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar evento",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }

                    // Botón de ocultar
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.RemoveRedEye,
                            contentDescription = "Ocultar Evento",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}


/**
 * Encabezado de la tarjeta de evento con color, categoría y título
 */
@Composable
fun EventHeader(event: Event) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Círculo con el color de la categoría
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(event.color)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Categoría y título
        Column {
            Text(
                text = "${event.category.name}: ${event.title}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = event.color
            )
        }
    }
}

/**
 * Item de información del evento (icono + texto)
 */
@Composable
fun EventInfoItem(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Botón de notificación para el evento
 */
@Composable
fun NotificationButton(notification: EventNotification) {
    OutlinedButton(
        onClick = { /* Implementar configuración de notificación */ },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = if (notification.isEnabled) "Avisarme 1 día antes..." else "Personalizado",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Formatea las fechas del evento para mostrarlas en la tarjeta
 */
@SuppressLint("NewApi")
fun formatEventDate(startDate: LocalDate?, endDate: LocalDate?): String {
    if (startDate == null) return ""

    val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM")

    return if (endDate != null && !startDate.isEqual(endDate)) {
        "${startDate.dayOfMonth} - ${endDate.dayOfMonth} ${startDate.month.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale("es", "ES"))}"
    } else {
        startDate.format(formatter)
    }
}

@SuppressLint("NewApi")
@Preview(showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun EventDetailCardPreview() {
    Surface(color = Color(0xFFF5F5F5)) {
        // Evento de carrera
        val event1 = Event(
            id = 1,
            title = "Convocatoria Servicio Social",
            shortDescription = "Registro para servicio",
            longDescription = "Estimado estudiante de TICs si le interesa realizar su servicio social durante el periodo Diciembre 2024 - Junio 2025 guardar esta información Coordinación Instruccional de tutorías Desarrollo Académico.",
            location = "Edificio 6",
            imagePath = R.drawable.seminario.toString(),
            startDate = LocalDate.of(2025, 11, 28),
            endDate = LocalDate.of(2025, 11, 29),
            category = EventCategory.CAREER,
            color = Color(0xFF00BCD4), // Cian
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

        EventDetailCard(event = event1)
    }
}

@SuppressLint("NewApi")
@Preview(showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun EventDetailCardPersonalPreview() {
    Surface(color = Color(0xFFF5F5F5)) {
        // Evento personal
        val event2 = Event(
            id = 2,
            title = "Sesión de Estudio para Examen Final",
            shortDescription = "Preparación examen",
            longDescription = "Tengo examen final de Programación y quiero repasar bien los temas. Voy a hacer ejercicios, revisar apuntes y usar tarjetas de memoria para recordar mejor. También quiero resolver dudas y practicar con preguntas tipo examen.",
            location = "Biblioteca Central",
            startDate = LocalDate.of(2025, 6, 10),
            endDate = LocalDate.of(2025, 6, 10),
            category = EventCategory.PERSONAL,
            color = Color(0xFFE91E63), // Rosa
            notification = EventNotification(
                id = 2,
                time = 3600000, // 1 hora
                title = "Recordatorio",
                message = "Sesión de estudio en 1 hora",
                isEnabled = false
            )
        )

        EventDetailCard(event = event2)
    }
}


@SuppressLint("NewApi")
@Preview(showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun EventDetailCardInstitutionalPreview() {
    Surface(color = Color(0xFFF5F5F5)) {
        // Evento institucional
        val event3 = Event(
            id = 3,
            title = "Conferencia de Inteligencia Artificial",
            shortDescription = "Evento académico",
            longDescription = "",
            startDate = LocalDate.of(2025, 4, 15),
            endDate = LocalDate.of(2025, 4, 15),
            category = EventCategory.INSTITUTIONAL,
            color = Color(0xFF2196F3), // Azul

        )

        EventDetailCard(event = event3)
    }
}

