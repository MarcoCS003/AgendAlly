package com.example.academically.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.academically.data.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.example.academically.data.api.Channel
import com.example.academically.data.api.ChannelType
import com.example.academically.data.api.EventInstituteBlog
import com.example.academically.data.api.Organization

class BlogEventsViewModel(
    private val apiService: ApiService
) : ViewModel() {

    // Estados del ViewModel
    private val _events = MutableStateFlow<List<EventInstituteBlog>>(emptyList())
    val events: StateFlow<List<EventInstituteBlog>> = _events.asStateFlow()

    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    private val _organizations = MutableStateFlow<List<Organization>>(emptyList())
    val organizations: StateFlow<List<Organization>> = _organizations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedChannelId = MutableStateFlow<Int?>(null)
    val selectedChannelId: StateFlow<Int?> = _selectedChannelId.asStateFlow()

    private val _selectedOrganizationId = MutableStateFlow<Int?>(null)
    val selectedOrganizationId: StateFlow<Int?> = _selectedOrganizationId.asStateFlow()

    init {
        // Cargar todos los datos al inicializar
        loadAllEvents()
        loadAllChannels()
        loadAllOrganizations()
    }

    /**
     * Cargar todos los eventos disponibles
     */
    fun loadAllEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                Log.d("BlogEventsViewModel", "🚀 Cargando todos los eventos...")

                apiService.getAllEvents().fold(
                    onSuccess = { eventsList ->
                        _events.value = eventsList
                        _selectedChannelId.value = null
                        _selectedOrganizationId.value = null
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
     * Cargar todos los canales disponibles
     */
    fun loadAllChannels() {
        viewModelScope.launch {
            try {
                Log.d("BlogEventsViewModel", "📺 Cargando todos los canales...")

                apiService.getAllChannels().fold(
                    onSuccess = { channelsList ->
                        _channels.value = channelsList
                        Log.d("BlogEventsViewModel", "✅ Canales cargados: ${channelsList.size}")
                    },
                    onFailure = { exception ->
                        Log.e("BlogEventsViewModel", "❌ Error cargando canales", exception)
                    }
                )
            } catch (e: Exception) {
                Log.e("BlogEventsViewModel", "❌ Error inesperado cargando canales", e)
            }
        }
    }

    /**
     * Cargar todas las organizaciones disponibles
     */
    fun loadAllOrganizations() {
        viewModelScope.launch {
            try {
                Log.d("BlogEventsViewModel", "🏢 Cargando todas las organizaciones...")

                apiService.getAllOrganizations().fold(
                    onSuccess = { organizationsList ->
                        _organizations.value = organizationsList
                        Log.d("BlogEventsViewModel", "✅ Organizaciones cargadas: ${organizationsList.size}")
                    },
                    onFailure = { exception ->
                        Log.e("BlogEventsViewModel", "❌ Error cargando organizaciones", exception)
                    }
                )
            } catch (e: Exception) {
                Log.e("BlogEventsViewModel", "❌ Error inesperado cargando organizaciones", e)
            }
        }
    }

    /**
     * Cargar eventos de un canal específico
     */
    fun loadEventsByChannel(channelId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                Log.d("BlogEventsViewModel", "📺 Cargando eventos del canal $channelId...")

                apiService.getEventsByChannel(channelId).fold(
                    onSuccess = { eventsList ->
                        _events.value = eventsList
                        _selectedChannelId.value = channelId
                        _selectedOrganizationId.value = null
                        Log.d("BlogEventsViewModel", "✅ Eventos del canal cargados: ${eventsList.size}")
                    },
                    onFailure = { exception ->
                        _errorMessage.value = "Error al cargar eventos del canal: ${exception.message}"
                        Log.e("BlogEventsViewModel", "❌ Error cargando eventos del canal", exception)
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
     * Cargar eventos de una organización específica
     */
    fun loadEventsByOrganization(organizationId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                Log.d("BlogEventsViewModel", "🏢 Cargando eventos de la organización $organizationId...")

                apiService.getEventsByOrganization(organizationId).fold(
                    onSuccess = { response ->
                        _events.value = response.events
                        _selectedOrganizationId.value = organizationId
                        _selectedChannelId.value = null
                        Log.d("BlogEventsViewModel", "✅ Eventos de la organización cargados: ${response.events.size}")
                    },
                    onFailure = { exception ->
                        _errorMessage.value = "Error al cargar eventos de la organización: ${exception.message}"
                        Log.e("BlogEventsViewModel", "❌ Error cargando eventos de la organización", exception)
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
                    onSuccess = { response ->
                        _events.value = response.events
                        _selectedChannelId.value = null
                        _selectedOrganizationId.value = null
                        Log.d("BlogEventsViewModel", "✅ Búsqueda completada: ${response.events.size} resultados")
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
     * Filtrar eventos por tipo de canal
     */
    fun filterByChannelType(channelType: ChannelType) {
        val filteredChannels = _channels.value.filter { it.type == channelType }
        val filteredChannelIds = filteredChannels.map { it.id }

        val currentEvents = _events.value
        val filteredEvents = currentEvents.filter { event ->
            event.channelId != null && event.channelId in filteredChannelIds
        }

        _events.value = filteredEvents
        _selectedChannelId.value = null
        _selectedOrganizationId.value = null

        Log.d("BlogEventsViewModel", "🏷️ Filtrado por tipo de canal '$channelType': ${filteredEvents.size} eventos")
    }

    /**
     * Obtener canales por tipo
     */
    fun getChannelsByType(channelType: ChannelType): List<Channel> {
        return _channels.value.filter { it.type == channelType }
    }

    /**
     * Obtener canales por organización
     */
    fun getChannelsByOrganization(organizationId: Int): List<Channel> {
        return _channels.value.filter { it.organizationId == organizationId }
    }

    /**
     * Obtener canal por ID
     */
    fun getChannelById(channelId: Int): Channel? {
        return _channels.value.find { it.id == channelId }
    }

    /**
     * Obtener organización por ID
     */
    fun getOrganizationById(organizationId: Int): Organization? {
        return _organizations.value.find { it.organizationID == organizationId }
    }

    /**
     * Obtener evento específico por ID
     */
    fun getEventById(eventId: Int): EventInstituteBlog? {
        return _events.value.find { it.id == eventId }
    }

    /**
     * Limpiar filtros y mostrar todos los eventos
     */
    fun clearFilters() {
        _selectedChannelId.value = null
        _selectedOrganizationId.value = null
        loadAllEvents()
    }

    /**
     * Limpiar mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Refrescar datos
     */
    fun refreshData() {
        val currentChannelId = _selectedChannelId.value
        val currentOrganizationId = _selectedOrganizationId.value

        when {
            currentChannelId != null -> loadEventsByChannel(currentChannelId)
            currentOrganizationId != null -> loadEventsByOrganization(currentOrganizationId)
            else -> loadAllEvents()
        }

        loadAllChannels()
        loadAllOrganizations()
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