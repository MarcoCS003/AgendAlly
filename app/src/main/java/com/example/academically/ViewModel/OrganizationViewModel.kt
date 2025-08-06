package com.example.academically.ViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.academically.data.EventAdapter
import com.example.academically.data.api.ApiService
import com.example.academically.data.api.Organization
import com.example.academically.data.entities.SubscriptionEntity
import com.example.academically.data.mappers.toApiModel
import com.example.academically.data.mappers.toEntityWithSubscriptions
import com.example.academically.data.repositorty.OrganizationRepository
import com.example.academically.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class OrganizationViewModel(
    private val repository: OrganizationRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    // ========== STATES ==========

    private val _organizations = MutableStateFlow<List<Organization>>(emptyList())
    val organizations: StateFlow<List<Organization>> = _organizations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _organizationCount = MutableStateFlow(0)
    val organizationCount: StateFlow<Int> = _organizationCount.asStateFlow()

    // ========== INITIALIZATION ==========

    init {
        loadOrganizations()
        loadOrganizationCount()
    }

    // ========== PUBLIC METHODS ==========

    /**
     * Carga todas las organizaciones guardadas
     */
    fun loadOrganizations() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.getAllOrganizations()
                .catch { e ->
                    _errorMessage.value = "Error al cargar organizaciones: ${e.message}"
                    _isLoading.value = false
                }
                .collect { organizationEntities ->
                    _organizations.value = organizationEntities.map { it.toApiModel() }
                    _isLoading.value = false
                }
        }
    }

    /**
     * Guarda una nueva organización
     */
    fun saveOrganization(organization: Organization) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val (organizationEntity, subscriptions) = organization.toEntityWithSubscriptions()
                repository.saveOrganizationWithSubscriptions(organizationEntity, subscriptions)

                loadOrganizationCount()
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error al guardar organización: ${e.message}"
                _isLoading.value = false
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun downloadOrganizationCalendar(
        organizationId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val apiService = ApiService()
                val result = apiService.getCalendarEventsByOrganization(organizationId)

                result.onSuccess { serverEvents ->

                    // Convertir y guardar nuevos eventos
                    serverEvents.forEach { serverEvent ->
                        val localEvent = EventAdapter.fromCalendarEvent(
                            serverEvent,
                            organizationId
                        )
                        eventRepository.insertEvent(localEvent)
                    }

                    _isLoading.value = false
                    onSuccess()

                }.onFailure { error ->
                    _isLoading.value = false
                    onError(error.message ?: "Error desconocido")
                }

            } catch (e: Exception) {
                _isLoading.value = false
                onError(e.message ?: "Error de conexión")
            }
        }
    }

    /**
     * Elimina una organización
     */
    fun deleteOrganization(organizationId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                repository.deleteOrganizationById(organizationId)
                loadOrganizationCount()
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar organización: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun updateChannelSubscriptions(organizationId: Int, channelIds: List<Int>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                // Eliminar suscripciones existentes de esta organización
                repository.deleteSubscriptionsByOrganization(organizationId)

                // Crear nuevas suscripciones
                channelIds.forEach { channelId ->
                    val subscription = SubscriptionEntity(
                        organizationId = organizationId,
                        channelId = channelId,
                        channelName = "", // Se llenará desde la API si es necesario
                        channelAcronym = "",
                        isActive = true
                    )
                    repository.saveSubscription(subscription)
                }

                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error actualizando suscripciones: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    /**
     * Obtiene una organización por ID
     */
    suspend fun getOrganizationById(organizationId: Int): Organization? {
        return try {
            repository.getOrganizationById(organizationId)?.toApiModel()
        } catch (e: Exception) {
            _errorMessage.value = "Error al obtener organización: ${e.message}"
            null
        }
    }

    suspend fun isSubscribedToChannel(organizationId: Int, channelId: Int): Boolean {
        return try {
            repository.isSubscribedToChannel(organizationId, channelId)
        } catch (e: Exception) {
            _errorMessage.value = "Error verificando suscripción: ${e.message}"
            false
        }
    }
    /**
     * Carga el contador de organizaciones
     */
    private fun loadOrganizationCount() {
        viewModelScope.launch {
            try {
                _organizationCount.value = repository.getOrganizationCount()
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar contador: ${e.message}"
            }
        }
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Refresca la lista de organizaciones
     */
    fun refresh() {
        loadOrganizations()
        loadOrganizationCount()
    }

    // ========== FACTORY ==========

    class Factory(
        private val repository: OrganizationRepository,
        private val eventRepository: EventRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OrganizationViewModel::class.java)) {
                return OrganizationViewModel(
                    repository,
                    eventRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}