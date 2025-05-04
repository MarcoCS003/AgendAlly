package com.example.academically.uiAcademicAlly.calendar

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academically.ViewModel.EventViewModel
import com.example.academically.data.Event
import com.example.academically.data.EventCategory
import com.example.academically.data.EventShape
import com.example.academically.data.database.AcademicAllyDatabase
import com.example.academically.data.repository.EventRepository
import com.example.academically.ui.theme.DarkThemeScheduleColors
import com.example.academically.ui.theme.LightThemeScheduleColors
import com.example.academically.ui.theme.ScheduleColorsProvider
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@SuppressLint("NewApi")
@Composable
fun AddEventScreenWithViewModel(
    onBack: () -> Unit = {},
    viewModel: EventViewModel
) {
    // Inicializar ViewModel
    val context = LocalContext.current
    val database = AcademicAllyDatabase.getDatabase(context)
    val repository = EventRepository(database.eventDao())
    val eventViewModel: EventViewModel = viewModel(
        factory = EventViewModel.Factory(repository)
    )

    var title by remember { mutableStateOf("") }
    var selectedEndDay by remember { mutableStateOf(LocalDate.now()) }
    var selectedColor by remember { mutableIntStateOf(0) }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Añadir estado para el switch de evento de varios días
    var isMultiDayEvent by remember { mutableStateOf(false) }

    // Fechas
    var showDatePickerStart by remember { mutableStateOf(false) }
    var showDatePickerEnd by remember { mutableStateOf(false) }

    // Fecha inicio
    var selectedCreateStartDay by remember { mutableStateOf(LocalDate.now()) }
    var selectedCreateStartEnd by remember { mutableStateOf(LocalDate.now()) }

    var showColorPicker by remember { mutableStateOf(false) }

    // Estado para mostrar cargando
    val isLoading by viewModel.isLoading.collectAsState()

    // Estado para errores
    val errorMessage by viewModel.errorMessage.collectAsState()
    // Mostrar SnackBar para errores
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            // Aquí podrías mostrar un SnackBar con el mensaje de error
        }
    }

    val scrollState = rememberScrollState()

    errorMessage?.let { message ->
        LaunchedEffect(message) {
            // Aquí podrías mostrar un SnackBar con el mensaje de error
        }
    }


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
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }

                Text(
                    text = "Añadir Evento",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // Campo de título
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Añadir título", fontSize = 28.sp) },
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
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Fecha",
                        modifier = Modifier.size(32.dp)
                    )

                    Text(
                        text = selectedCreateStartDay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 18.dp)
                    )
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

            // Botón de agregar
            Button(
                onClick = {
                    // Crear evento
                    val newEvent = Event(
                        id = 0, // El ID lo asignará Room
                        title = title,
                        shortDescription = description,
                        longDescription = description,
                        location = location,
                        colorIndex = selectedColor,
                        startDate = selectedCreateStartDay,
                        endDate = if (isMultiDayEvent) selectedCreateStartEnd else selectedCreateStartDay,
                        category = EventCategory.PERSONAL,
                        shape = EventShape.RoundedFull
                    )

                    // Guardar el evento
                    viewModel.insertEvent(newEvent)
                    // Volver atrás
                    onBack()
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
                    Text("Agregar")
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

@SuppressLint("NewApi")
@Composable
fun DatePickerDialog(
    selectedDate: LocalDate,
    minDate: LocalDate?, // Fecha mínima seleccionable (opcional)
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var currentMonth by remember { mutableStateOf(selectedDate.month) }
    var currentYear by remember { mutableIntStateOf(selectedDate.year) }
    var currentSelectedDate by remember { mutableStateOf(selectedDate) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                // Título con la fecha seleccionada
                Text(
                    text = "${currentSelectedDate.dayOfMonth} de ${currentSelectedDate.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Calendario
                SimpleCalendarView(
                    currentMonth = currentMonth,
                    currentYear = currentYear,
                    selectedDate = currentSelectedDate,
                    minDate = minDate,
                    onMonthYearChanged = { month, year ->
                        currentMonth = month
                        currentYear = year
                    },
                    onDateSelected = {
                        currentSelectedDate = it
                    }
                )

                // Botones de acción
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }

                    TextButton(
                        onClick = {
                            onDateSelected(currentSelectedDate)
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            }
        }
    }
}


@SuppressLint("NewApi")
@Composable
fun SimpleCalendarView(
    currentMonth: java.time.Month,
    currentYear: Int,
    selectedDate: LocalDate,
    minDate: LocalDate?, // Fecha mínima seleccionable (opcional)
    onMonthYearChanged: (java.time.Month, Int) -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    // Usamos estado mutable para poder cambiar el mes y año
    var month by remember { mutableStateOf(currentMonth) }
    var year by remember { mutableIntStateOf(currentYear) }

    // Observar cambios en month y year
    LaunchedEffect(month, year) {
        onMonthYearChanged(month, year)
    }

    // Recalcular valores cuando cambian el mes o año
    val daysInMonth = remember(month, year) {
        YearMonth.of(year, month).lengthOfMonth()
    }

    // CORRECCIÓN: Usar la misma lógica que en CalendarScreen
    val firstDayOfMonth = remember(month, year) {
        // Obtenemos el día de la semana (1=lunes, 2=martes, ..., 7=domingo)
        val dayOfWeek = LocalDate.of(year, month, 1).dayOfWeek.value

        // Ajustamos para que domingo sea 0, lunes sea 1, etc.
        if (dayOfWeek == 7) 0 else dayOfWeek
    }

    // Calcular límites para los botones
    val isFirstMonth = month == java.time.Month.JANUARY
    val isLastMonth = month == java.time.Month.DECEMBER

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Encabezado del mes
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Botón para mes anterior
            IconButton(
                onClick = {
                    if (!isFirstMonth) {
                        // Si no es enero, simplemente retrocedemos un mes
                        month = month.minus(1)
                    } else {
                        // Si es enero, vamos a diciembre del año anterior
                        month = java.time.Month.DECEMBER
                        year -= 1
                    }
                },
                // Desactivar el botón si estamos en el mes mínimo o anterior
                enabled = minDate == null ||
                        year > minDate.year ||
                        (year == minDate.year && month.value > minDate.month.value)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    "Mes anterior",
                    tint = if (minDate == null ||
                        year > minDate.year ||
                        (year == minDate.year && month.value > minDate.month.value))
                        LocalContentColor.current
                    else
                        LocalContentColor.current.copy(alpha = 0.38f)
                )
            }

            // Nombre del mes y año
            Text(
                text = "${month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} $year",
                style = MaterialTheme.typography.titleMedium
            )

            // Botón para mes siguiente
            IconButton(
                onClick = {
                    if (!isLastMonth) {
                        // Si no es diciembre, simplemente avanzamos un mes
                        month = month.plus(1)
                    } else {
                        // Si es diciembre, vamos a enero del año siguiente
                        month = java.time.Month.JANUARY
                        year += 1
                    }
                },
                enabled = !isLastMonth || year < 2030
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    "Mes siguiente",
                    tint = if (!isLastMonth || year < 2030)
                        LocalContentColor.current
                    else
                        LocalContentColor.current.copy(alpha = 0.38f)
                )
            }
        }

        // Días de la semana
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Usar Dom, Lun, Mar, etc.
            listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.width(36.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Días del mes
        val days = (1..daysInMonth).toList()
        val weeks = mutableListOf<List<Int?>>()
        var currentWeek = mutableListOf<Int?>()

        // Añadir espacios vacíos para el primer día
        // CORRECCIÓN: La lógica para colocar los días debe coincidir con CalendarScreen
        val spacesToAdd = when (firstDayOfMonth) {
            0 -> 0 // Si es domingo (0), no añadir espacios
            else -> firstDayOfMonth // Si no, añadir espacios según el día de la semana
        }

        repeat(spacesToAdd) {
            currentWeek.add(null)
        }

        // Añadir días
        for (day in days) {
            currentWeek.add(day)
            if (currentWeek.size == 7) {
                weeks.add(currentWeek)
                currentWeek = mutableListOf()
            }
        }

        // Completar la última semana si es necesario
        if (currentWeek.isNotEmpty()) {
            while (currentWeek.size < 7) {
                currentWeek.add(null)
            }
            weeks.add(currentWeek)
        }

        // Mostrar semanas
        weeks.forEach { week ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                week.forEach { day ->
                    val currentDate = if (day != null) LocalDate.of(year, month, day) else null
                    val isDateDisabled = day != null && minDate != null &&
                            LocalDate.of(year, month, day).isBefore(minDate)

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (day != null &&
                                    day == selectedDate.dayOfMonth &&
                                    month == selectedDate.month &&
                                    year == selectedDate.year
                                ) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    Color.Transparent
                                }
                            )
                            .clickable(
                                enabled = day != null && !isDateDisabled
                            ) {
                                if (day != null && !isDateDisabled) {
                                    // Crear nueva fecha y notificar
                                    val newDate = LocalDate.of(year, month, day)
                                    onDateSelected(newDate)
                                }
                            }
                    ) {
                        if (day != null) {
                            Text(
                                text = day.toString(),
                                color = when {
                                    // Día seleccionado
                                    day == selectedDate.dayOfMonth &&
                                            month == selectedDate.month &&
                                            year == selectedDate.year -> Color.White
                                    // Día deshabilitado
                                    isDateDisabled -> Color.Gray.copy(alpha = 0.5f)
                                    // Día normal
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorPickerDialog(
    selectedColorIndex: Int,
    onColorSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = if (isSystemInDarkTheme())
        DarkThemeScheduleColors
    else
        LightThemeScheduleColors

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Seleccionar color",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Grid de colores
                Column {
                    colors.chunked(4).forEachIndexed { rowIndex, rowColors ->
                        Row {
                            rowColors.forEachIndexed { colIndex, color ->
                                val index = rowIndex * 4 + colIndex
                                ColorItem(
                                    color = color,
                                    isSelected = selectedColorIndex == index,
                                    onClick = { onColorSelected(index) }
                                )
                            }
                        }
                    }
                }

                // Botones de acción
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}

@Composable
fun ColorItem(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .padding(4.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, Color.Black, CircleShape)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Seleccionado",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@SuppressLint("NewApi")
fun formatDate(date: LocalDate): String {
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es", "ES"))
    val month = date.month
    return "$dayOfWeek, ${date.dayOfMonth} de $month"
}