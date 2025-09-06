package com.agendally.app.ViewModel

import android.os.Build
import android.os.Debug
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.agendally.app.data.EventAdapter
import com.agendally.app.data.EventCategory
import com.agendally.app.data.api.ApiService
import com.agendally.app.data.api.Organization
import com.agendally.app.data.entities.EventCategoryEntity
import com.agendally.app.data.entities.SubscriptionEntity
import com.agendally.app.data.mappers.toApiModel
import com.agendally.app.data.mappers.toEntityWithSubscriptions
import com.agendally.app.data.repositorty.OrganizationRepository
import com.agendally.app.data.repository.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    private fun getUserFriendlyErrorMessage(error: Throwable?): String {
        return when {
            // Errores de conexión
            error?.message?.contains("ConnectException") == true ->
                "No se pudo conectar al servidor. Verifica tu conexión a internet."

            error?.message?.contains("SocketTimeoutException") == true ->
                "El servidor está tardando mucho en responder. Inténtalo de nuevo."

            error?.message?.contains("UnknownHostException") == true ->
                "No se pudo encontrar el servidor. Verifica tu conexión a internet."

            error?.message?.contains("HTTP 500") == true ->
                "Error interno del servidor. Inténtalo más tarde."

            error?.message?.contains("HTTP 404") == true ->
                "El recurso solicitado no fue encontrado."

            error?.message?.contains("HTTP 401") == true ->
                "No tienes permisos para acceder a este recurso."

            error?.message?.contains("localhost") == true ->
                "No se pudo conectar al servidor. Verifica que esté funcionando."

            error?.message?.contains("8080") == true ->
                "Servicio no disponible. Inténtalo más tarde."

            // Error genérico de red
            error?.message?.contains("IOException") == true ->
                "Error de conexión. Verifica tu internet e inténtalo de nuevo."

            // Si el mensaje es muy técnico o largo, usar mensaje genérico
            (error?.message?.length ?: 0) > 100 ->
                "Error de conexión con el servidor. Inténtalo más tarde."

            // Usar mensaje original si es corto y no técnico
            else -> error?.message ?: "Error desconocido"
        }
    }
    /**
     * Carga todas las organizaciones guardadas
     */
    fun loadOrganizations() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.getAllOrganizations()
                .catch { e ->
                    _errorMessage.value = getUserFriendlyErrorMessage(e) // ✅ Mensaje amigable
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
                _errorMessage.value = getUserFriendlyErrorMessage(e) // ✅ Mensaje amigable
                _isLoading.value = false
            }
        }
    }

    // AGREGAR - Función para verificar si hay eventos del calendario en la base de datos
    fun hasCalendarEventsFlow(organizationId: Int): Flow<Boolean> {
        return eventRepository.getEventsByOrganizationFlow(organizationId)
            .map { events ->
                // ✅ DEBUGGING
                println("🔍 hasCalendarEventsFlow DEBUG:")
                println("   🆔 OrganizationId buscado: $organizationId")
                println("   📊 Total eventos encontrados: ${events.size}")

                if (events.isEmpty()) {
                    println("   ⚠️  NO se encontraron eventos para organizationId = $organizationId")
                } else {
                    println("   📋 Eventos encontrados:")
                    events.forEach { event ->
                        println("     - ID: ${event.id}, Título: ${event.title}")
                        println("     - OrganizationId: ${event.organizationId}")
                        println("     - CategoryId: ${event.categoryId}")
                        println("     - EventCategory.CALENDAR_EVENT value: ${EventCategory.CALENDAR_EVENT}")
                    }
                }

                val hasCalendarEvents = events.any { it.categoryId == EventCategory.CALENDAR_EVENT }

                println("   📅 Eventos de CALENDAR_EVENT encontrados: ${events.count { it.categoryId == EventCategory.CALENDAR_EVENT }}")
                println("   ✅ Resultado hasCalendarEvents: $hasCalendarEvents")
                println("   ---")

                hasCalendarEvents
            }
            .catch { error ->
                println("❌ ERROR en hasCalendarEventsFlow: ${error.message}")
                emit(false)
            }
    }

    fun deleteCalendarEventsByOrganization(
    organizationId: Int,
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Usar el método del DAO que ya tienes definido
                eventRepository.deleteCalendarEventsByOrganization(organizationId)

                // Actualizar el estado después de eliminar
                _isLoading.value = false
                onSuccess()

            } catch (e: Exception) {
                _isLoading.value = false
                onError("Error al eliminar eventos del calendario: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun debugAllEventsInDatabase() {
        viewModelScope.launch {
            try {
                println("🗄️ ===== DEBUG: TODOS LOS EVENTOS EN BD =====")

                eventRepository.allEvents.collect { allEvents ->
                    println("📊 TOTAL EVENTOS EN BASE DE DATOS: ${allEvents.size}")

                    if (allEvents.isEmpty()) {
                        println("⚠️ LA BASE DE DATOS ESTÁ COMPLETAMENTE VACÍA")
                        println("   - Nunca se han descargado eventos del calendario")
                        println("   - O hay un error en la descarga/guardado")
                    } else {
                        println("📋 TODOS LOS EVENTOS:")
                        allEvents.forEachIndexed { index, event ->
                            println("   🔢 Evento #$index:")
                            println("     📝 Título: ${event.title}")
                            println("     🆔 ID local: ${event.id}")
                            println("     🏢 OrganizationId: ${event.organizationId}")
                            println("     📂 Category: ${event.category}")
                            println("     📅 Fecha: ${event.startDate}")
                            println("     ✅ Activo: ${event.isActive}")
                            println("     ---")
                        }

                        // Agrupar por organizationId
                        val byOrgId = allEvents.groupBy { it.organizationId }
                        println("🏢 EVENTOS AGRUPADOS POR ORGANIZATION_ID:")
                        byOrgId.forEach { (orgId, events) ->
                            println("   🆔 OrganizationId $orgId: ${events.size} eventos")
                        }
                    }

                    return@collect // Solo tomar el primer valor del Flow
                }
            } catch (e: Exception) {
                println("❌ ERROR debuggeando BD: ${e.message}")
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
                    onError(getUserFriendlyErrorMessage(error)) // ✅ Mensaje amigable
                }

            } catch (e: Exception) {
                _isLoading.value = false
                onError(getUserFriendlyErrorMessage(e)) // ✅ Mensaje amigable
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