@file:Suppress("IMPLICIT_CAST_TO_ANY")

package com.example.academically.uiAcademicAlly.calendar

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academically.ViewModel.EventViewModel
import com.example.academically.data.PersonalEvent
import com.example.academically.data.PersonalEventType
import com.example.academically.data.database.AcademicAllyDatabase
import com.example.academically.data.mappers.getIconByName
import com.example.academically.data.repository.PersonalEventRepository
import com.example.academically.ui.theme.ScheduleColorsProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Tarjeta de detalles del evento que se muestra al seleccionar un evento
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventDetailCardWithViewModel(
    event: PersonalEvent,
    onDismiss: () -> Unit = {},
    onEditEvent: (PersonalEvent) -> Unit = {},

    ) {
    // Inicializar ViewModel
    val context = LocalContext.current
    val database = AcademicAllyDatabase.getDatabase(context)
    val repository = PersonalEventRepository(database.personalEventDao())
    val eventViewModel: EventViewModel = viewModel(
        factory = EventViewModel.Factory(repository)
    )

    // Estado para confirmación de eliminación
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Estado de carga
    val isLoading by eventViewModel.isLoading.collectAsState()

    // Estado de error
    val errorMessage by eventViewModel.errorMessage.collectAsState()

    // Mostrar SnackBar para errores
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            // Aquí puedes mostrar un SnackBar con el mensaje de error
        }
    }

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
                            icon = getIconByName(item.iconName),
                            text = item.text
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Botón de eliminar
                    IconButton(
                        onClick = { showDeleteConfirmation = true },
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar evento",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }

                    // Botón de editar (solo para eventos personales)
                    if (event.type == PersonalEventType.PERSONAL) { // 3 = PERSONAL
                        IconButton(
                            onClick = {
                                println("DEBUG: Botón de editar presionado para evento: ${event.id}")
                                onEditEvent(event) // Verificamos que se llama con el evento correcto
                                onDismiss()
                            },
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar evento"
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Eliminar evento") },
            text = { Text("¿Estás seguro que deseas eliminar este evento?") },
            confirmButton = {
                Button(
                    onClick = {
                        // Eliminar el evento
                        eventViewModel.deleteEvent(event)
                        showDeleteConfirmation = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onError,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Eliminar")
                    }
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteConfirmation = false },
                    enabled = !isLoading
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Encabezado de la tarjeta de evento con color, categoría y título
 */
@Composable
fun EventHeader(event: PersonalEvent) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Círculo con el color del evento
        val colors = ScheduleColorsProvider.getColors()
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    if (colors.isNotEmpty())
                        colors[event.colorIndex % colors.size]
                    else
                        Color.Gray
                )
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Categoría y título
        Column {
            Text(
                text = "${event.type}: ${event.title}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
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
    text: String,
    isClickable: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val itemModifier = if (isClickable && onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    Row(
        modifier = itemModifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isClickable) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

/**
 * Formatea las fechas del evento para mostrarlas en la tarjeta
 */
@SuppressLint("NewApi")
fun formatEventDate(startDate: LocalDate?, endDate: LocalDate?): String {
    if (startDate == null) return ""

    val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "ES"))

    return if (endDate != null && !startDate.isEqual(endDate)) {
        if (startDate.month != endDate.month) {
            // Si son meses diferentes, mostrar el mes en ambas fechas
            "${startDate.format(formatter)} - ${endDate.format(formatter)}"
        } else {
            // Si es el mismo mes, solo mostrar el día de inicio y fin con el mes una sola vez
            "${startDate.dayOfMonth} - ${endDate.dayOfMonth} de ${
                startDate.month.getDisplayName(
                    java.time.format.TextStyle.FULL,
                    java.util.Locale("es", "ES")
                )
            }"
        }
    } else {
        startDate.format(formatter)
    }
}