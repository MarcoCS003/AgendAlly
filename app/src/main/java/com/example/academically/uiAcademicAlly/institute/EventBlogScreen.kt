package com.example.academically.uiAcademicAlly.institute

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.academically.ViewModel.BlogEventsViewModel
import com.example.academically.ViewModel.EventViewModel
import com.example.academically.data.EventCategory
import com.example.academically.data.EventInstitute
import com.example.academically.data.api.EventInstituteBlog
import com.example.academically.utils.EventItemHandler
import kotlinx.coroutines.delay

enum class EventTab {
    INSTITUTE,
    CAREER
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventBlogScreen(
    blogEventsViewModel: BlogEventsViewModel? = null,
    eventViewModel: EventViewModel? = null,
    modifier: Modifier = Modifier
) {
    // Estados del ViewModel de la API
    val apiEvents by blogEventsViewModel?.events?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(emptyList<EventInstituteBlog>()) }
    val isLoading by blogEventsViewModel?.isLoading?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }
    val errorMessage by blogEventsViewModel?.errorMessage?.collectAsStateWithLifecycle() ?: remember { mutableStateOf<String?>(null) }

    // Estados locales
    var selectedTab by remember { mutableStateOf(EventTab.INSTITUTE) }
    var selectedEvent by remember { mutableStateOf<EventInstitute?>(null) }
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Convertir eventos de la API a formato local
    val convertedEvents = remember(apiEvents) {
        apiEvents.map { apiEvent ->
            convertAPIToLocal(apiEvent)
        }
    }

    // Filtrar eventos según la pestaña seleccionada
    val filteredEvents = remember(selectedTab, convertedEvents) {
        when (selectedTab) {
            EventTab.INSTITUTE -> convertedEvents.filter { it.category == EventCategory.INSTITUTIONAL }
            EventTab.CAREER -> convertedEvents.filter { it.category == EventCategory.CAREER }
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
                            onValueChange = { query ->
                                searchQuery = query
                                if (query.length >= 2 || query.isEmpty()) {
                                    blogEventsViewModel?.searchEvents(query)
                                }
                            },
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

                        IconButton(
                            onClick = {
                                showSearchBar = false
                                searchQuery = ""
                                blogEventsViewModel?.loadAllEvents()
                            }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Tabs de Instituto y Carrera
            EventTabRow(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            // Contenido principal
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading && convertedEvents.isEmpty() -> {
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

                    errorMessage != null && convertedEvents.isEmpty() -> {
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
                                    blogEventsViewModel?.clearError()
                                    blogEventsViewModel?.loadAllEvents()
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
                        // Lista de eventos usando tu diseño original
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
                                Spacer(modifier = Modifier.height(80.dp)) // Espacio para el FAB
                            }
                        }
                    }
                }

                // Loading overlay para refresh
                if (isLoading && convertedEvents.isNotEmpty()) {
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

        // FloatingActionButton animado para búsqueda
        AnimatedVisibility(
            visible = !showSearchBar,
            enter = scaleIn(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
            exit = scaleOut(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300)),
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            FloatingActionButton(
                onClick = {
                    showSearchBar = true
                },
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

        // FAB secundario para actualizar (solo visible cuando hay búsqueda activa)
        if (showSearchBar) {
            FloatingActionButton(
                onClick = {
                    blogEventsViewModel?.refreshEvents()
                },
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
            blogEventsViewModel?.clearError()
        }
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

// Función para convertir EventInstituteBlog a EventInstitute
@RequiresApi(Build.VERSION_CODES.O)
private fun convertAPIToLocal(apiEvent: EventInstituteBlog): EventInstitute {
    // Convertir items de la API a items locales
    val localItems = apiEvent.items.map { apiItem ->
        EventItemHandler.convertToLocalEventItem(apiItem)
    }

    return EventInstitute(
        id = apiEvent.id,
        title = apiEvent.title,
        shortDescription = apiEvent.shortDescription,
        longDescription = apiEvent.longDescription,
        location = apiEvent.location,
        color = when (apiEvent.category) {
            "INSTITUTIONAL" -> Color(0xFF2196F3) // Azul
            "CAREER" -> Color(0xFF4CAF50) // Verde
            else -> Color(0xFFFF9800) // Naranja
        },
        startDate = apiEvent.startDate?.let { java.time.LocalDate.parse(it) },
        endDate = apiEvent.endDate?.let { java.time.LocalDate.parse(it) },
        category = when (apiEvent.category) {
            "INSTITUTIONAL" -> EventCategory.INSTITUTIONAL
            "CAREER" -> EventCategory.CAREER
            else -> EventCategory.PERSONAL
        },
        imagePath = apiEvent.imagePath,
        items = localItems, // NUEVO: Items convertidos
        instituteId = apiEvent.instituteId
    )
}