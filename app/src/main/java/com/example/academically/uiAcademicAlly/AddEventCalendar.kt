package com.example.academically.uiAcademicAlly

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.academically.ui.theme.ScheduleColorsProvider
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*


@SuppressLint("NewApi")
@Composable
fun AddEventScreen(
    onBack: () -> Unit = {},
    onAddEvent: (String, LocalDate, String, Color, String) -> Unit = { _, _, _, _, _ -> }
) {
    var title by remember { mutableStateOf("") }
    var selectedEndDay by remember { mutableStateOf(LocalDate.now()) }
    var selectedColor by remember { mutableStateOf(Color(0xFF2196F3)) } // Azul por defecto
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    // fechas
    var showDatePickerStart by remember { mutableStateOf(false) }
    var showDatePickerEnd by remember { mutableStateOf(false) }
    // fecha inicio
    var selectedCreateStartDay by remember { mutableStateOf(LocalDate.now()) }
    var selectedCreateStartEnd by remember { mutableStateOf(LocalDate.now()) }

    var showColorPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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

        // Selección de fecha
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Row (modifier = Modifier.clickable { showDatePickerStart = true })
            {
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
            Spacer(modifier = Modifier.width(20.dp))
            // EndDay
            Row (modifier = Modifier.clickable { showDatePickerEnd = true }){
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Fecha",
                    modifier = Modifier.size(32.dp)
                )

                Text(
                    text = selectedCreateStartEnd.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
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

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(selectedColor)
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

    // Diálogo de selección de fecha
    if (showDatePickerStart) {
        DatePickerDialog(
            selectedDate = selectedCreateStartDay,
            onDateSelected = {
                selectedCreateStartDay = it
                showDatePickerStart = false
            },
            onDismiss = { showDatePickerStart = false }
        )
    }

    if (showDatePickerEnd) {
        DatePickerDialog(
            selectedDate = selectedEndDay,
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
            selectedColor = selectedColor,
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
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val currentMonth = remember { selectedDate.month }
    val currentYear = remember { selectedDate.year }

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
                    text = "${selectedDate.dayOfMonth} de ${selectedDate.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Calendario simplificado (en producción deberías usar DatePicker de Material3)
                SimpleCalendarView(
                    currentMonth = currentMonth,
                    currentYear = currentYear,
                    selectedDate = selectedDate,
                    onDateSelected = onDateSelected
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

                    TextButton(onClick = { onDateSelected(selectedDate) }) {
                        Text("Aceptar")
                    }
                }
            }
        }
    }
}

/**
 * Vista simplificada de calendario
 */@SuppressLint("NewApi")
@Composable
fun SimpleCalendarView(
    currentMonth: java.time.Month,
    currentYear: Int,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    // Usamos estado mutable para poder cambiar el mes y año
    var month by remember { mutableStateOf(currentMonth) }
    var year by remember { mutableStateOf(currentYear) }

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
                enabled = !isFirstMonth || year > 2020 // Permitir retroceder si no es enero o si año > 2020
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    "Mes anterior",
                    tint = if (!isFirstMonth || year > 2020)
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
                enabled = !isLastMonth || year < 2030 // Permitir avanzar si no es diciembre o si año < 2030
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
                            .clickable(enabled = day != null) {
                                if (day != null) {
                                    // Crear nueva fecha y notificar
                                    val newDate = LocalDate.of(year, month, day)
                                    onDateSelected(newDate)
                                }
                            }
                    ) {
                        if (day != null) {
                            Text(
                                text = day.toString(),
                                color = if (day == selectedDate.dayOfMonth &&
                                    month == selectedDate.month &&
                                    year == selectedDate.year) {
                                    Color.White
                                } else {
                                    MaterialTheme.colorScheme.onSurface
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
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val availableColors = ScheduleColorsProvider.getColors()
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
                    availableColors.chunked(4).forEach { rowColors ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowColors.forEach { color ->
                                ColorItem(
                                    color = color,
                                    isSelected = selectedColor == color,
                                    onClick = { onColorSelected(color) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
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

@SuppressLint("NewApi")
@Preview(showBackground = true)
@Composable
fun DatePickerDialogPreview() {
    MaterialTheme {
        DatePickerDialog(
            selectedDate = LocalDate.now(),
            onDateSelected = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ColorPickerDialogPreview() {
    MaterialTheme {
        ColorPickerDialog(
            selectedColor = Color(0xFF2196F3),
            onColorSelected = {},
            onDismiss = {}
        )
    }
}