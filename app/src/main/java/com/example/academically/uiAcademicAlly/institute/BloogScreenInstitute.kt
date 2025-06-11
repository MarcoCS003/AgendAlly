package com.example.academically.uiAcademicAlly.institute

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ImageNotSupported // NUEVO: Para mostrar cuando no hay imagen
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.example.academically.R
import com.example.academically.ViewModel.EventViewModel
import com.example.academically.data.Event
import com.example.academically.data.EventCategory
import com.example.academically.data.EventInstitute
import com.example.academically.data.EventItem
import com.example.academically.data.EventNotification
import com.example.academically.data.api.EventItemType
import com.example.academically.uiAcademicAlly.calendar.EventInfoItem
import com.example.academically.uiAcademicAlly.calendar.formatEventDate
import io.ktor.client.plugins.logging.Logger
import kotlinx.coroutines.launch
import java.time.LocalDate

// ===== FUNCIÓN AUXILIAR PARA MANEJAR IMÁGENES =====
@Composable
private fun EventImage(
    imagePath: String,
    modifier: Modifier = Modifier
) {
    Log.d("EventImage", "Original imagePath: '$imagePath'")
    when {
        // Si imagePath está vacío, no mostrar imagen
        imagePath.isEmpty() -> {
            // No renderizar nada
        }
        imagePath.startsWith("http") -> {

            SubcomposeAsyncImage(
                model = imagePath,

                contentDescription = "Imagen del evento",
                modifier = modifier,
                loading = {
                    // Mostrar loading mientras carga
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                },
                error = {
                    // Mostrar error si falla la carga
                    Icon(
                        imageVector = Icons.Default.ImageNotSupported,
                        contentDescription = "Error cargando imagen",
                        modifier = Modifier.fillMaxSize(),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            )
        }


        // Si imagePath es otra cosa (path relativo), construir URL completa
        else -> {
            val fullUrl = if (imagePath.startsWith("/")) {
                "http://localhost:8080$imagePath"
            } else {
                "http://localhost:8080/images/$imagePath"
            }

            SubcomposeAsyncImage(
                model = fullUrl,
                contentDescription = "Imagen del evento",
                modifier = modifier,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                },
                error = {
                    Icon(
                        imageVector = Icons.Default.ImageNotSupported,
                        contentDescription = "Error cargando imagen",
                        modifier = Modifier.fillMaxSize(),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            )
        }
    }
}

@Composable
fun EventCardBlog(
    event: EventInstitute,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme())
                MaterialTheme.colorScheme.surface
            else
                Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = event.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // ===== IMAGEN CORREGIDA =====
                if (event.imagePath.isNotEmpty()) {
                    EventImage(
                        imagePath = event.imagePath,
                        modifier = Modifier
                            .size(150.dp)
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                    )
                }

                Column(
                    modifier = Modifier.height(150.dp),
                    verticalArrangement = Arrangement.Center
                ) {
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
                }
            }

            // Descripción larga
            event.longDescription.let { longDescription ->
                val truncatedDescription = if (event.longDescription.length > 150) {
                    longDescription.substring(0, 150) + "..."
                } else {
                    longDescription
                }

                Text(
                    text = truncatedDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
@SuppressLint("NewApi")
@Composable
fun EventDetailCardBlog(
    event: EventInstitute,
    onDismiss: () -> Unit = {},
    eventViewModel: EventViewModel? = null
) {
    val scrollState = rememberScrollState()
    var showSuccessMessage by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                Text(text = event.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // ===== IMAGEN =====
                if (event.imagePath.isNotEmpty()) {
                    EventImage(
                        imagePath = event.imagePath,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(vertical = 4.dp
                            )
                    )
                    Spacer(Modifier.padding(5.dp))
                }

                // Descripción larga si existe
                if (event.longDescription.isNotEmpty()) {
                    Text(
                        text = event.longDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Información de fecha
                EventInfoItemClickable(
                    icon = Icons.Default.DateRange,
                    text = "Fecha: ${formatEventDate(event.startDate, event.endDate)}",
                    isClickable = false
                )

                // Ubicación si existe
                if (event.location.isNotEmpty()) {
                    EventInfoItemClickable(
                        icon = Icons.Default.LocationOn,
                        text = "Ubicación: ${event.location}",
                        isClickable = false
                    )
                }

                // ===== ITEMS DEL EVENTO CON FUNCIONALIDAD CLICKEABLE =====
                if (event.items.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Información adicional",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Mostrar cada item del evento
                    event.items.forEach { item ->
                        EventInfoItemClickable(
                            icon = item.icon,
                            text = "${item.text}: ${item.value}",
                            isClickable = item.isClickable,
                            onClick = if (item.isClickable) {
                                { handleItemClick(context, item) }
                            } else null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mensaje de éxito
                if (showSuccessMessage) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = "✓ Evento añadido al calendario",
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            eventViewModel?.let { viewModel ->
                                val calendarEvent = convertToCalendarEvent(event)
                                viewModel.insertEvent(calendarEvent)
                                showSuccessMessage = true

                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                    kotlinx.coroutines.delay(2000)
                                    showSuccessMessage = false
                                    onDismiss()
                                }
                            }
                        },
                        enabled = eventViewModel != null
                    ) {
                        Text("Añadir a Calendario")
                    }
                }
            }
        }
    }
}

// ===== COMPONENTE MEJORADO PARA ITEMS CLICKEABLES =====
@Composable
fun EventInfoItemClickable(
    icon: ImageVector,
    text: String,
    isClickable: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val itemModifier = if (isClickable && onClick != null) {
        modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)
    } else {
        modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    }

    Row(
        modifier = itemModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (isClickable) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isClickable) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.weight(1f)
        )

        // Mostrar icono de flecha si es clickeable
        if (isClickable) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Clickeable",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
        }
    }
}

// ===== FUNCIÓN PARA MANEJAR CLICKS EN ITEMS =====
private fun handleItemClick(context: Context, item: EventItem) {
    try {
        when (item.type) {
            EventItemType.EMAIL -> {
                // Abrir app de email
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:${item.value}")
                }
                context.startActivity(emailIntent)
            }

            EventItemType.PHONE -> {
                // Abrir app de teléfono
                val phoneIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${item.value}")
                }
                context.startActivity(phoneIntent)
            }

            EventItemType.WHATSAPP -> {
                // Abrir WhatsApp
                val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(item.value) // Ya debe ser una URL de WhatsApp
                }
                context.startActivity(whatsappIntent)
            }

            EventItemType.WEBSITE,
            EventItemType.REGISTRATION_LINK,
            EventItemType.LIVE_STREAM,
            EventItemType.RECORDING,
            EventItemType.FACEBOOK,
            EventItemType.INSTAGRAM,
            EventItemType.TWITTER,
            EventItemType.YOUTUBE,
            EventItemType.LINKEDIN -> {
                // Abrir navegador
                val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(item.value)
                }
                context.startActivity(browserIntent)
            }

            EventItemType.MAPS_LINK -> {
                // Abrir Google Maps
                val mapsIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(item.value)
                }
                context.startActivity(mapsIntent)
            }

            EventItemType.ATTACHMENT -> {
                // Descargar/abrir archivo
                val fileIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(item.value)
                }
                context.startActivity(fileIntent)
            }

            else -> {
                // Para otros tipos, no hacer nada o mostrar mensaje
                println("Tipo de item no clickeable: ${item.type}")
            }
        }
    } catch (e: Exception) {
        // Manejar errores (por ejemplo, si no hay app instalada)
        println("Error al abrir ${item.type}: ${e.message}")

        // Opcionalmente, puedes mostrar un Toast o Snackbar
        // Toast.makeText(context, "No se pudo abrir ${item.text}", Toast.LENGTH_SHORT).show()
    }
}
// Función para convertir EventInstitute a Event
@SuppressLint("NewApi")
private fun convertToCalendarEvent(eventInstitute: EventInstitute): Event {
    return Event(
        id = 0, // Room asignará el ID
        title = eventInstitute.title,
        shortDescription = eventInstitute.shortDescription,
        longDescription = eventInstitute.longDescription,
        location = eventInstitute.location,
        colorIndex = getColorIndex(eventInstitute.color), // Convertir color a índice
        startDate = eventInstitute.startDate ?: LocalDate.now(),
        endDate = eventInstitute.endDate ?: eventInstitute.startDate ?: LocalDate.now(),
        category = eventInstitute.category,
        shape = eventInstitute.shape
    )
}

// Función auxiliar para convertir Color a índice
private fun getColorIndex(color: Color): Int {
    return when (color.toArgb()) {
        0xFF00BCD4.toInt() -> 0 // Cian
        0xFF2196F3.toInt() -> 1 // Azul
        0xFF4CAF50.toInt() -> 2 // Verde
        0xFFFFAB00.toInt() -> 3 // Naranja
        0xFFE91E63.toInt() -> 4 // Rosa
        else -> 0 // Por defecto
    }
}

@SuppressLint("NewApi")
@Preview(showBackground = true)
@Composable
fun CardEventBlogPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ===== EJEMPLO CON IMAGEN COMO RECURSO =====
            EventDetailCardBlog(
                event = EventInstitute(
                    id = 1,
                    title = "INNOVATECNMN 2025",
                    shortDescription = "Registro para estudiantes lider",
                    longDescription = "Cumbre nacional de desarrollo tecnológico...",
                    location = "Edificio 53",
                    imagePath = R.drawable.seminario.toString(), // Esto funcionará
                    startDate = LocalDate.of(2025, 11, 28),
                    endDate = LocalDate.of(2025, 11, 29),
                    category = EventCategory.CAREER,
                    color = Color(0xFF00BCD4),
                    notification = EventNotification(
                        id = 1,
                        time = 86400000,
                        title = "Recordatorio",
                        message = "Convocatoria Servicio Social mañana",
                        isEnabled = true
                    )
                )
            )

            // ===== EJEMPLO SIN IMAGEN =====
            EventCardBlog(
                EventInstitute(
                    id = 2,
                    title = "Evento sin imagen",
                    shortDescription = "Evento de prueba",
                    longDescription = "Este evento no tiene imagen y debería funcionar correctamente",
                    location = "Laboratorio 1",
                    imagePath = "", // Sin imagen
                    startDate = LocalDate.of(2025, 6, 10),
                    endDate = LocalDate.of(2025, 6, 10),
                    category = EventCategory.PERSONAL,
                    color = Color(0xFFE91E63),
                    notification = EventNotification(
                        id = 2,
                        time = 3600000,
                        title = "Recordatorio",
                        message = "Evento en 1 hora",
                        isEnabled = false
                    )
                ),
                modifier = Modifier
            )
        }
    }
}