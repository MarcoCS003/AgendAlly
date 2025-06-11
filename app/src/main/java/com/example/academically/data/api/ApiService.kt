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

    // ================== INSTITUTOS ==================
    suspend fun getAllInstitutes(): Result<List<Institute>> {
        return try {
            Log.d("ApiService", "🚀 Obteniendo todos los institutos")

            val response: HttpResponse = client.get("$baseUrl/institutes")
            val responseText = response.bodyAsText()

            Log.d("ApiService", "📄 Status: ${response.status}")
            Log.d("ApiService", "📄 JSON recibido (primeros 500 chars): ${responseText.take(500)}")

            val institutes: List<Institute> = json.decodeFromString(responseText)

            Log.d("ApiService", "✅ Institutos obtenidos: ${institutes.size}")
            Result.success(institutes)

        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo institutos", e)
            Result.failure(e)
        }
    }

    suspend fun searchInstitutes(query: String): Result<InstituteSearchResponse> {
        return try {
            val response: InstituteSearchResponse = client.get("$baseUrl/institutes/search") {
                parameter("q", query)
            }.body()

            Log.i("ApiService", "✅ Búsqueda completada: ${response.total} resultados")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error buscando institutos", e)
            Result.failure(e)
        }
    }

    suspend fun getInstituteById(id: Int): Result<Institute> {
        return try {
            val institute: Institute = client.get("$baseUrl/institutes/$id").body()
            Result.success(institute)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo instituto por ID", e)
            Result.failure(e)
        }
    }

    // ================== EVENTOS DEL BLOG ==================

    /**
     * Obtener todos los eventos del blog
     */
    suspend fun getAllBlogEvents(): Result<List<EventInstituteBlog>> {
        return try {
            Log.d("ApiService", "🚀 Obteniendo todos los eventos del blog")

            val response: HttpResponse = client.get("$baseUrl/events")
            val responseText = response.bodyAsText()

            Log.d("ApiService", "📄 Status: ${response.status}")
            Log.d("ApiService", "📄 Eventos JSON: ${responseText.take(300)}")

            val eventsResponse: EventsListResponse = json.decodeFromString(responseText)

            Log.d("ApiService", "✅ Eventos obtenidos: ${eventsResponse.events.size}")
            Result.success(eventsResponse.events)

        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo eventos del blog", e)
            Result.failure(e)
        }
    }

    /**
     * Obtener eventos por instituto
     */
    suspend fun getEventsByInstitute(instituteId: Int): Result<BlogEventsResponse> {
        return try {
            Log.d("ApiService", "🏢 Obteniendo eventos del instituto $instituteId")

            val response: BlogEventsResponse = client.get("$baseUrl/institutes/$instituteId/events").body()

            Log.d("ApiService", "✅ Eventos del instituto obtenidos: ${response.events.size}")
            Result.success(response)

        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo eventos del instituto", e)
            Result.failure(e)
        }
    }

    /**
     * Obtener evento específico por ID
     */
    suspend fun getEventById(eventId: Int): Result<EventInstituteBlog> {
        return try {
            val event: EventInstituteBlog = client.get("$baseUrl/events/$eventId").body()
            Log.d("ApiService", "✅ Evento obtenido: ${event.title}")
            Result.success(event)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error obteniendo evento por ID", e)
            Result.failure(e)
        }
    }

    /**
     * Buscar eventos por título o descripción
     */
    suspend fun searchEvents(query: String): Result<List<EventInstituteBlog>> {
        return try {
            val response: EventSearchResponse = client.get("$baseUrl/events/search") {
                parameter("q", query)
            }.body()

            Log.i("ApiService", "✅ Búsqueda de eventos completada: ${response.events.size} resultados")
            Result.success(response.events)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error buscando eventos", e)
            Result.failure(e)
        }
    }

    /**
     * Crear nuevo evento (para administradores)
     */
    suspend fun createEvent(request: CreateEventRequest): Result<EventInstituteBlog> {
        return try {
            val event: EventInstituteBlog = client.post("$baseUrl/events") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            Log.i("ApiService", "✅ Evento creado: ${event.title}")
            Result.success(event)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Error creando evento", e)
            Result.failure(e)
        }
    }

    // ================== UTILIDADES ==================

    suspend fun healthCheck(): Result<String> {
        return try {
            val response: String = client.get("$baseUrl/../health").body()
            Log.i("ApiService", "✅ Health check OK")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("ApiService", "❌ Health check failed", e)
            Result.failure(e)
        }
    }

    fun close() {
        client.close()
    }
}