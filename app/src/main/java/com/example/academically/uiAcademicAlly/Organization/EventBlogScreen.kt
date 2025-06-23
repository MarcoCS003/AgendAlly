package com.example.academically.uiAcademicAlly.Organization

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.academically.ViewModel.BlogEventsViewModel
import com.example.academically.ViewModel.EventViewModel
import com.example.academically.ViewModel.OrganizationViewModel
import com.example.academically.data.model.EventOrganization
import com.example.academically.data.model.PersonalEventType
import com.example.academically.data.mappers.UserSubscriptionDomain
import com.example.academically.data.repositorty.IntegratedRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventBlogScreen(
    blogEventsViewModel: BlogEventsViewModel,
    eventViewModel: EventViewModel? = null,
    organizationViewModel: OrganizationViewModel? = null,
    onNavigateToSubscriptions: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // ✅ Estados del ViewModel corregido
    val events by blogEventsViewModel.events.collectAsStateWithLifecycle()
    val isLoading by blogEventsViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by blogEventsViewModel.errorMessage.collectAsStateWithLifecycle()

    // Estados de suscripciones del usuario
    val userSubscriptions = organizationViewModel?.userSubscriptions ?: emptyList()
    val isLoadingSubscriptions = organizationViewModel?.isLoading ?: false

    // Estados locales
    var selectedChannelId by remember { mutableStateOf<Int?>(null) }
    var selectedEvent by remember { mutableStateOf<EventOrganization?>(null) }
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // ✅ Cargar suscripciones y eventos al inicializar
    LaunchedEffect(Unit) {
        organizationViewModel?.loadUserSubscriptions()
        // blogEventsViewModel ya carga eventos en su init{}
    }

    // Filtrar eventos según el canal seleccionado y suscripciones
    val filteredEvents = remember(selectedChannelId, events, userSubscriptions) {
        if (userSubscriptions.isEmpty()) {
            // Si no hay suscripciones, no mostrar eventos
            emptyList()
        } else {
            val subscribedChannelIds = userSubscriptions.filter { it.isActive }.map { it.channelId }

            if (selectedChannelId == null) {
                // Mostrar todos los eventos de canales suscritos
                events.filter { event ->
                    // TODO: Aquí necesitas conectar event.organizationId o channelId con las suscripciones
                    // Por ahora, mostrar todos los eventos SUBSCRIBED
                    event.category == PersonalEventType.SUBSCRIBED
                }
            } else {
                // Filtrar por canal específico
                events.filter { event ->
                    // TODO: Implementar filtro por channelId específico
                    // event.channelId == selectedChannelId && subscribedChannelIds.contains(selectedChannelId)
                    event.category == PersonalEventType.SUBSCRIBED
                }
            }
        }
    }

    // Animación del FAB
    val fabScale by animateFloatAsState(
        targetValue = if (showSearchBar) 0f else 1f,
        animationSpec = tween(300),
        label = "fab_scale"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header con título y botón de suscripciones
            EventBlogHeader(
                onNavigateToSubscriptions = onNavigateToSubscriptions,
                showSearchBar = showSearchBar,
                onToggleSearch = { showSearchBar = !showSearchBar }
            )

            // Barra de búsqueda animada
            AnimatedVisibility(
                visible = showSearchBar,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            ) {
                SearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { query ->
                        searchQuery = query
                        if (query.length >= 2 || query.isEmpty()) {
                            blogEventsViewModel.searchEvents(query)  // ✅ Usar ViewModel
                        }
                    },
                    onCloseSearch = {
                        showSearchBar = false
                        searchQuery = ""
                        blogEventsViewModel.loadAllEvents()  // ✅ Usar ViewModel
                    }
                )
            }

            // Filtros de canales suscritos
            if (userSubscriptions.isNotEmpty()) {
                ChannelFilters(
                    subscriptions = userSubscriptions,
                    selectedChannelId = selectedChannelId,
                    onChannelSelected = { channelId ->
                        selectedChannelId = if (selectedChannelId == channelId) null else channelId
                    }
                )
            }

            // Contenido principal
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading && events.isEmpty() -> {
                        LoadingState()
                    }

                    errorMessage != null && events.isEmpty() -> {
                        ErrorState(
                            errorMessage = errorMessage!!,
                            onRetry = {
                                blogEventsViewModel.clearError()  // ✅ Usar ViewModel
                                blogEventsViewModel.loadAllEvents()  // ✅ Usar ViewModel
                            }
                        )
                    }

                    userSubscriptions.isEmpty() && !isLoadingSubscriptions -> {
                        EmptySubscriptionsState(
                            onNavigateToSubscriptions = onNavigateToSubscriptions
                        )
                    }

                    filteredEvents.isEmpty() -> {
                        EmptyEventsState()
                    }

                    else -> {
                        EventsList(
                            events = filteredEvents,
                            onEventClick = { event -> selectedEvent = event }
                        )
                    }
                }

                // Loading overlay para refresh
                if (isLoading && events.isNotEmpty()) {
                    LoadingOverlay()
                }
            }
        }

        // FloatingActionButton animado para búsqueda
        AnimatedVisibility(
            visible = !showSearchBar,
            enter = scaleIn(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
            exit = scaleOut(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300)),
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            FloatingActionButton(
                onClick = { showSearchBar = true },
                modifier = Modifier
                    .padding(16.dp)
                    .scale(fabScale),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Buscar eventos",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // FAB secundario para actualizar
        if (showSearchBar) {
            FloatingActionButton(
                onClick = { blogEventsViewModel.refreshEvents() },  // ✅ Usar ViewModel
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Actualizar eventos",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    // Mostrar diálogo de detalle si hay un evento seleccionado
    selectedEvent?.let { event ->
        EventDetailCardBlog(
            event = event,
            onDismiss = { selectedEvent = null },
            eventViewModel = eventViewModel
        )
    }

    // Mostrar snackbar de error si hay mensaje
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            delay(3000)
            blogEventsViewModel.clearError()  // ✅ Usar ViewModel
        }
    }
}

// Resto de los composables se mantienen igual...
@Composable
fun EventBlogHeader(
    onNavigateToSubscriptions: () -> Unit,
    showSearchBar: Boolean,
    onToggleSearch: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Eventos",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row {
            IconButton(onClick = onToggleSearch) {
                Icon(
                    imageVector = if (showSearchBar) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = if (showSearchBar) "Cerrar búsqueda" else "Buscar",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            IconButton(onClick = onNavigateToSubscriptions) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Gestionar suscripciones",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = "Buscar",
                tint = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                placeholder = { Text("Buscar eventos...") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            IconButton(onClick = onCloseSearch) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ChannelFilters(
    subscriptions: List<UserSubscriptionDomain>,
    selectedChannelId: Int?,
    onChannelSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Filtrar por canal:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedChannelId == null,
                    onClick = { onChannelSelected(-1) },
                    label = { Text("Todos") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }

            items(subscriptions.filter { it.isActive }) { subscription ->
                FilterChip(
                    selected = selectedChannelId == subscription.channelId,
                    onClick = { onChannelSelected(subscription.channelId) },
                    label = { Text(subscription.channelName) },
                    leadingIcon = {
                        Icon(
                            imageVector = getChannelTypeIcon(subscription.channelType),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}


// Resto de composables (EmptySubscriptionsState, EmptyEventsState, LoadingState, ErrorState, EventsList, LoadingOverlay)
// se mantienen igual que en el código original...

@Composable

fun EmptySubscriptionsState(

    onNavigateToSubscriptions: () -> Unit

) {

    Column(

        modifier = Modifier.fillMaxSize(),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center

    ) {

        Icon(

            imageVector = Icons.Default.NotificationsOff,

            contentDescription = "Sin suscripciones",

            tint = MaterialTheme.colorScheme.outline,

            modifier = Modifier.size(64.dp)

        )



        Spacer(modifier = Modifier.height(16.dp))



        Text(

            text = "No tienes suscripciones",

            fontSize = 20.sp,

            fontWeight = FontWeight.SemiBold,

            color = MaterialTheme.colorScheme.onSurface

        )



        Text(

            text = "Suscríbete a canales para ver eventos",

            fontSize = 14.sp,

            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),

            modifier = Modifier.padding(top = 8.dp)

        )



        Spacer(modifier = Modifier.height(24.dp))



        Button(

            onClick = onNavigateToSubscriptions,

            modifier = Modifier.padding(horizontal = 32.dp)

        ) {

            Icon(

                imageVector = Icons.Default.Add,

                contentDescription = null,

                modifier = Modifier.size(18.dp)

            )

            Spacer(modifier = Modifier.width(8.dp))

            Text("Gestionar Suscripciones")

        }

    }

}


@Composable

fun EmptyEventsState() {

    Column(

        modifier = Modifier.fillMaxSize(),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center

    ) {

        Icon(

            imageVector = Icons.Default.EventNote,

            contentDescription = "Sin eventos",

            tint = MaterialTheme.colorScheme.outline,

            modifier = Modifier.size(64.dp)

        )



        Spacer(modifier = Modifier.height(16.dp))



        Text(

            text = "No hay eventos disponibles",

            fontSize = 18.sp,

            fontWeight = FontWeight.Medium,

            color = MaterialTheme.colorScheme.onSurface

        )



        Text(

            text = "Los eventos de tus canales suscritos aparecerán aquí",

            fontSize = 14.sp,

            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),

            modifier = Modifier.padding(top = 8.dp)

        )

    }

}


@Composable

fun LoadingState() {

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


@Composable

fun ErrorState(

    errorMessage: String,

    onRetry: () -> Unit

) {

    Box(

        modifier = Modifier.fillMaxSize(),

        contentAlignment = Alignment.Center

    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Icon(

                imageVector = Icons.Default.Error,

                contentDescription = "Error",

                tint = MaterialTheme.colorScheme.error,

                modifier = Modifier.size(48.dp)

            )



            Spacer(modifier = Modifier.height(16.dp))



            Text(

                text = "Error al cargar eventos",

                style = MaterialTheme.typography.titleMedium,

                color = MaterialTheme.colorScheme.error

            )



            Text(

                text = errorMessage,

                style = MaterialTheme.typography.bodyMedium,

                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),

                modifier = Modifier.padding(16.dp)

            )



            Button(onClick = onRetry) {

                Text("Reintentar")

            }

        }

    }

}


@Composable

fun EventsList(

    events: List<EventOrganization>,

    onEventClick: (EventOrganization) -> Unit

) {

    LazyColumn(

        modifier = Modifier.fillMaxSize(),

        verticalArrangement = Arrangement.spacedBy(8.dp),

        contentPadding = PaddingValues(16.dp)

    ) {

        items(events) { event ->

            EventCardBlog(

                event = event,

                modifier = Modifier

                    .fillMaxWidth()

                    .clickable { onEventClick(event) }

            )

        }


        // Espacio adicional al final para el FAB

        item {

            Spacer(modifier = Modifier.height(80.dp))

        }

    }

}


@Composable

fun LoadingOverlay() {

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


