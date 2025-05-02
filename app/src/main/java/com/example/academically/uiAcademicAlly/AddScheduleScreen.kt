package com.example.academically.uiAcademicAlly


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academically.ViewModel.ScheduleViewModel
import com.example.academically.ViewModel.ScheduleViewModelFactory
import com.example.academically.data.ScheduleTime
import com.example.academically.data.database.AcademicAllyDatabase
import com.example.academically.data.repositorty.ScheduleRepository
import com.example.academically.ui.theme.ScheduleColorsProvider
import java.time.LocalTime
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun AddScheduleActivityScreenWithViewModel(
    viewModel: ScheduleViewModel = viewModel(
        factory = ScheduleViewModelFactory(
            ScheduleRepository(
                AcademicAllyDatabase.getDatabase(LocalContext.current).scheduleDao()
            )
        )
    ),
    onNavigateBack: () -> Unit
) {

    val availableColors = ScheduleColorsProvider.getColors()

    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(availableColors[0]) }
    var teacherName by remember { mutableStateOf("") }
    var customizePerDay by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
    var endTime by remember { mutableStateOf(LocalTime.of(10, 0)) }
    var selectedDays by remember { mutableStateOf(setOf<DaysOfWeek>()) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showDaySelector by remember { mutableStateOf(false) }
    var customTimePerDay by remember { mutableStateOf(mapOf<DaysOfWeek, Pair<LocalTime, LocalTime>>()) }

    val scrollState = rememberScrollState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Mostrar errores si existen
    error?.let { errorMessage ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("Aceptar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Actividad") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Título
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Añadir título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ubicación
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Añadir ubicación") },
                leadingIcon = {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Color
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showColorPicker = true }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(selectedColor)
                        .border(1.dp, Color.LightGray, CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("color predeterminado")
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Profesor
            OutlinedTextField(
                value = teacherName,
                onValueChange = { teacherName = it },
                label = { Text("Añadir profesor o mentor") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Switch para personalizar hora por día
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Personalizar horas por día")
                Switch(
                    checked = customizePerDay,
                    onCheckedChange = { customizePerDay = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Vistas de tiempo
            AnimatedVisibility(
                visible = !customizePerDay,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                SimpleTimeView(
                    startTime = startTime,
                    endTime = endTime,
                    onStartTimeChange = { startTime = it },
                    onEndTimeChange = { endTime = it },
                    selectedDays = selectedDays,
                    onShowDaySelector = { showDaySelector = true }
                )
            }

            AnimatedVisibility(
                visible = customizePerDay,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                CustomizedDayTimeView(
                    customTimePerDay = customTimePerDay,
                    onTimeChange = { day, start, end ->
                        customTimePerDay = customTimePerDay.toMutableMap().apply {
                            put(day, Pair(start, end))
                        }
                    },
                    onDayEnabled = { day, enabled ->
                        customTimePerDay = if (enabled) {
                            customTimePerDay.toMutableMap().apply {
                                put(day, Pair(LocalTime.of(9, 0), LocalTime.of(10, 0)))
                            }
                        } else {
                            customTimePerDay.toMutableMap().apply {
                                remove(day)
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Agregar
            Button(
                onClick = {
                    val scheduleTimes = if (customizePerDay) {
                        customTimePerDay.map { (day, times) ->
                            ScheduleTime(
                                day = day,
                                hourStart = times.first,
                                hourEnd = times.second
                            )
                        }
                    } else {
                        selectedDays.map { day ->
                            ScheduleTime(
                                day = day,
                                hourStart = startTime,
                                hourEnd = endTime
                            )
                        }
                    }

                    viewModel.addSchedule(
                        name = title,
                        place = location,
                        teacher = teacherName,
                        color = selectedColor,
                        times = scheduleTimes
                    )

                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() &&
                        location.isNotBlank() &&
                        teacherName.isNotBlank() &&
                        ((customizePerDay && customTimePerDay.isNotEmpty()) ||
                                (!customizePerDay && selectedDays.isNotEmpty())) &&
                        !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Agregar")
                }
            }
        }
    }

    // Diálogos
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

    if (showDaySelector) {
        DaySelectorDialogImproved(
            selectedDays = selectedDays,
            onDaysSelected = { days ->
                selectedDays = days
            },
            onDismiss = { showDaySelector = false }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SimpleTimeView(
    startTime: LocalTime,
    endTime: LocalTime,
    onStartTimeChange: (LocalTime) -> Unit,
    onEndTimeChange: (LocalTime) -> Unit,
    selectedDays: Set<DaysOfWeek>,
    onShowDaySelector: () -> Unit
) {
    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.height(100.dp)) {
                Text("Hora de inicio", Modifier.padding(vertical = 10.dp))
                TimePickerField(
                    time = startTime,
                    onTimeChange = onStartTimeChange,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(Modifier.height(100.dp)) {
                Text("Hora final", Modifier.padding(vertical = 10.dp))
                TimePickerField(
                    time = endTime,
                    onTimeChange = onEndTimeChange,
                    modifier = Modifier.weight(1f),
                )
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de días
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onShowDaySelector() },
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Schedule, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedDays.isEmpty()) {
                        "Añadir días de la semana"
                    } else {
                        selectedDays.joinToString(", ") { it.name.take(3) }
                    }
                )
            }
        }
    }
}

@Composable
fun DaySelectorDialogImproved(
    selectedDays: Set<DaysOfWeek>,
    onDaysSelected: (Set<DaysOfWeek>) -> Unit,
    onDismiss: () -> Unit
) {
    var tempSelectedDays by remember { mutableStateOf(selectedDays) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Seleccionar días",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                DaysOfWeek.entries.forEach { day ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                tempSelectedDays = if (tempSelectedDays.contains(day)) {
                                    tempSelectedDays - day
                                } else {
                                    tempSelectedDays + day
                                }
                                onDaysSelected(tempSelectedDays)
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = tempSelectedDays.contains(day),
                            onCheckedChange = { checked ->
                                tempSelectedDays = if (checked) {
                                    tempSelectedDays + day
                                } else {
                                    tempSelectedDays - day
                                }
                                onDaysSelected(tempSelectedDays)
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(day.name.lowercase()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() })
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text("Aceptar")
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomizedDayTimeView(
    customTimePerDay: Map<DaysOfWeek, Pair<LocalTime, LocalTime>>,
    onTimeChange: (DaysOfWeek, LocalTime, LocalTime) -> Unit,
    onDayEnabled: (DaysOfWeek, Boolean) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        DaysOfWeek.entries.forEach { day ->
            val isEnabled = customTimePerDay.containsKey(day)
            val timeRange = customTimePerDay[day] ?: Pair(LocalTime.of(9, 0), LocalTime.of(10, 0))

            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = day.name.take(1),
                        modifier = Modifier.width(24.dp),
                        fontWeight = FontWeight.Bold
                    )

                    TimePickerField(
                        time = timeRange.first,
                        onTimeChange = { newTime ->
                            if (isEnabled) {
                                onTimeChange(day, newTime, timeRange.second)
                            }
                        },
                        enabled = isEnabled,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                    )

                    TimePickerField(
                        time = timeRange.second,
                        onTimeChange = { newTime ->
                            if (isEnabled) {
                                onTimeChange(day, timeRange.first, newTime)
                            }
                        },
                        enabled = isEnabled,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                    )

                    Checkbox(
                        checked = isEnabled,
                        onCheckedChange = { onDayEnabled(day, it) }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePickerField(
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var hour by remember { mutableStateOf(time.hour.toString().padStart(2, '0')) }
    var minute by remember { mutableStateOf(time.minute.toString().padStart(2, '0')) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // Campo para la hora
        OutlinedTextField(
            value = hour,
            onValueChange = { newValue ->
                if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                    hour = newValue
                    val hourInt = newValue.toIntOrNull() ?: 0
                    if (hourInt in 0..23) {
                        onTimeChange(LocalTime.of(hourInt, time.minute))
                    }
                }
            },
            modifier = Modifier.width(60.dp),
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        Text(":", modifier = Modifier.padding(horizontal = 4.dp))

        // Campo para los minutos
        OutlinedTextField(
            value = minute,
            onValueChange = { newValue ->
                if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                    minute = newValue
                    val minuteInt = newValue.toIntOrNull() ?: 0
                    if (minuteInt in 0..59) {
                        onTimeChange(LocalTime.of(time.hour, minuteInt))
                    }
                }
            },
            modifier = Modifier.width(60.dp),
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

