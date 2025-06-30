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
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val result = apiService.getAllEvents()
                if (result.isSuccess) {
                    _events.value = result.getOrNull() ?: emptyList()
                    Log.d("BlogEventsViewModel", "Eventos cargados: ${_events.value.size}")
                } else {
                    throw result.exceptionOrNull() ?: Exception("Error desconocido")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar eventos: ${e.message}"
                Log.e("BlogEventsViewModel", "Error cargando eventos", e)
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
                val result = apiService.getAllChannels()
                if (result.isSuccess) {
                    _channels.value = result.getOrNull() ?: emptyList()
                    Log.d("BlogEventsViewModel", "Canales cargados: ${_channels.value.size}")
                } else {
                    Log.e("BlogEventsViewModel", "Error cargando canales: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e("BlogEventsViewModel", "Error cargando canales", e)
                // No mostramos error de canales al usuario ya que no es crítico
            }
        }
    }

    /**
     * ✅ NUEVO: Cargar todas las organizaciones disponibles
     */
    fun loadAllOrganizations() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val result = apiService.getAllOrganizations()
                if (result.isSuccess) {
                    _organizations.value = result.getOrNull() ?: emptyList()
                    Log.d("BlogEventsViewModel", "Organizaciones cargadas: ${_organizations.value.size}")
                } else {
                    throw result.exceptionOrNull() ?: Exception("Error desconocido")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar organizaciones: ${e.message}"
                Log.e("BlogEventsViewModel", "Error cargando organizaciones", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * ✅ NUEVO: Buscar organizaciones por query
     */
    fun searchOrganizations(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val result = apiService.searchOrganizations(query)
                if (result.isSuccess) {
                    _organizations.value = result.getOrNull() ?: emptyList()
                    Log.d("BlogEventsViewModel", "Búsqueda '$query': ${_organizations.value.size} resultados")
                } else {
                    throw result.exceptionOrNull() ?: Exception("Error desconocido")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error en la búsqueda: ${e.message}"
                Log.e("BlogEventsViewModel", "Error en búsqueda de organizaciones", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * ✅ NUEVO: Obtener una organización específica por ID
     */
    fun getOrganizationById(organizationId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val result = apiService.getOrganizationById(organizationId)
                if (result.isSuccess) {
                    val organization = result.getOrNull()
                    if (organization != null) {
                        // Actualizar la lista con la organización obtenida
                        val currentList = _organizations.value.toMutableList()
                        val existingIndex = currentList.indexOfFirst { it.organizationID == organizationId }

                        if (existingIndex != -1) {
                            currentList[existingIndex] = organization
                        } else {
                            currentList.add(organization)
                        }

                        _organizations.value = currentList
                        Log.d("BlogEventsViewModel", "Organización $organizationId cargada: ${organization.name}")
                    }
                } else {
                    throw result.exceptionOrNull() ?: Exception("Error desconocido")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar organización: ${e.message}"
                Log.e("BlogEventsViewModel", "Error cargando organización $organizationId", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Buscar eventos por query
     */
    fun searchEvents(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val result = apiService.searchEvents(query)
                if (result.isSuccess) {
                    _events.value = result.getOrNull() ?: emptyList()
                    Log.d("BlogEventsViewModel", "Búsqueda eventos '$query': ${_events.value.size} resultados")
                } else {
                    throw result.exceptionOrNull() ?: Exception("Error desconocido")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error en la búsqueda de eventos: ${e.message}"
                Log.e("BlogEventsViewModel", "Error en búsqueda de eventos", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterEventsByChannel(channelId: Int?) {
        _selectedChannelId.value = channelId

        if (channelId == null) {
            // Mostrar todos los eventos
            loadAllEvents()
        } else {
            viewModelScope.launch {
                try {
                    _isLoading.value = true
                    _errorMessage.value = null

                    val result = apiService.getEventsByChannel(channelId)
                    if (result.isSuccess) {
                        _events.value = result.getOrNull() ?: emptyList()
                        Log.d("BlogEventsViewModel", "Eventos del canal $channelId: ${_events.value.size}")
                    } else {
                        throw result.exceptionOrNull() ?: Exception("Error desconocido")
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Error al filtrar eventos: ${e.message}"
                    Log.e("BlogEventsViewModel", "Error filtrando eventos por canal", e)
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }
    /**
     * Filtrar eventos por organizacion
     */
    fun filterEventsByOrganization(organizationId: Int?) {
        _selectedOrganizationId.value = organizationId

        if (organizationId == null) {
            // Mostrar todos los eventos
            loadAllEvents()
        } else {
            viewModelScope.launch {
                try {
                    _isLoading.value = true
                    _errorMessage.value = null

                    val result = apiService.getEventsByOrganization(organizationId)
                    if (result.isSuccess) {
                        _events.value = result.getOrNull() ?: emptyList()
                        Log.d("BlogEventsViewModel", "Eventos de organización $organizationId: ${_events.value.size}")
                    } else {
                        throw result.exceptionOrNull() ?: Exception("Error desconocido")
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Error al filtrar eventos: ${e.message}"
                    Log.e("BlogEventsViewModel", "Error filtrando eventos por organización", e)
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }



    /**
     * Obtener canales por tipo
     */
    fun getChannelsByType(type: ChannelType): List<Channel> {
        return _channels.value.filter { it.type == type }
    }

    /**
     * Obtener canal por ID
     */
    fun getChannelById(channelId: Int): Channel? {
        return _channels.value.find { it.id == channelId }
    }

    /**
     * ✅ NUEVO: Obtener organización por ID desde el estado
     */
    fun getOrganizationFromState(organizationId: Int): Organization? {
        return _organizations.value.find { it.organizationID == organizationId }
    }

    /**
     * Limpiar filtros
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
     * Refrescar todos los datos
     */
    fun refresh() {
        loadAllEvents()
        loadAllChannels()
        loadAllOrganizations()
    }

    // ========== FACTORY ==========

    class Factory(
        private val apiService: ApiService
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BlogEventsViewModel::class.java)) {
                return BlogEventsViewModel(apiService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}