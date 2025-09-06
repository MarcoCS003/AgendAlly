package com.agendally.app.uiAcademicAlly.calendar

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.agendally.app.ViewModel.EventViewModel
import com.agendally.app.data.database.AcademicAllyDatabase
import com.agendally.app.data.repository.EventRepository
import com.agendally.app.ui.theme.ScheduleColorsProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@SuppressLint("NewApi")
@Composable
fun EditEventScreenWithViewModel(
    eventId: Int,
    viewModel: EventViewModel = viewModel(  // ✅ AGREGAR default value como en EditScheduleActivityScreen
        factory = EventViewModel.Factory(
            EventRepository(
                AcademicAllyDatabase.getDatabase(LocalContext.current).eventDao()
            )
        )
    ),
    onNavigateBack: () -> Unit = {}
) {
    // Cargamos el evento al iniciar
    val allEvents by viewModel.allEvents.collectAsState()
    val event = remember(allEvents, eventId) {
        allEvents.find { it.id == eventId }
    }
    if (event == null) {
        // Si no se encuentra el evento, mostrar un mensaje y volver
        LaunchedEffect(Unit) {
            onNavigateBack()
        }
        return
    }

    // Observar el evento seleccionado
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Mostrar pantalla de carga mientras se obtiene el evento
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
        return
    }


    // Estados para el formulario, inicializados con los datos del evento
    var title by remember(event) { mutableStateOf(event.title) }
    var selectedColor by remember(event) { mutableIntStateOf(event.colorIndex) }
    var location by remember(event) { mutableStateOf(event.location) }
    var description by remember(event) { mutableStateOf(event.shortDescription) }

    // Determinar si es un evento de varios días
    var isMultiDayEvent by remember(event) {
        mutableStateOf(event.startDate != event.endDate)
    }

    // Fechas
    var showDatePickerStart by remember { mutableStateOf(false) }
    var showDatePickerEnd by remember { mutableStateOf(false) }

    // Fecha inicio y fin
    var selectedCreateStartDay by remember(event) {
        mutableStateOf(event.startDate ?: LocalDate.now())
    }
    var selectedCreateStartEnd by remember(event) {
        mutableStateOf(event.endDate ?: LocalDate.now())
    }

    var showColorPicker by remember { mutableStateOf(false) }


    val colors = ScheduleColorsProvider.getColors()

    // Mostrar SnackBar para errores
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            // Aquí podrías mostrar un SnackBar con el mensaje de error
        }
    }

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Barra superior con botón de retroceso
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }

                Text(
                    text = "Editar Evento",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // Campo de título
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título", fontSize = 28.sp) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                ),
                singleLine = true,
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Switch para evento de varios días
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Evento de varios días",
                    style = MaterialTheme.typography.bodyLarge
                )

                Switch(
                    checked = isMultiDayEvent,
                    onCheckedChange = {
                        isMultiDayEvent = it
                        // Si activamos el switch, establecemos la fecha final igual a la inicial
                        if (it) {
                            selectedCreateStartEnd = selectedCreateStartDay
                        }
                    }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Selección de fecha
            if (isMultiDayEvent) {
                // Mostrar dos selectores de fecha (inicio y fin)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    // Fecha de inicio
                    Column {
                        Text(
                            text = "Fecha de inicio",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Row(modifier = Modifier.clickable { showDatePickerStart = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Fecha inicio",
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = selectedCreateStartDay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // Fecha de fin
                    Column {
                        Text(
                            text = "Fecha de fin",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Row(modifier = Modifier.clickable { showDatePickerEnd = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Fecha fin",
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = selectedCreateStartEnd.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            } else {
                // Mostrar solo un selector de fecha
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { showDatePickerStart = true }
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Fecha de inicio",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(modifier = Modifier.clickable {showDatePickerStart = true}) {
                            Icon(
                                imageVector = Icons.Default.DateRange, contentDescription = "Fecha de inicio",
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text =selectedCreateStartDay.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Selección de color
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Color",
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.width(16.dp))

                val scheduleColors = ScheduleColorsProvider.getColors()
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (scheduleColors.isNotEmpty())
                                scheduleColors[selectedColor % scheduleColors.size]
                            else
                                Color.Gray
                        )
                        .clickable { showColorPicker = true }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Campo de ubicación
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Ubicación",
                    modifier = Modifier.size(32.dp)
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { Text("Añadir ubicación") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            // Campo de descripción
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ViewHeadline,
                    contentDescription = "Descripción",
                    modifier = Modifier.size(32.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Añadir descripción") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    minLines = 3
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón de guardar
            Button(
                onClick = {
                    // Crear evento actualizado
                    event?.let { event ->
                        val updatedEvent = event.copy(
                            title = title,
                            shortDescription = description,
                            longDescription = description,
                            location = location,
                            colorIndex = selectedColor,
                            startDate = selectedCreateStartDay,
                            endDate = if (isMultiDayEvent) selectedCreateStartEnd else selectedCreateStartDay
                        )

                        // Actualizar el evento
                        viewModel.updateEvent(updatedEvent)

                        // Volver atrás
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = title.isNotEmpty() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar")
                }
            }
        }

        // Diálogos
        // Diálogo de selección de fecha de inicio
        if (showDatePickerStart) {
            DatePickerDialog(
                selectedDate = selectedCreateStartDay,
                minDate = null, // No hay restricción para la fecha de inicio
                onDateSelected = {
                    selectedCreateStartDay = it
                    // Si es un evento de varios días, actualizar la fecha de fin
                    // para que no sea anterior a la de inicio
                    if (isMultiDayEvent && selectedCreateStartEnd.isBefore(it)) {
                        selectedCreateStartEnd = it
                    }
                    showDatePickerStart = false
                },
                onDismiss = { showDatePickerStart = false }
            )
        }

        // Diálogo de selección de fecha de fin
        if (showDatePickerEnd) {
            DatePickerDialog(
                selectedDate = selectedCreateStartEnd,
                minDate = selectedCreateStartDay, // Restricción: no puede ser anterior a la fecha de inicio
                onDateSelected = {
                    selectedCreateStartEnd = it
                    showDatePickerEnd = false
                },
                onDismiss = { showDatePickerEnd = false }
            )
        }

        // Diálogo de selección de color
        if (showColorPicker) {
            ColorPickerDialog(
                selectedColorIndex = selectedColor,
                onColorSelected = {
                    selectedColor = it
                    showColorPicker = false
                },
                onDismiss = { showColorPicker = false }
            )
        }
    }
}