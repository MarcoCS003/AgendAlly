package com.example.academically.ViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.academically.data.repositorty.IntegratedRepository
import com.example.academically.data.model.EventOrganization
import com.example.academically.data.model.PersonalEventType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.example.academically.data.model.Organization

@RequiresApi(Build.VERSION_CODES.O)
class BlogEventsViewModel(
    private val integratedRepository: IntegratedRepository
) : ViewModel() {

    // Estados del ViewModel - usando EventOrganization que es el modelo de UI
    private val _events = MutableStateFlow<List<EventOrganization>>(emptyList())
    val events: StateFlow<List<EventOrganization>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedOrganizationId = MutableStateFlow<Int?>(null)
    val selectedOrganizationId: StateFlow<Int?> = _selectedOrganizationId.asStateFlow()

    init {
        // Cargar todos los eventos al inicializar
        loadAllEvents()
    }

    /**
     * Cargar todos los eventos institucionales del servidor
     */
    fun loadAllEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                Log.d("BlogEventsViewModel", "🚀 Cargando todos los eventos institucionales...")

                val result = integratedRepository.getInstitutionalEvents()

                if (result.isSuccess) {
                    val institutionalEvents = result.getOrThrow()
                    // Convertir EventInstitute a EventOrganization para la UI
                    val uiEvents = institutionalEvents
                    _events.value = uiEvents
                    _selectedOrganizationId.value = null
                    Log.d("BlogEventsViewModel", "✅ Eventos cargados: ${uiEvents.size}")
                } else {
                    val errorMsg = "Error al cargar eventos: ${result.exceptionOrNull()?.message}"
                    _errorMessage.value = errorMsg
                    Log.e("BlogEventsViewModel", "❌ $errorMsg")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error inesperado: ${e.message}"
                Log.e("BlogEventsViewModel", "❌ Error inesperado", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Cargar eventos de una organización específica
     */
    fun loadEventsByOrganization(organizationId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                Log.d("BlogEventsViewModel", "🏢 Cargando eventos de la organización $organizationId...")

                val result = integratedRepository.getEventsByOrganization(organizationId)

                if (result.isSuccess) {
                    val institutionalEvents = result.getOrThrow()
                    val uiEvents = institutionalEvents
                    _events.value = uiEvents
                    _selectedOrganizationId.value = organizationId
                    Log.d("BlogEventsViewModel", "✅ Eventos de la organización cargados: ${uiEvents.size}")
                } else {
                    val errorMsg = "Error al cargar eventos de la organización: ${result.exceptionOrNull()?.message}"
                    _errorMessage.value = errorMsg
                    Log.e("BlogEventsViewModel", "❌ $errorMsg")
                }
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

                val result = integratedRepository.searchEvents(query)

                if (result.isSuccess) {
                    val institutionalEvents = result.getOrThrow()
                    val uiEvents = institutionalEvents
                    _events.value = uiEvents
                    _selectedOrganizationId.value = null
                    Log.d("BlogEventsViewModel", "✅ Búsqueda completada: ${uiEvents.size} resultados")
                } else {
                    val errorMsg = "Error en búsqueda: ${result.exceptionOrNull()?.message}"
                    _errorMessage.value = errorMsg
                    Log.e("BlogEventsViewModel", "❌ $errorMsg")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error inesperado en búsqueda: ${e.message}"
                Log.e("BlogEventsViewModel", "❌ Error inesperado en búsqueda", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Filtrar eventos por categoría (mantenido para compatibilidad)
     */
    fun filterByCategory(category: String) {
        val currentEvents = _events.value
        if (currentEvents.isEmpty()) {
            loadAllEvents()
            return
        }

        val filteredEvents = when (category.uppercase()) {
            "ALL" -> currentEvents
            "INSTITUTIONAL" -> currentEvents.filter { it.category == PersonalEventType.SUBSCRIBED }
            "CAREER" -> currentEvents.filter { it.category == PersonalEventType.SUBSCRIBED }
            "DEPARTMENT" -> currentEvents.filter { it.category == PersonalEventType.SUBSCRIBED }
            else -> currentEvents
        }

        _events.value = filteredEvents
        Log.d("BlogEventsViewModel", "🏷️ Filtrado por categoría '$category': ${filteredEvents.size} eventos")
    }

    /**
     * Obtener evento específico por ID
     */
    fun getEventById(eventId: Int, onResult: (EventOrganization?) -> Unit) {
        // Buscar primero en los eventos ya cargados
        val existingEvent = _events.value.find { it.id == eventId }
        if (existingEvent != null) {
            Log.d("BlogEventsViewModel", "✅ Evento encontrado en caché: ${existingEvent.title}")
            onResult(existingEvent)
            return
        }

        // Si no está en caché, podríamos implementar una búsqueda específica
        // Por ahora, retornar null
        Log.w("BlogEventsViewModel", "⚠️ Evento $eventId no encontrado en caché")
        onResult(null)
    }

    /**
     * Refrescar eventos (pull to refresh)
     */
    fun refreshEvents() {
        val currentOrganizationId = _selectedOrganizationId.value
        if (currentOrganizationId != null) {
            loadEventsByOrganization(currentOrganizationId)
        } else {
            loadAllEvents()
        }
    }

    /**
     * Limpiar mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Filtrar eventos por suscripciones del usuario
     */
    fun filterEventsBySubscriptions(subscribedChannelIds: List<Int>) {
        viewModelScope.launch {
            try {
                // TODO: Implementar filtrado por canales suscritos
                // Esto requiere que los eventos tengan información del channelId
                Log.d("BlogEventsViewModel", "🔗 Filtrando por suscripciones: $subscribedChannelIds")

                // Por ahora, mantener eventos actuales
                // En el futuro, filtrar por channelId cuando esté disponible en EventOrganization

            } catch (e: Exception) {
                Log.e("BlogEventsViewModel", "❌ Error filtrando por suscripciones", e)
            }
        }
    }


    // Factory para crear el ViewModel con IntegratedRepository
    class Factory(
        private val integratedRepository: IntegratedRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BlogEventsViewModel::class.java)) {
                return BlogEventsViewModel(integratedRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}