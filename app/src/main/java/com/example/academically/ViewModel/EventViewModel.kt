package com.example.academically.ViewModel


import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.academically.data.PersonalEvent
import com.example.academically.data.repository.PersonalEventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(VERSION_CODES.O)
class EventViewModel(private val eventRepository: PersonalEventRepository) : ViewModel() {

    // Estado para todos los eventos
    private val _allEvents = MutableStateFlow<List<PersonalEvent>>(emptyList())
    val allEvents: StateFlow<List<PersonalEvent>> = _allEvents.asStateFlow()

    // Estado para un evento específico
    private val _selectedEvent = MutableStateFlow<PersonalEvent?>(null)
    val selectedEvent: StateFlow<PersonalEvent?> = _selectedEvent.asStateFlow()

    // Estado para eventos filtrados por fecha
    private val _filteredEvents = MutableStateFlow<List<PersonalEvent>>(emptyList())
    val filteredEvents: StateFlow<List<PersonalEvent>> = _filteredEvents.asStateFlow()

    // Estado para el mes y año actual
    private val _currentYearMonth = MutableStateFlow(YearMonth.now())
    val currentYearMonth: StateFlow<YearMonth> = _currentYearMonth.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Inicializar el ViewModel


    init {
        fetchAllEvents()
    }

    // Cargar todos los eventos
    @RequiresApi(VERSION_CODES.O)
    fun fetchAllEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                eventRepository.allEvents.collectLatest { events ->
                    _allEvents.value = events
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar eventos: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Cargar un evento específico por ID
    @RequiresApi(VERSION_CODES.O)
    fun fetchEventById(eventId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                eventRepository.getEventById(eventId).collectLatest { event ->
                    _selectedEvent.value = event
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar el evento: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Cargar eventos entre fechas
    @RequiresApi(VERSION_CODES.O)
    fun fetchEventsBetweenDates(startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                eventRepository.getEventsBetweenDates(startDate, endDate).collectLatest { events ->
                    _filteredEvents.value = events
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al filtrar eventos: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Cambiar al mes anterior
    @RequiresApi(VERSION_CODES.O)
    fun previousMonth() {
        _currentYearMonth.value = _currentYearMonth.value.minusMonths(1)
        updateEventsForCurrentMonth()
    }

    // Cambiar al mes siguiente
    @RequiresApi(VERSION_CODES.O)
    fun nextMonth() {
        _currentYearMonth.value = _currentYearMonth.value.plusMonths(1)
        updateEventsForCurrentMonth()
    }

    // Actualizar eventos para el mes actual
    @RequiresApi(VERSION_CODES.O)
    private fun updateEventsForCurrentMonth() {
        val yearMonth = _currentYearMonth.value
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()
        fetchEventsBetweenDates(startDate, endDate)
    }

    // Insertar un nuevo evento
    @RequiresApi(VERSION_CODES.O)
    fun insertEvent(event: PersonalEvent) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                eventRepository.insertEvent(event)
                // Refrescar la lista después de insertar
                fetchAllEvents()
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error al insertar evento: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Actualizar un evento existente
    @RequiresApi(VERSION_CODES.O)
    fun updateEvent(event: PersonalEvent) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                eventRepository.updateEvent(event)
                // Refrescar la lista después de actualizar
                fetchAllEvents()
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar evento: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Eliminar un evento
    fun deleteEvent(event: PersonalEvent) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                eventRepository.deleteEvent(event)
                // Refrescar la lista después de eliminar
                fetchAllEvents()
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar evento: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Limpiar mensaje de error
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // Factory para crear instancias del ViewModel
    class Factory(private val eventRepository: PersonalEventRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
                return EventViewModel(eventRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}