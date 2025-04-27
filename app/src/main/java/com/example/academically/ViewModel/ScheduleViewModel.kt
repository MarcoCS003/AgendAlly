package com.example.academically.ViewModel


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.academically.data.Schedule
import com.example.academically.data.ScheduleTime
import com.example.academically.data.repositorty.ScheduleRepository
import com.example.academically.uiAcademicAlly.ViewMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class ScheduleViewModel(private val repository: ScheduleRepository) : ViewModel() {

    // Estado de la pantalla
    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules: StateFlow<List<Schedule>> = _schedules.asStateFlow()

    private val _viewMode = MutableStateFlow(ViewMode.DAILY)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()

    private val _currentDate = MutableStateFlow(LocalDate.now())
    val currentDate: StateFlow<LocalDate> = _currentDate.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadSchedules()
    }

    private fun loadSchedules() {
        viewModelScope.launch {
            try {
                repository.allSchedulesWithTimes.collect { scheduleList ->
                    _schedules.value = scheduleList
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar horarios: ${e.message}"
                Log.e("ScheduleViewModel", "Error loading schedules", e)
            }
        }
    }

    fun addSchedule(
        name: String,
        place: String,
        teacher: String,
        color: Color,
        times: List<ScheduleTime>
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newSchedule = Schedule(
                    id = 0, // Room generará el ID
                    name = name,
                    place = place,
                    teacher = teacher,
                    color = color,
                    times = times
                )
                repository.insertSchedule(newSchedule)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al agregar horario: ${e.message}"
                Log.e("ScheduleViewModel", "Error adding schedule", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateSchedule(schedule)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al actualizar horario: ${e.message}"
                Log.e("ScheduleViewModel", "Error updating schedule", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            try {
                repository.deleteSchedule(schedule)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al eliminar horario: ${e.message}"
                Log.e("ScheduleViewModel", "Error deleting schedule", e)
            }
        }
    }

    fun changeViewMode(mode: ViewMode) {
        _viewMode.value = mode
    }

    fun navigateToNextDay() {
        _currentDate.value = _currentDate.value.plusDays(1)
    }

    fun navigateToPreviousDay() {
        _currentDate.value = _currentDate.value.minusDays(1)
    }

    fun navigateToNextWeek() {
        _currentDate.value = _currentDate.value.plusWeeks(1)
    }

    fun navigateToPreviousWeek() {
        _currentDate.value = _currentDate.value.minusWeeks(1)
    }

    fun clearError() {
        _error.value = null
    }
}

// Factory para crear el ViewModel
@RequiresApi(Build.VERSION_CODES.O)
class ScheduleViewModelFactory(private val repository: ScheduleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScheduleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}