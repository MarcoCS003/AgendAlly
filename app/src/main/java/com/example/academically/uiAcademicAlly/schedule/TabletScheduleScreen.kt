package com.example.academically.uiAcademicAlly.schedule

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academically.ViewModel.ScheduleViewModel
import com.example.academically.ViewModel.ScheduleViewModelFactory
import com.example.academically.data.model.Schedule
import com.example.academically.data.local.database.AcademicAllyDatabase
import com.example.academically.data.repositorty.ScheduleRepository

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TabletScheduleScreen(
    viewModel: ScheduleViewModel = viewModel(
        factory = ScheduleViewModelFactory(
            ScheduleRepository(
                AcademicAllyDatabase.getDatabase(LocalContext.current).scheduleDao()
            )
        )
    )
) {
    var showAddScheduleForm by remember { mutableStateOf(false) }
    var editingSchedule by remember { mutableStateOf<Schedule?>(null) }

    Row(modifier = Modifier.fillMaxSize()) {
        // Panel principal del horario
        Box(
            modifier = Modifier
                .weight(if (showAddScheduleForm || editingSchedule != null) 0.6f else 1f)
                .fillMaxHeight()
        ) {
            TabletScheduleContent(
                viewModel = viewModel,
                onAddActivity = { showAddScheduleForm = true },
                onEditActivity = { schedule -> editingSchedule = schedule }
            )
        }

        // Panel lateral para formularios
        if (showAddScheduleForm || editingSchedule != null) {
            Card(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),

            ) {
                Column {

                    // Contenido del formulario
                    if (editingSchedule != null) {
                        EditScheduleActivityScreen(
                            scheduleId = editingSchedule!!.id,
                            viewModel = viewModel,
                            onNavigateBack = { editingSchedule = null }
                        )
                    } else {
                        AddScheduleActivityScreenWithViewModel(
                            viewModel = viewModel,
                            onNavigateBack = { showAddScheduleForm = false }
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TabletScheduleContent(
    viewModel: ScheduleViewModel,
    onAddActivity: () -> Unit,
    onEditActivity: (Schedule) -> Unit
) {
    val schedules by viewModel.schedules.collectAsState()
    val currentDate by viewModel.currentDate.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
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

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        ScheduleScreen(
            schedules = schedules,
            currentDate = currentDate,
            viewMode = viewMode,
            onAddActivity = onAddActivity,
            onEditSchedule = onEditActivity,
            onDeleteSchedule = { schedule ->
                viewModel.deleteSchedule(schedule)
            },
            onViewModeChange = { viewModel.changeViewMode(it) },
            onNextDay = { viewModel.navigateToNextDay() },
            onPreviousDay = { viewModel.navigateToPreviousDay() },
            onNextWeek = { viewModel.navigateToNextWeek() },
            onPreviousWeek = { viewModel.navigateToPreviousWeek() }
        )
    }
}