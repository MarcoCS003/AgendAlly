
package com.example.academically.uiAcademicAlly.institute

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
// ✅ CORRECCIÓN: Usar los modelos correctos
import com.example.academically.data.api.*
import com.example.academically.data.api.Organization
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventBlogScreenWithAPI(
    blogEventsViewModel: BlogEventsViewModel,
    eventViewModel: EventViewModel? = null,
    modifier: Modifier = Modifier
) {
    // Estados del ViewModel
    val events by blogEventsViewModel.events.collectAsStateWithLifecycle()
    val channels by blogEventsViewModel.channels.collectAsStateWithLifecycle()
    val organizations by blogEventsViewModel.organizations.collectAsStateWithLifecycle()
    val isLoading by blogEventsViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by blogEventsViewModel.errorMessage.collectAsStateWithLifecycle()
    val selectedChannelId by blogEventsViewModel.selectedChannelId.collectAsStateWithLifecycle()

    // Estados locales
    var selectedEvent by remember { mutableStateOf<EventInstituteBlog?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }

    // Filtrar eventos según el canal seleccionado
    val filteredEvents = remember(selectedChannelId, events) {
        when (selectedChannelId) {
            null -> events // Mostrar todos los eventos
            else -> events.filter { it.channelId == selectedChannelId }
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
                        blogEventsViewModel.clearFilters()
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpiar")
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
            Column {
                Text(
                    text = "Eventos Académicos",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Mostrar canal activo
                selectedChannelId?.let { channelId ->
                    val channel = blogEventsViewModel.getChannelById(channelId)
                    val organization = channel?.let {
                        blogEventsViewModel.getOrganizationById(it.organizationId)
                    }

                    if (channel != null) {
                        Text(
                            text = "${channel.acronym} - ${organization?.acronym ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Row {
                IconButton(onClick = { showSearchBar = !showSearchBar }) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                }
                IconButton(onClick = { blogEventsViewModel.refreshData() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                }
            }
        }

        // Filtros por canales (tabs horizontales)
        ChannelFilterTabs(
            channels = channels,
            organizations = organizations,
            selectedChannelId = selectedChannelId,
            onChannelSelected = { channelId ->
                if (channelId != null) {
                    blogEventsViewModel.loadEventsByChannel(channelId)
                } else {
                    blogEventsViewModel.clearFilters()
                }
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.EventBusy,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (selectedChannelId != null) {
                                    "No hay eventos en este canal"
                                } else {
                                    "No hay eventos disponibles"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { blogEventsViewModel.clearFilters() }) {
                                Text("Ver todos los eventos")
                            }
                        }
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
                                channels = channels,
                                organizations = organizations,
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
            channels = channels,
            organizations = organizations,
            onDismiss = { selectedEvent = null },
            eventViewModel = eventViewModel
        )
    }

    // Mostrar snackbar de error si hay mensaje
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            delay(5000)
            blogEventsViewModel.clearError()
        }
    }
}

@Composable
fun ChannelFilterTabs(
    channels: List<Channel>,
    organizations: List<com.example.academically.data.api.Organization>,
    selectedChannelId: Int?,
    onChannelSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Canales",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                // Opción "Todos"
                item {
                    FilterChip(
                        onClick = { onChannelSelected(null) },
                        label = { Text("Todos") },
                        selected = selectedChannelId == null,
                        leadingIcon = {
                            Icon(
                                Icons.Default.Apps,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }

                // Canales agrupados por organización
                items(channels.sortedBy { it.acronym }) { channel ->
                    val organization = organizations.find { it.organizationID == channel.organizationId }

                    FilterChip(
                        onClick = { onChannelSelected(channel.id) },
                        label = {
                            Column {
                                Text(
                                    text = channel.acronym,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                if (organization != null) {
                                    Text(
                                        text = organization.acronym,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        selected = selectedChannelId == channel.id,
                        leadingIcon = {
                            Icon(
                                when (channel.type) {
                                    ChannelType.CAREER -> Icons.Default.School
                                    ChannelType.DEPARTMENT -> Icons.Default.Business
                                    ChannelType.ADMINISTRATIVE -> Icons.Default.AccountBox
                                },
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventCardBlogAPI(
    event: EventInstituteBlog,
    channels: List<Channel>,
    organizations: List<Organization>,
    modifier: Modifier = Modifier
) {
    // Obtener información del canal y organización
    val channel = event.channelId?.let { channelId ->
        channels.find { it.id == channelId }
    }
    val organization = channel?.let { ch ->
        organizations.find { it.organizationID == ch.organizationId }
    }

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
            // Encabezado con información del canal
            if (channel != null && organization != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            when (channel.type) {
                                ChannelType.CAREER -> Icons.Default.School
                                ChannelType.DEPARTMENT -> Icons.Default.Business
                                ChannelType.ADMINISTRATIVE -> Icons.Default.AccountBox
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${channel.acronym}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = when (channel.type) {
                                ChannelType.CAREER -> MaterialTheme.colorScheme.primaryContainer
                                ChannelType.DEPARTMENT -> MaterialTheme.colorScheme.secondaryContainer
                                ChannelType.ADMINISTRATIVE -> MaterialTheme.colorScheme.tertiaryContainer
                            }
                        ),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = when (channel.type) {
                                ChannelType.CAREER -> "Carrera"
                                ChannelType.DEPARTMENT -> "Depto"
                                ChannelType.ADMINISTRATIVE -> "Admin"
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Título del evento
            Text(
                text = event.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Descripción corta
            if (event.shortDescription.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.shortDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Fecha y ubicación si existen
            if (event.startDate != null || event.location.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                if (event.startDate != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.startDate!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (event.location.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Descripción larga truncada
            if (event.longDescription.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                val truncatedDescription = if (event.longDescription.length > 100) {
                    event.longDescription.substring(0, 100) + "..."
                } else {
                    event.longDescription
                }

                Text(
                    text = truncatedDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventDetailCardBlogAPI(
    event: EventInstituteBlog,
    channels: List<Channel>,
    organizations: List<com.example.academically.data.api.Organization>,
    onDismiss: () -> Unit = {},
    eventViewModel: EventViewModel? = null
) {
    var showSuccessMessage by remember { mutableStateOf(false) }

    // Obtener información del canal y organización
    val channel = event.channelId?.let { channelId ->
        channels.find { it.id == channelId }
    }
    val organization = channel?.let { ch ->
        organizations.find { it.organizationID == ch.organizationId }
    }

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
                // Información del canal
                if (channel != null && organization != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(
                            when (channel.type) {
                                ChannelType.CAREER -> Icons.Default.School
                                ChannelType.DEPARTMENT -> Icons.Default.Business
                                ChannelType.ADMINISTRATIVE -> Icons.Default.AccountBox
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = channel.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = organization.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Título
                Text(
                    text = event.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Fecha: ${event.startDate}${if (event.endDate != null && event.endDate != event.startDate) " - ${event.endDate}" else ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }

                // Ubicación
                if (event.location.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ubicación: ${event.location}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }

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
                    TextButton(onClick = onDismiss) {
                        Text("Cerrar")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            eventViewModel?.let { viewModel ->
                                // Convertir EventInstituteBlog a Event local
                                val calendarEvent = convertAPIEventToLocal(event)
                                viewModel.insertEvent(calendarEvent)
                                showSuccessMessage = true

                                // Ocultar mensaje y cerrar después de 2 segundos
                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                    delay(2000)
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