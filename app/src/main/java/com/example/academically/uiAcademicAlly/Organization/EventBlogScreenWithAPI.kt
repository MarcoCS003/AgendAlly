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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.academically.ViewModel.BlogEventsViewModel
import com.example.academically.ViewModel.EventViewModel
import com.example.academically.ViewModel.OrganizationViewModel
import com.example.academically.data.Event
import com.example.academically.data.EventCategory
import com.example.academically.data.EventShape
import com.example.academically.data.api.*
import com.example.academically.data.database.AcademicAllyDatabase
import com.example.academically.data.repositorty.OrganizationRepository
import com.example.academically.uiAcademicAlly.Organization.ChannelSubscriptionDialog
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventBlogScreenWithAPI(
    blogEventsViewModel: BlogEventsViewModel,
    eventViewModel: EventViewModel? = null,
    organizationId: Int? = null, // ✅ NUEVO: ID de organización específica
    modifier: Modifier = Modifier
) {
    // Setup para OrganizationViewModel
    val context = LocalContext.current
    val database = AcademicAllyDatabase.getDatabase(context)
    val organizationRepository = OrganizationRepository(database.organizationDao())
    val organizationViewModel: OrganizationViewModel = viewModel(
        factory = OrganizationViewModel.Factory(organizationRepository)
    )

    // Estados del ViewModel
    val allEvents by blogEventsViewModel.events.collectAsStateWithLifecycle()
    val allChannels by blogEventsViewModel.channels.collectAsStateWithLifecycle()
    val organizations by blogEventsViewModel.organizations.collectAsStateWithLifecycle()
    val isLoading by blogEventsViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by blogEventsViewModel.errorMessage.collectAsStateWithLifecycle()
    val selectedChannelId by blogEventsViewModel.selectedChannelId.collectAsStateWithLifecycle()

    // ✅ FILTRAR POR ORGANIZACIÓN ESPECÍFICA
    val currentOrganization = remember(organizations, organizationId) {
        organizationId?.let { id -> organizations.find { it.organizationID == id } }
    }

    // ✅ SOLO EVENTOS DE ESTA ORGANIZACIÓN
    val organizationEvents = remember(allEvents, organizationId) {
        if (organizationId != null) {
            allEvents.filter { event ->
                val eventChannel = allChannels.find { it.id == event.channelId }
                eventChannel?.organizationId == organizationId
            }
        } else {
            allEvents
        }
    }

    // ✅ SOLO CANALES DE ESTA ORGANIZACIÓN
    val organizationChannels = remember(allChannels, organizationId) {
        if (organizationId != null) {
            allChannels.filter { it.organizationId == organizationId }
        } else {
            allChannels
        }
    }

    // Estados locales
    var selectedEvent by remember { mutableStateOf<EventInstituteBlog?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }
    var showChannelDialog by remember { mutableStateOf(false) }

    // ✅ SUSCRIPCIONES ACTIVAS DE ESTA ORGANIZACIÓN
    var subscribedChannelIds by remember { mutableStateOf<List<Int>>(emptyList()) }

    // Cargar suscripciones de esta organización
    LaunchedEffect(organizationId) {
        if (organizationId != null) {
            // Obtener suscripciones activas de esta organización
            organizationRepository.getSubscriptionsByOrganization(organizationId)
                .collect { subscriptions ->
                    subscribedChannelIds = subscriptions
                        .filter { it.isActive }
                        .map { it.channelId }
                }
        } else {
            subscribedChannelIds = emptyList()
        }
    }

    // ✅ FILTRAR CANALES SUSCRITOS PARA LOS CHIPS
    val subscribedChannels = remember(organizationChannels, subscribedChannelIds) {
        organizationChannels.filter { channel ->
            subscribedChannelIds.contains(channel.id)
        }
    }

    // ✅ FILTRADO CORRECTO DE EVENTOS
    val filteredEvents =
        remember(selectedChannelId, organizationEvents, subscribedChannelIds, searchQuery) {
            var events = organizationEvents

            // Filtrar por búsqueda
            if (searchQuery.isNotEmpty()) {
                events = events.filter { event ->
                    event.title.contains(searchQuery, ignoreCase = true) ||
                            event.shortDescription.contains(searchQuery, ignoreCase = true) ||
                            event.longDescription.contains(searchQuery, ignoreCase = true)
                }
            }

            // Filtrar por canal seleccionado
            when {
                selectedChannelId != null -> {
                    events.filter { it.channelId == selectedChannelId }
                }

                subscribedChannelIds.isNotEmpty() -> {
                    events.filter { event ->
                        event.channelId?.let { subscribedChannelIds.contains(it) } ?: false
                    }
                }

                else -> {
                    // Si no hay suscripciones, no mostrar eventos
                    emptyList()
                }
            }
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header con controles y título de organización
        EventBlogHeader(
            organizationName = currentOrganization?.acronym ?: "Eventos",
            searchQuery = searchQuery,
            showSearchBar = showSearchBar,
            onSearchQueryChanged = {
                searchQuery = it
                // Ejecutar búsqueda si hay query
                if (it.isNotEmpty()) {
                    blogEventsViewModel.searchEvents(it)
                }
            },
            onSearchToggle = { showSearchBar = !showSearchBar },
            onChannelsClick = { showChannelDialog = true },
            onRefresh = {
                if (organizationId != null) {
                    blogEventsViewModel.filterEventsByOrganization(organizationId)
                } else {
                    blogEventsViewModel.refresh()
                }
            },
            isLoading = isLoading
        )

        // ✅ Lista de canales (chips horizontales) - SOLO CANALES SUSCRITOS
        if (subscribedChannels.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Chip "Todos" (todos los suscritos)
                item {
                    FilterChip(
                        onClick = { blogEventsViewModel.clearFilters() },
                        label = { Text("Todos") },
                        selected = selectedChannelId == null,
                        leadingIcon = if (selectedChannelId == null) {
                            {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else null
                    )
                }

                // ✅ SOLO CHIPS DE CANALES SUSCRITOS
                items(subscribedChannels) { channel ->
                    FilterChip(
                        onClick = {
                            blogEventsViewModel.filterEventsByChannel(
                                if (selectedChannelId == channel.id) null else channel.id
                            )
                        },
                        label = {
                            Text(
                                text = channel.acronym,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium
                            )
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

        // Mensaje de error
        errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // Lista de eventos
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            filteredEvents.isEmpty() -> {
                EmptyEventsState(
                    isFiltered = selectedChannelId != null || subscribedChannelIds.isNotEmpty(),
                    onClearFilters = { blogEventsViewModel.clearFilters() },
                    modifier = Modifier.fillMaxSize(),
                    hasSubscriptions = subscribedChannelIds.isNotEmpty()
                )
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredEvents) { event ->
                        EventCardBlogAPI(
                            event = event,
                            channels = subscribedChannels, // ✅ USAR SOLO CANALES SUSCRITOS
                            organizations = organizations,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedEvent = event }
                        )
                    }
                }
            }
        }
    }

    // Dialog de detalles del evento
    selectedEvent?.let { event ->
        EventDetailCardBlogAPI(
            event = event,
            channels = allChannels, // Para el dialog usar todos los canales
            organizations = organizations,
            onDismiss = { selectedEvent = null },
            eventViewModel = eventViewModel
        )
    }

    // Dialog de gestión de canales - SOLO DE ESTA ORGANIZACIÓN
    if (showChannelDialog && organizationId != null) {
        ChannelSubscriptionDialog(
            channels = organizationChannels, // Todos los canales para gestión
            organizationId = organizationId,
            organizationViewModel = organizationViewModel,
            onDismiss = { showChannelDialog = false },
            onSubscriptionsChanged = { channelIds ->
                subscribedChannelIds = channelIds
                showChannelDialog = false
                // Limpiar filtros para aplicar el nuevo filtro de suscripciones
                blogEventsViewModel.clearFilters()
            }
        )
    }
}

@Composable
fun EventBlogHeader(
    organizationName: String,
    searchQuery: String,
    showSearchBar: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onSearchToggle: () -> Unit,
    onChannelsClick: () -> Unit,
    onRefresh: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Barra de título y acciones
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = organizationName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "canales",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    // Botón de gestión de canales
                    IconButton(
                        onClick = onChannelsClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Gestionar canales",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                }
                // Botón de búsqueda
                IconButton(onClick = onSearchToggle) {
                    Icon(
                        imageVector = if (showSearchBar) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = if (showSearchBar) "Cerrar búsqueda" else "Buscar"
                    )
                }

                // Botón de refrescar
                IconButton(
                    onClick = onRefresh,
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualizar"
                    )
                }
            }
        }

        // Barra de búsqueda (si está activa)
        if (showSearchBar) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Buscar eventos...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar"
                            )
                        }
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

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
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header del evento
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (event.shortDescription.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = event.shortDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (event.imagePath != "") {
                        AsyncImage(
                            model = "https://academic-ally-backend-113306869747.us-central1.run.app/images/${event.imagePath}",
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                }

                // Información del canal/organización
                if (organization != null) {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = organization.acronym,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                        if (channel != null) {
                            Text(
                                text = channel.acronym,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Ubicación si existe
            if (event.location.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
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

@Composable
fun EmptyEventsState(
    isFiltered: Boolean,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier,
    hasSubscriptions: Boolean = true // ✅ NUEVO parámetro
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isFiltered) Icons.Default.FilterList else Icons.Default.EventNote,
            contentDescription = "Sin eventos",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when {
                !hasSubscriptions -> "No estás suscrito a ningún canal"
                isFiltered -> "No hay eventos en este canal"
                else -> "No hay eventos disponibles"
            },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when {
                !hasSubscriptions -> "Ve a 'Gestionar canales' para suscribirte a los canales que te interesan"
                isFiltered -> "Prueba seleccionando otro canal o viendo todos los eventos"
                else -> "Los eventos aparecerán aquí cuando estén disponibles"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        if (isFiltered && hasSubscriptions) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(onClick = onClearFilters) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver eventos suscritos")
            }
        }
    }
}

// Placeholder para EventDetailCardBlogAPI (si no existe)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventDetailCardBlogAPI(
    event: EventInstituteBlog,
    channels: List<Channel>,
    organizations: List<Organization>,
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
                .heightIn(max = 650.dp),
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
                                text = "${organization.acronym} - ${channel.name}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${channel.type.name} • ${organization.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Título del evento
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Descripción corta si existe
                if (event.shortDescription.isNotEmpty()) {
                    Text(
                        text = event.shortDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Imagen del evento si existe
                if (event.imagePath.isNotEmpty()) {
                    AsyncImage(
                        model = "https://academic-ally-backend-113306869747.us-central1.run.app/images/${event.imagePath}",
                        contentDescription = event.shortDescription,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(vertical = 8.dp)
                    )
                }

                // Información de fechas
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatEventDate(event.startDate, event.endDate),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Ubicación si existe
                if (event.location.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = event.location,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Categoría
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Category,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = event.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Descripción completa
                if (event.longDescription.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = event.longDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón Cerrar
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cerrar")
                    }

                    // Botón Agregar al Calendario (si eventViewModel está disponible)
                    if (eventViewModel != null) {
                        Button(
                            onClick = {
                                // Convertir EventInstituteBlog a Event local
                                val localEvent = convertToLocalEvent(event)
                                eventViewModel.addEvent(localEvent)
                                showSuccessMessage = true
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Agregar")
                        }
                    }
                }

                // Mensaje de éxito
                if (showSuccessMessage) {
                    LaunchedEffect(Unit) {
                        delay(2000)
                        showSuccessMessage = false
                        onDismiss()
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Evento agregado al calendario",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun convertToLocalEvent(blogEvent: EventInstituteBlog): Event {
    return Event(
        id = blogEvent.id,
        title = blogEvent.title,
        shortDescription = blogEvent.shortDescription,
        longDescription = blogEvent.longDescription,
        location = blogEvent.location,
        colorIndex = 0, // Color por defecto
        startDate = LocalDate.parse(blogEvent.startDate),
        endDate = LocalDate.parse(blogEvent.endDate),
        category = EventCategory.INSTITUTIONAL,
        imagePath = blogEvent.imagePath,
        shape = EventShape.RoundedFull, // Forma por defecto
        notification = null,
        mesID = null
    )
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatEventDate(startDate: String?, endDate: String?): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val displayFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "ES"))

        when {
            startDate != null && endDate != null && startDate != endDate -> {
                val start = LocalDate.parse(startDate, formatter)
                val end = LocalDate.parse(endDate, formatter)
                "${start.format(displayFormatter)} - ${end.format(displayFormatter)}"
            }

            startDate != null -> {
                val start = LocalDate.parse(startDate, formatter)
                start.format(displayFormatter)
            }

            else -> "Fecha por confirmar"
        }
    } catch (e: Exception) {
        "Fecha no disponible"
    }
}