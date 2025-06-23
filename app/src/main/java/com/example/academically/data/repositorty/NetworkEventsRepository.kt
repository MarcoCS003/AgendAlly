package com.example.academically.data.repositorty

import com.example.academically.data.remote.api.EventsApiService


import android.os.Build
import androidx.annotation.RequiresApi
import com.example.academically.data.mappers.toDomainModel
import com.example.academically.data.model.EventOrganization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkEventsRepository @Inject constructor(
    private val eventsApiService: EventsApiService
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAllEvents(): Result<List<EventOrganization>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = eventsApiService.getAllEvents()
                if (response.isSuccessful) {
                    val events = response.body()?.events?.map { it.toDomainModel() } ?: emptyList()
                    Result.success(events)
                } else {
                    Result.failure(Exception("Error obteniendo eventos: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun searchEvents(query: String): Result<List<EventOrganization>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = eventsApiService.searchEvents(query)
                if (response.isSuccessful) {
                    val events = response.body()?.events?.map { it.toDomainModel() } ?: emptyList()
                    Result.success(events)
                } else {
                    Result.failure(Exception("Error buscando eventos: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getEventsByCategory(category: String): Result<List<EventOrganization>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = eventsApiService.getEventsByCategory(category)
                if (response.isSuccessful) {
                    val events = response.body()?.events?.map { it.toDomainModel() } ?: emptyList()
                    Result.success(events)
                } else {
                    Result.failure(Exception("Error obteniendo eventos por categoría: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getUpcomingEvents(): Result<List<EventOrganization>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = eventsApiService.getUpcomingEvents()
                if (response.isSuccessful) {
                    val events = response.body()?.events?.map { it.toDomainModel() } ?: emptyList()
                    Result.success(events)
                } else {
                    Result.failure(Exception("Error obteniendo eventos próximos: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getEventsByOrganization(organizationId: Int): Result<List<EventOrganization>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = eventsApiService.getEventsByOrganization(organizationId)
                if (response.isSuccessful) {
                    val events = response.body()?.events?.map { it.toDomainModel() } ?: emptyList()
                    Result.success(events)
                } else {
                    Result.failure(Exception("Error obteniendo eventos de la organización: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}