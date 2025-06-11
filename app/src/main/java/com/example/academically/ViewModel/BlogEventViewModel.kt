package com.example.academically.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.academically.data.api.ApiService
import com.example.academically.data.api.BlogEventsResponse
import com.example.academically.data.api.EventInstituteBlog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

class BlogEventsViewModel(
    private val apiService: ApiService
) : ViewModel() {

    // Estados del ViewModel
    private val _events = MutableStateFlow<List<EventInstituteBlog>>(emptyList())
    val events: StateFlow<List<EventInstituteBlog>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedInstitute = MutableStateFlow<Int?>(null)
    val selectedInstitute: StateFlow<Int?> = _selectedInstitute.asStateFlow()

    init {
        // Cargar todos los eventos al inicializar
        loadAllEvents()
    }

    /**
     * Cargar todos los eventos del blog
     */
    fun loadAllEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                Log.d("BlogEventsViewModel", "🚀 Cargando todos los eventos...")

                apiService.getAllBlogEvents().fold(
                    onSuccess = { eventsList ->
                        _events.value = eventsList
                        _selectedInstitute.value = null
                        Log.d("BlogEventsViewModel", "✅ Eventos cargados: ${eventsList.size}")
                    },
                    onFailure = { exception ->
                        _errorMessage.value = "Error al cargar eventos: ${exception.message}"
                        Log.e("BlogEventsViewModel", "❌ Error cargando eventos", exception)
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error inesperado: ${e.message}"
                Log.e("BlogEventsViewModel", "❌ Error inesperado", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Cargar eventos de un instituto específico
     */
    fun loadEventsByInstitute(instituteId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                Log.d("BlogEventsViewModel", "🏢 Cargando eventos del instituto $instituteId...")

                apiService.getEventsByInstitute(instituteId).fold(
                    onSuccess = { response ->
                        _events.value = response.events
                        _selectedInstitute.value = instituteId
                        Log.d("BlogEventsViewModel", "✅ Eventos del instituto cargados: ${response.events.size}")
                    },
                    onFailure = { exception ->
                        _errorMessage.value = "Error al cargar eventos del instituto: ${exception.message}"
                        Log.e("BlogEventsViewModel", "❌ Error cargando eventos del instituto", exception)
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error inesperado: ${e.message}"
                Log.e("BlogEventsViewModel", "❌ Error inesperado", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Buscar eventos por término de búsqueda
     */
    fun searchEvents(query: String) {
        if (query.isBlank()) {
            loadAllEvents()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                Log.d("BlogEventsViewModel", "🔍 Buscando eventos: '$query'")

                apiService.searchEvents(query).fold(
                    onSuccess = { eventsList ->
                        _events.value = eventsList
                        _selectedInstitute.value = null
                        Log.d("BlogEventsViewModel", "✅ Búsqueda completada: ${eventsList.size} resultados")
                    },
                    onFailure = { exception ->
                        _errorMessage.value = "Error en búsqueda: ${exception.message}"
                        Log.e("BlogEventsViewModel", "❌ Error en búsqueda", exception)
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error inesperado en búsqueda: ${e.message}"
                Log.e("BlogEventsViewModel", "❌ Error inesperado en búsqueda", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Filtrar eventos por categoría
     */
    fun filterByCategory(category: String) {
        val currentEvents = _events.value
        if (currentEvents.isEmpty()) {
            // Si no hay eventos cargados, cargar todos primero
            loadAllEvents()
            return
        }

        val filteredEvents = when (category.uppercase()) {
            "ALL" -> currentEvents
            "INSTITUTIONAL" -> currentEvents.filter { it.category == "INSTITUTIONAL" }
            "CAREER" -> currentEvents.filter { it.category == "CAREER" }
            "PERSONAL" -> currentEvents.filter { it.category == "PERSONAL" }
            else -> currentEvents
        }

        _events.value = filteredEvents
        Log.d("BlogEventsViewModel", "🏷️ Filtrado por categoría '$category': ${filteredEvents.size} eventos")
    }

    /**
     * Obtener evento específico por ID
     */
    fun getEventById(eventId: Int, onResult: (EventInstituteBlog?) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("BlogEventsViewModel", "📄 Obteniendo evento $eventId...")

                apiService.getEventById(eventId).fold(
                    onSuccess = { event ->
                        Log.d("BlogEventsViewModel", "✅ Evento obtenido: ${event.title}")
                        onResult(event)
                    },
                    onFailure = { exception ->
                        Log.e("BlogEventsViewModel", "❌ Error obteniendo evento", exception)
                        onResult(null)
                    }
                )
            } catch (e: Exception) {
                Log.e("BlogEventsViewModel", "❌ Error inesperado obteniendo evento", e)
                onResult(null)
            }
        }
    }

    /**
     * Limpiar mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Refrescar eventos (pull to refresh)
     */
    fun refreshEvents() {
        val currentInstitute = _selectedInstitute.value
        if (currentInstitute != null) {
            loadEventsByInstitute(currentInstitute)
        } else {
            loadAllEvents()
        }
    }

    override fun onCleared() {
        super.onCleared()
        apiService.close()
    }

    // Factory para crear el ViewModel
    class Factory(private val apiService: ApiService) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BlogEventsViewModel::class.java)) {
                return BlogEventsViewModel(apiService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}