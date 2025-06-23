package com.example.academically.ViewModel


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.academically.data.repositorty.IntegratedRepository
import com.example.academically.data.model.Organization
import com.example.academically.data.mappers.ChannelDomain
import com.example.academically.data.mappers.UserSubscriptionDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class OrganizationViewModel @Inject constructor(
    private val integratedRepository: IntegratedRepository
) : ViewModel() {

    var organizations by mutableStateOf<List<Organization>>(emptyList())
        private set

    var channels by mutableStateOf<List<ChannelDomain>>(emptyList())
        private set

    var userSubscriptions by mutableStateOf<List<UserSubscriptionDomain>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var selectedOrganizationId by mutableStateOf<Int?>(null)
        private set

    init {
        loadOrganizations()
    }

    fun loadOrganizations() {
        viewModelScope.launch {
            isLoading = true
            error = null

            try {
                // Intentar desde caché local primero, luego refrescar desde red
                val result = integratedRepository.refreshOrganizations()

                if (result.isSuccess) {
                    organizations = result.getOrThrow()
                } else {
                    error = "No se pudieron cargar las organizaciones: ${result.exceptionOrNull()?.message}"
                    organizations = getFallbackOrganizations()
                }
            } catch (e: Exception) {
                error = "Error: ${e.message}"
                organizations = getFallbackOrganizations()
            }

            isLoading = false
        }
    }

    fun selectOrganization(organizationId: Int) {
        selectedOrganizationId = organizationId
        loadChannelsForOrganization(organizationId)
        loadUserSubscriptions()
    }

    fun loadChannelsForOrganization(organizationId: Int) {
        viewModelScope.launch {
            isLoading = true

            try {
                val result = integratedRepository.refreshChannelsByOrganization(organizationId)
                if (result.isSuccess) {
                    channels = result.getOrThrow()
                } else {
                    error = "No se pudieron cargar los canales: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                error = "Error cargando canales: ${e.message}"
            }

            isLoading = false
        }
    }

    fun loadUserSubscriptions(userId: Int = 1) {
        viewModelScope.launch {
            try {
                val result = integratedRepository.refreshUserSubscriptions(userId)
                if (result.isSuccess) {
                    userSubscriptions = result.getOrThrow()
                }
            } catch (e: Exception) {
                error = "Error cargando suscripciones: ${e.message}"
            }
        }
    }

    fun subscribeToChannel(channelId: Int, userId: Int = 1) {
        viewModelScope.launch {
            isLoading = true

            try {
                val result = integratedRepository.subscribeToChannel(userId, channelId, true)
                if (result.isSuccess) {
                    // Recargar suscripciones para actualizar la UI
                    loadUserSubscriptions(userId)
                } else {
                    error = "Error al suscribirse: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                error = "Error al suscribirse: ${e.message}"
            }

            isLoading = false
        }
    }

    fun unsubscribeFromChannel(channelId: Int, userId: Int = 1) {
        viewModelScope.launch {
            isLoading = true

            try {
                val result = integratedRepository.unsubscribeFromChannel(userId, channelId)
                if (result.isSuccess) {
                    // Recargar suscripciones para actualizar la UI
                    loadUserSubscriptions(userId)
                } else {
                    error = "Error al desuscribirse: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                error = "Error al desuscribirse: ${e.message}"
            }

            isLoading = false
        }
    }

    fun isSubscribedToChannel(channelId: Int): Boolean {
        return userSubscriptions.any { it.channelId == channelId && it.isActive }
    }

    fun searchOrganizations(query: String) {
        searchQuery = query

        if (query.isBlank()) {
            loadOrganizations()
            return
        }

        // Búsqueda local en las organizaciones cargadas
        val allOrganizations = organizations
        organizations = allOrganizations.filter { organization ->
            organization.name.contains(query, ignoreCase = true) ||
                    organization.acronym.contains(query, ignoreCase = true)
        }
    }

    fun clearError() {
        error = null
    }

    private fun getFallbackOrganizations(): List<Organization> {
        return listOf(
            Organization(
                organizationID = 1,
                acronym = "ITP",
                name = "Instituto Tecnológico de Puebla",
                address = "Del Tecnológico 420, Puebla",
                email = "info@puebla.tecnm.mx",
                phone = "222 229 8810",
                studentNumber = 6284,
                teacherNumber = 298,
                webSite = "https://www.puebla.tecnm.mx"
            )
        )
    }
}