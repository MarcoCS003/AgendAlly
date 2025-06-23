package com.example.academically.uiAcademicAlly.Organization


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.academically.ViewModel.EventViewModel
import com.example.academically.data.model.EventOrganization
import com.example.academically.data.model.PersonalEventType

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TabletEventBlogScreen(
    events: List<EventOrganization>,
    modifier: Modifier = Modifier,
    eventViewModel: EventViewModel? = null // Añadir parámetro
) {
    // Implementare en otra version adaptabilidad
    /*
    var selectedTab by remember { mutableStateOf(EventTab.INSTITUTE) }
    var selectedEvent by remember { mutableStateOf<EventOrganization?>(null) }

    // Filtrar eventos según la pestaña seleccionada
    val filteredEvents = remember(selectedTab, events) {
        when (selectedTab) {
            EventTab.INSTITUTE -> events.filter { it.category == PersonalEventType.SUBSCRIBED } // CAMBIADO: category -> type
            EventTab.CAREER -> events.filter { it.category == PersonalEventType.SUBSCRIBED }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // REUTILIZAR el componente EventTabRow existente
        EventTabRow(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // CAMBIO PRINCIPAL: LazyVerticalGrid en lugar de LazyColumn
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 350.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredEvents) { event ->
                // REUTILIZAR exactamente el mismo EventCardBlog
                EventCardBlog(
                    event = event,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedEvent = event }
                )
            }
        }
    }

    // REUTILIZAR exactamente el mismo diálogo
    selectedEvent?.let { event ->
        EventDetailCardBlog(
            event = event,
            onDismiss = { selectedEvent = null },
            eventViewModel = eventViewModel // Pasar el ViewModel
        )
    }
    */
}