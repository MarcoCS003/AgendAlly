
package com.example.academically.uiAcademicAlly.institute

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import com.example.academically.ViewModel.BlogEventsViewModel
import com.example.academically.ViewModel.EventViewModel
import com.example.academically.data.api.EventInstituteBlog
import kotlinx.coroutines.launch

enum class EventTabAPI {
    ALL,
    INSTITUTE,
    CAREER
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventBlogScreenWithAPI(
    blogEventsViewModel: BlogEventsViewModel,
    eventViewModel: EventViewModel? = null,
    modifier: Modifier = Modifier
) {
    // Estados del ViewModel
    val events by blogEventsViewModel.events.collectAsStateWithLifecycle()
    val isLoading by blogEventsViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by blogEventsViewModel.errorMessage.collectAsStateWithLifecycle()

    // Estados locales
    var selectedTab by remember { mutableStateOf(EventTabAPI.ALL) }
    var selectedEvent by remember { mutableStateOf<EventInstituteBlog?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }

    // Filtrar eventos según la pestaña seleccionada
    val filteredEvents = remember(selectedTab, events) {
        when (selectedTab) {
            EventTabAPI.ALL -> events
            EventTabAPI.INSTITUTE -> events.filter { it.category == "INSTITUTIONAL" }
            EventTabAPI.CAREER -> events.filter { it.category == "CAREER" }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Barra de búsqueda (opcional)
        if (showSearchBar) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (it.length >= 2 || it.isEmpty()) {
                        blogEventsViewModel.searchEvents(it)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar eventos...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                trailingIcon = {
                    IconButton(onClick = {
                        showSearchBar = false
                        searchQuery = ""
                        blogEventsViewModel.loadAllEvents()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Limpiar")
                    }
                }
            )
        }

        // Toolbar con botones de acción
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Eventos Académicos",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Row {
                IconButton(onClick = { showSearchBar = !showSearchBar }) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                }
                IconButton(onClick = { blogEventsViewModel.refreshEvents() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                }
            }
        }

        // Tabs de filtrado
        EventTabRowAPI(
            selectedTab = selectedTab,
            onTabSelected = {
                selectedTab = it
                blogEventsViewModel.filterByCategory(it.name)
            }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            // Contenido principal
            when {
                isLoading && events.isEmpty() -> {
                    // Loading inicial
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Cargando eventos...")
                        }
                    }
                }

                errorMessage != null && events.isEmpty() -> {
                    // Error state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Error: $errorMessage",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = {
                                blogEventsViewModel.clearError()
                                blogEventsViewModel.loadAllEvents()
                            }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                filteredEvents.isEmpty() -> {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay eventos disponibles",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    // Lista de eventos
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(filteredEvents) { event ->
                            EventCardBlogAPI(
                                event = event,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedEvent = event }
                            )
                        }
                    }
                }
            }

            // Loading overlay para refresh
            if (isLoading && events.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        }
    }

    // Mostrar diálogo de detalle si hay un evento seleccionado
    selectedEvent?.let { event ->
        EventDetailCardBlogAPI(
            event = event,
            onDismiss = { selectedEvent = null },
            eventViewModel = eventViewModel
        )
    }

    // Mostrar snackbar de error si hay mensaje
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            // Aquí podrías mostrar un Snackbar
            // Por ahora solo limpiamos el error después de un tiempo
            kotlinx.coroutines.delay(3000)
            blogEventsViewModel.clearError()
        }
    }
}

@Composable
fun EventTabRowAPI(
    selectedTab: EventTabAPI,
    onTabSelected: (EventTabAPI) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        // Tab Todos
        TabButtonAPI(
            text = "Todos",
            isSelected = selectedTab == EventTabAPI.ALL,
            onClick = { onTabSelected(EventTabAPI.ALL) },
            modifier = Modifier.weight(1f)
        )

        // Separador
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(24.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                .align(Alignment.CenterVertically)
        )

        // Tab Instituto
        TabButtonAPI(
            text = "Instituto",
            isSelected = selectedTab == EventTabAPI.INSTITUTE,
            onClick = { onTabSelected(EventTabAPI.INSTITUTE) },
            modifier = Modifier.weight(1f)
        )

        // Separador
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(24.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                .align(Alignment.CenterVertically)
        )

        // Tab Carrera
        TabButtonAPI(
            text = "Carrera",
            isSelected = selectedTab == EventTabAPI.CAREER,
            onClick = { onTabSelected(EventTabAPI.CAREER) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TabButtonAPI(
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
@Composable
fun EventCardBlogAPI(
    event: EventInstituteBlog,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Título del evento
            Text(
                text = event.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Separador
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Información básica
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Descripción corta
                Text(
                    text = event.shortDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )

                // Categoría
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (event.category) {
                            "INSTITUTIONAL" -> MaterialTheme.colorScheme.primaryContainer
                            "CAREER" -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.tertiaryContainer
                        }
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = when (event.category) {
                            "INSTITUTIONAL" -> "Instituto"
                            "CAREER" -> "Carrera"
                            else -> "Personal"
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            // Fecha y ubicación si existen
            if (event.startDate != null || event.location.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                if (event.startDate != null) {
                    Text(
                        text = "📅 ${event.startDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (event.location.isNotEmpty()) {
                    Text(
                        text = "📍 ${event.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Descripción larga truncada
            if (event.longDescription.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                val truncatedDescription = if (event.longDescription.length > 120) {
                    event.longDescription.substring(0, 120) + "..."
                } else {
                    event.longDescription
                }

                Text(
                    text = truncatedDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 3
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventDetailCardBlogAPI(
    event: EventInstituteBlog,
    onDismiss: () -> Unit = {},
    eventViewModel: EventViewModel? = null
) {
    var showSuccessMessage by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 600.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Título
                Text(
                    text = event.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Información del evento
                if (event.longDescription.isNotEmpty()) {
                    Text(
                        text = event.longDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Fecha
                if (event.startDate != null) {
                    Text(
                        text = "📅 Fecha: ${event.startDate}${if (event.endDate != null && event.endDate != event.startDate) " - ${event.endDate}" else ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Ubicación
                if (event.location.isNotEmpty()) {
                    Text(
                        text = "📍 Ubicación: ${event.location}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Categoría
                Text(
                    text = "🏷️ Categoría: ${when (event.category) {
                        "INSTITUTIONAL" -> "Instituto"
                        "CAREER" -> "Carrera"
                        else -> "Personal"
                    }}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

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
                                // Convertir EventInstituteBlog a Event local
                                val calendarEvent = convertAPIEventToLocal(event)
                                viewModel.insertEvent(calendarEvent)
                                showSuccessMessage = true

                                // Ocultar mensaje y cerrar después de 2 segundos
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

// Función para convertir EventInstituteBlog a Event local
@RequiresApi(Build.VERSION_CODES.O)
private fun convertAPIEventToLocal(apiEvent: EventInstituteBlog): com.example.academically.data.Event {
    return com.example.academically.data.Event(
        id = 0, // Room asignará el ID
        title = apiEvent.title,
        shortDescription = apiEvent.shortDescription,
        longDescription = apiEvent.longDescription,
        location = apiEvent.location,
        colorIndex = when (apiEvent.category) {
            "INSTITUTIONAL" -> 0
            "CAREER" -> 2
            else -> 1
        },
        startDate = apiEvent.startDate?.let { java.time.LocalDate.parse(it) } ?: java.time.LocalDate.now(),
        endDate = apiEvent.endDate?.let { java.time.LocalDate.parse(it) } ?: java.time.LocalDate.now(),
        category = when (apiEvent.category) {
            "INSTITUTIONAL" -> com.example.academically.data.EventCategory.INSTITUTIONAL
            "CAREER" -> com.example.academically.data.EventCategory.CAREER
            else -> com.example.academically.data.EventCategory.PERSONAL
        },
        shape = com.example.academically.data.EventShape.RoundedFull
    )
}