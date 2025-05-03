package com.example.academically.uiAcademicAlly.calendar

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.academically.ui.theme.DarkThemeScheduleColors
import com.example.academically.ui.theme.LightThemeScheduleColors
import com.example.academically.ui.theme.ScheduleColorsProvider
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


@SuppressLint("NewApi")
@Composable
fun AddEventScreen(
    onBack: () -> Unit = {},
    onAddEvent: (String, LocalDate, String, Int, String) -> Unit = { _, _, _, _, _ -> }
) {

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

    val scrollState = rememberScrollState()
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
                    .background(scheduleColors[selectedColor])
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

        // Botón de notificación
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Abrir configuración de notificación */ }
                .padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notificación",
                modifier = Modifier.size(32.dp)
            )

            Text(
                text = "Personalizar notificación",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp)
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
                onAddEvent(title, selectedCreateStartDay, location, selectedColor, description)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Agregar")
        }
    }

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


/**
 * Diálogo para seleccionar una fecha
 */

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


/**
 * Vista simplificada de calendario
 */
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
    val firstDayOfMonth = remember(month, year) {
        LocalDate.of(year, month, 1).dayOfWeek.value
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
            listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom").forEach { day ->
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
        repeat(firstDayOfMonth - 1) {
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

/**
 * Diálogo para seleccionar un color
 */
@Composable
fun ColorPickerDialog(
    selectedColorIndex : Int,
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
                        Row{
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

/**
 * Formatea una fecha para mostrarla
 */
@SuppressLint("NewApi")
fun formatDate(date: LocalDate): String {
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es", "ES"))
    val month = date.month
    return "$dayOfWeek, ${date.dayOfMonth} de $month"
}

@SuppressLint("NewApi")
@Preview(showBackground = true)
@Composable
fun AddEventScreenPreview() {
    MaterialTheme {
        AddEventScreen()
    }
}



@Preview(showBackground = true)
@Composable
fun ColorPickerDialogPreview() {
    MaterialTheme {
        ColorPickerDialog(
            selectedColorIndex = 1 ,
            onColorSelected = {},
            onDismiss = {}
        )
    }
}