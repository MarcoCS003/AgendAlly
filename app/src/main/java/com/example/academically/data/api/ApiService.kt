package com.example.academically.data.api

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ApiService {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json)
        }

        install(Logging) {
            logger = Logger.ANDROID
            level = LogLevel.ALL
        }
    }

    private val baseUrl = "https://academic-ally-backend-113306869747.us-central1.run.app/api/"

    // ==================== ORGANIZACIONES ====================

    /**
     * Obtener todas las organizaciones disponibles
     */
    suspend fun getAllOrganizations(): Result<List<Organization>> {
        return try {
            val response: OrganizationSearchResponse = client.get("$baseUrl/organizations").body()
            Log.i("ApiService", "✅ Organizaciones obtenidas: ${response.total}")
            Result.success(response.organizations)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo organizaciones", e)
            Result.failure(e)
        }
    }

    /**
     * Buscar organizaciones por query
     */
    suspend fun searchOrganizations(query: String): Result<List<Organization>> {
        return try {
            val response: OrganizationSearchResponse = client.get("$baseUrl/organizations/search") {
                parameter("q", query)
            }.body()
            Log.i("ApiService", "✅ Búsqueda de organizaciones completada: ${response.total} resultados")
            Result.success(response.organizations)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error buscando organizaciones", e)
            Result.failure(e)
        }
    }

    /**
     * Obtener una organización específica por ID
     */
    suspend fun getOrganizationById(organizationId: Int): Result<Organization> {
        return try {
            val response = client.get("$baseUrl/organizations/$organizationId")
            val organization: Organization = response.body()
            Log.i("ApiService", "✅ Organización obtenida: ${organization.name}")
            Result.success(organization)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo organización por ID: $organizationId", e)
            Result.failure(e)
        }
    }

    /**
     * Obtener estadísticas de organizaciones
     */
    suspend fun getOrganizationStats(): Result<Map<String, Any>> {
        return try {
            val stats: Map<String, Any> = client.get("$baseUrl/organizations/stats").body()
            Log.i("ApiService", "✅ Estadísticas obtenidas")
            Result.success(stats)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo estadísticas", e)
            Result.failure(e)
        }
    }

    // ==================== CANALES ====================

    /**
     * Obtener todos los canales disponibles
     */
    suspend fun getAllChannels(): Result<List<Channel>> {
        return try {
            val response: ChannelsResponse = client.get("$baseUrl/channels").body()
            Log.i("ApiService", "✅ Canales obtenidos: ${response.total}")
            Result.success(response.channels)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo canales", e)
            Result.failure(e)
        }
    }

    /**
     * Buscar canales por query
     */
    suspend fun searchChannels(query: String): Result<List<Channel>> {
        return try {
            val response: ChannelsResponse = client.get("$baseUrl/channels/search") {
                parameter("q", query)
            }.body()
            Log.i("ApiService", "✅ Búsqueda de canales completada: ${response.total} resultados")
            Result.success(response.channels)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error buscando canales", e)
            Result.failure(e)
        }
    }

    /**
     * Obtener canal por ID
     */
    suspend fun getChannelById(channelId: Int): Result<Channel> {
        return try {
            val response = client.get("$baseUrl/channels/$channelId")
            val channel: Channel = response.body()
            Log.i("ApiService", "✅ Canal obtenido: ${channel.name}")
            Result.success(channel)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo canal $channelId", e)
            Result.failure(e)
        }
    }

    /**
     * Obtener canales por organización
     */
    suspend fun getChannelsByOrganization(organizationId: Int): Result<List<Channel>> {
        return try {
            val response: ChannelsResponse = client.get("$baseUrl/organizations/$organizationId/channels").body()
            Log.i("ApiService", "✅ Canales por organización obtenidos: ${response.total}")
            Result.success(response.channels)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo canales por organización", e)
            Result.failure(e)
        }
    }

    // ==================== EVENTOS ====================

    /**
     * Obtener todos los eventos disponibles
     */
    suspend fun getAllEvents(): Result<List<EventInstituteBlog>> {
        return try {
            val response: BlogEventsResponse = client.get("$baseUrl/events").body()
            Log.i("ApiService", "✅ Eventos obtenidos: ${response.total}")
            Result.success(response.events)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo eventos", e)
            Result.failure(e)
        }
    }

    /**
     * Buscar eventos por query
     */
    suspend fun searchEvents(query: String): Result<List<EventInstituteBlog>> {
        return try {
            val response: EventSearchResponse = client.get("$baseUrl/events/search") {
                parameter("q", query)
            }.body()
            Log.i("ApiService", "✅ Búsqueda de eventos completada: ${response.total} resultados")
            Result.success(response.events)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error buscando eventos", e)
            Result.failure(e)
        }
    }

    /**
     * Obtener evento por ID
     */
    suspend fun getEventById(eventId: Int): Result<EventInstituteBlog> {
        return try {
            val response = client.get("$baseUrl/events/$eventId")
            val event: EventInstituteBlog = response.body()
            Log.i("ApiService", "✅ Evento obtenido: ${event.title}")
            Result.success(event)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo evento $eventId", e)
            Result.failure(e)
        }
    }

    /**
     * Obtener eventos por canal
     */
    suspend fun getEventsByChannel(channelId: Int): Result<List<EventInstituteBlog>> {
        return try {
            val response: BlogEventsResponse = client.get("$baseUrl/events") {
                parameter("channelId", channelId)
            }.body()
            Log.i("ApiService", "✅ Eventos por canal obtenidos: ${response.total}")
            Result.success(response.events)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo eventos del canal $channelId", e)
            Result.failure(e)
        }
    }

    /**
     * Obtener eventos por organización
     */
    suspend fun getEventsByOrganization(organizationId: Int): Result<List<EventInstituteBlog>> {
        return try {
            val response: BlogEventsResponse = client.get("$baseUrl/events/organization/$organizationId").body()
            Log.i("ApiService", "✅ Eventos por organización obtenidos: ${response.total}")
            Result.success(response.events)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo eventos por organización", e)
            Result.failure(e)
        }
    }

    /**
     * Obtener eventos próximos
     */
    suspend fun getUpcomingEvents(): Result<List<EventInstituteBlog>> {
        return try {
            val response: UpcomingEventsResponse = client.get("$baseUrl/events/upcoming").body()
            Log.i("ApiService", "✅ Eventos próximos obtenidos: ${response.total}")
            Result.success(response.events)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo eventos próximos", e)
            Result.failure(e)
        }
    }

    /**
     * Obtener eventos por categoría
     */
    suspend fun getEventsByCategory(category: String): Result<List<EventInstituteBlog>> {
        return try {
            val response: EventsByCategoryResponse = client.get("$baseUrl/events/category/$category").body()
            Log.i("ApiService", "✅ Eventos por categoría obtenidos: ${response.total}")
            Result.success(response.events)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo eventos por categoría", e)
            Result.failure(e)
        }
    }

    // ==================== SUSCRIPCIONES ====================

    /**
     * Suscribirse a un canal
     */
    suspend fun subscribeToChannel(
        channelId: Int,
        authToken: String,
        notificationsEnabled: Boolean = true
    ): Result<SuccessResponse> {
        return try {
            val request = SubscribeToChannelRequest(channelId, notificationsEnabled)
            val response: SuccessResponse = client.post("$baseUrl/subscriptions") {
                header("Authorization", "Bearer $authToken")
                header("X-Client-Type", "ANDROID_STUDENT")
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            Log.i("ApiService", "✅ Suscripción exitosa al canal $channelId")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error suscribiéndose al canal", e)
            Result.failure(e)
        }
    }

    /**
     * Obtener suscripciones del usuario
     */
    suspend fun getUserSubscriptions(authToken: String): Result<List<UserSubscription>> {
        return try {
            val response: UserSubscriptionsResponse = client.get("$baseUrl/subscriptions") {
                header("Authorization", "Bearer $authToken")
                header("X-Client-Type", "ANDROID_STUDENT")
            }.body()

            Log.i("ApiService", "✅ Suscripciones obtenidas: ${response.total}")
            Result.success(response.subscriptions)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo suscripciones", e)
            Result.failure(e)
        }
    }

    /**
     * Desuscribirse de un canal
     */
    suspend fun unsubscribeFromChannel(
        channelId: Int,
        authToken: String
    ): Result<SuccessResponse> {
        return try {
            val response: SuccessResponse = client.delete("$baseUrl/subscriptions/$channelId") {
                header("Authorization", "Bearer $authToken")
                header("X-Client-Type", "ANDROID_STUDENT")
            }.body()

            Log.i("ApiService", "✅ Desuscripción exitosa del canal $channelId")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error desuscribiéndose del canal", e)
            Result.failure(e)
        }
    }

    /**
     * Actualizar notificaciones de suscripción
     */
    suspend fun updateSubscriptionNotifications(
        channelId: Int,
        authToken: String,
        notificationsEnabled: Boolean
    ): Result<SuccessResponse> {
        return try {
            val request = UpdateSubscriptionRequest(notificationsEnabled)
            val response: SuccessResponse = client.put("$baseUrl/subscriptions/$channelId") {
                header("Authorization", "Bearer $authToken")
                header("X-Client-Type", "ANDROID_STUDENT")
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            Log.i("ApiService", "✅ Notificaciones actualizadas para canal $channelId")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error actualizando notificaciones", e)
            Result.failure(e)
        }
    }

    /**
     * Cerrar cliente HTTP
     */
    fun close() {
        client.close()
    }
}