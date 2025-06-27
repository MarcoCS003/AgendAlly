package com.example.academically.data.api

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
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

    private val baseUrl = "http://192.168.100.2:8080/api"

    // ================== ORGANIZACIONES ==================
    suspend fun getAllOrganizations(): Result<List<Organization>> {
        return try {
            Log.d("ApiService", "🚀 Obteniendo todas las organizaciones")

            val response: HttpResponse = client.get("$baseUrl/organizations")
            val responseText = response.bodyAsText()

            Log.d("ApiService", "📄 Status: ${response.status}")
            Log.d("ApiService", "📄 JSON recibido (primeros 500 chars): ${responseText.take(500)}")

            val organizations: List<Organization> = json.decodeFromString(responseText)

            Log.d("ApiService", "✅ Organizaciones obtenidas: ${organizations.size}")
            Result.success(organizations)

        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo organizaciones", e)
            Result.failure(e)
        }
    }

    suspend fun searchOrganizations(query: String): Result<OrganizationSearchResponse> {
        return try {
            val response: OrganizationSearchResponse = client.get("$baseUrl/organizations/search") {
                parameter("q", query)
            }.body()

            Log.i("ApiService", "✅ Búsqueda completada: ${response.total} resultados")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error buscando organizaciones", e)
            Result.failure(e)
        }
    }

    suspend fun getOrganizationById(id: Int): Result<Organization> {
        return try {
            val organization: Organization = client.get("$baseUrl/organizations/$id").body()
            Log.i("ApiService", "✅ Organización obtenida: ${organization.name}")
            Result.success(organization)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo organización por ID: $id", e)
            Result.failure(e)
        }
    }

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

    // ================== CANALES ==================
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

    suspend fun getChannelsByOrganization(organizationId: Int): Result<List<Channel>> {
        return try {
            val response: ChannelsResponse = client.get("$baseUrl/channels") {
                parameter("organizationId", organizationId)
            }.body()
            Log.i("ApiService", "✅ Canales por organización obtenidos: ${response.total}")
            Result.success(response.channels)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo canales por organización", e)
            Result.failure(e)
        }
    }

    // ================== EVENTOS ==================
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

    suspend fun getEventsByOrganization(organizationId: Int): Result<BlogEventsResponse> {
        return try {
            val response: BlogEventsResponse = client.get("$baseUrl/events/organization/$organizationId").body()
            Log.i("ApiService", "✅ Eventos por organización obtenidos: ${response.total}")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo eventos por organización", e)
            Result.failure(e)
        }
    }

    suspend fun getEventsByChannel(channelId: Int): Result<List<EventInstituteBlog>> {
        return try {
            val response: BlogEventsResponse = client.get("$baseUrl/events/channel/$channelId").body()
            Log.i("ApiService", "✅ Eventos por canal obtenidos: ${response.total}")
            Result.success(response.events)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo eventos por canal", e)
            Result.failure(e)
        }
    }

    suspend fun searchEvents(query: String): Result<EventSearchResponse> {
        return try {
            val response: EventSearchResponse = client.get("$baseUrl/events/search") {
                parameter("q", query)
            }.body()
            Log.i("ApiService", "✅ Búsqueda de eventos completada: ${response.total} resultados")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error buscando eventos", e)
            Result.failure(e)
        }
    }

    suspend fun getUpcomingEvents(): Result<UpcomingEventsResponse> {
        return try {
            val response: UpcomingEventsResponse = client.get("$baseUrl/events/upcoming").body()
            Log.i("ApiService", "✅ Eventos próximos obtenidos: ${response.total}")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo eventos próximos", e)
            Result.failure(e)
        }
    }

    suspend fun getEventsByCategory(category: String): Result<EventsByCategoryResponse> {
        return try {
            val response: EventsByCategoryResponse = client.get("$baseUrl/events/category/$category").body()
            Log.i("ApiService", "✅ Eventos por categoría obtenidos: ${response.total}")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo eventos por categoría", e)
            Result.failure(e)
        }
    }

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

    suspend fun getUserSubscriptions(authToken: String): Result<UserSubscriptionsResponse> {
        return try {
            val response: UserSubscriptionsResponse = client.get("$baseUrl/subscriptions") {
                header("Authorization", "Bearer $authToken")
                header("X-Client-Type", "ANDROID_STUDENT")
            }.body()

            Log.i("ApiService", "✅ Suscripciones obtenidas: ${response.total}")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo suscripciones", e)
            Result.failure(e)
        }
    }

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


    fun close() {
        client.close()
    }
}