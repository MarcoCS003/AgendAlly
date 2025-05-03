package com.example.academically.uiAcademicAlly.schedule

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academically.ViewModel.ScheduleViewModel
import com.example.academically.ViewModel.ScheduleViewModelFactory
import com.example.academically.data.ScheduleTime
import com.example.academically.data.database.AcademicAllyDatabase
import com.example.academically.data.repositorty.ScheduleRepository
import com.example.academically.uiAcademicAlly.calendar.ColorPickerDialog
import com.example.academically.uiAcademicAlly.calendar.DaysOfWeek
import java.time.LocalTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun EditScheduleActivityScreen(
    scheduleId: Int,
    viewModel: ScheduleViewModel = viewModel(
        factory = ScheduleViewModelFactory(
            ScheduleRepository(
                AcademicAllyDatabase.getDatabase(LocalContext.current).scheduleDao()
            )
        )
    ),
    onNavigateBack: () -> Unit
) {
    val schedules by viewModel.schedules.collectAsState()
    val schedule = remember(schedules, scheduleId) {
        schedules.find { it.id == scheduleId }
    }

    if (schedule == null) {
        // Si no se encuentra el horario, mostrar un mensaje y volver
        LaunchedEffect(Unit) {
            onNavigateBack()
        }
        return
    }

    var title by remember { mutableStateOf(schedule.name) }
    var location by remember { mutableStateOf(schedule.place) }
    var selectedColor by remember { mutableStateOf(schedule.colorIndex) }
    var teacherName by remember { mutableStateOf(schedule.teacher) }
    var customizePerDay by remember {
        mutableStateOf(
            schedule.times.distinctBy { it.day }.size != schedule.times.size ||
                    schedule.times.any { time ->
                        time.hourStart != schedule.times.first().hourStart ||
                                time.hourEnd != schedule.times.first().hourEnd
                    }
        )
    }

    var startTime by remember {
        mutableStateOf(
            if (customizePerDay) LocalTime.of(9, 0) else schedule.times.first().hourStart
        )
    }

    var endTime by remember {
        mutableStateOf(
            if (customizePerDay) LocalTime.of(10, 0) else schedule.times.first().hourEnd
        )
    }

    var selectedDays by remember {
        mutableStateOf(if (customizePerDay) setOf<DaysOfWeek>() else schedule.times.map { it.day }.toSet())
    }

    var customTimePerDay by remember {
        mutableStateOf(
            if (customizePerDay) {
                schedule.times.associate {
                    it.day to Pair(it.hourStart, it.hourEnd)
                }
            } else {
                mapOf<DaysOfWeek, Pair<LocalTime, LocalTime>>()
            }
        )
    }

    var showColorPicker by remember { mutableStateOf(false) }
    var showDaySelector by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

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
                title = { Text("Editar Actividad") },
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
            // Campos del formulario (igual que en AddScheduleActivityScreen)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Ubicación") },
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
                        .background(schedule.getColor())
                        .border(1.dp, Color.LightGray, CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("color predeterminado")
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = teacherName,
                onValueChange = { teacherName = it },
                label = { Text("Profesor o mentor") },
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

            // Vistas de tiempo (igual que en AddScheduleActivityScreen)
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

            // Botón Guardar
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

                    val updatedSchedule = schedule.copy(
                        name = title,
                        place = location,
                        teacher = teacherName,
                        colorIndex = selectedColor,
                        times = scheduleTimes
                    )

                    viewModel.updateSchedule(updatedSchedule)
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
                    Text("Guardar")
                }
            }
        }
    }

    // Diálogos
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