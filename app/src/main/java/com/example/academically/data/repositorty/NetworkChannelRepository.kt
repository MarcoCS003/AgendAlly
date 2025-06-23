package com.example.academically.data.repositorty


import android.os.Build
import androidx.annotation.RequiresApi
import com.example.academically.data.mappers.ChannelDomain
import com.example.academically.data.mappers.UserSubscriptionDomain
import com.example.academically.data.mappers.toDomainModel
import com.example.academically.data.model.EventOrganization
import com.example.academically.data.remote.api.ChannelsApiService
import com.example.academically.data.remote.api.SubscribeToChannelRequest
import com.example.academically.data.remote.api.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkChannelsRepository @Inject constructor(
    private val channelsApiService: ChannelsApiService,
    private val tokenManager: TokenManager
) {

    suspend fun getAllChannels(organizationId: Int? = null, type: String? = null): Result<List<ChannelDomain>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = channelsApiService.getAllChannels(organizationId, type)
                if (response.isSuccessful) {
                    val channels = response.body()?.channels?.map { it.toDomainModel() } ?: emptyList()
                    Result.success(channels)
                } else {
                    Result.failure(Exception("Error obteniendo canales: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            } as Result<List<ChannelDomain>>
        }
    }

    suspend fun searchChannels(query: String, organizationId: Int? = null): Result<List<ChannelDomain>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = channelsApiService.searchChannels(query, organizationId)
                if (response.isSuccessful) {
                    val channels = response.body()?.channels?.map { it.toDomainModel() } ?: emptyList()
                    Result.success(channels)
                } else {
                    Result.failure(Exception("Error buscando canales: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            } as Result<List<ChannelDomain>>
        }
    }

    suspend fun getUserSubscriptions(userId: Int): Result<List<UserSubscriptionDomain>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = tokenManager.getAuthHeader()
                    ?: return@withContext Result.failure(Exception("No hay token de autenticación"))

                val response = channelsApiService.getUserSubscriptions(userId, authHeader)
                if (response.isSuccessful) {
                    val subscriptions = response.body()?.subscriptions?.map { it.toDomainModel() } ?: emptyList()
                    Result.success(subscriptions)
                } else {
                    Result.failure(Exception("Error obteniendo suscripciones: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun subscribeToChannel(userId: Int, channelId: Int, notificationsEnabled: Boolean = true): Result<UserSubscriptionDomain> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = tokenManager.getAuthHeader()
                    ?: return@withContext Result.failure(Exception("No hay token de autenticación"))

                val request = SubscribeToChannelRequest(channelId, notificationsEnabled)
                val response = channelsApiService.subscribeToChannel(userId, authHeader, request)

                if (response.isSuccessful) {
                    val subscription = response.body()?.toDomainModel()
                        ?: return@withContext Result.failure(Exception("Respuesta vacía del servidor"))
                    Result.success(subscription)
                } else {
                    Result.failure(Exception("Error suscribiéndose: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun unsubscribeFromChannel(userId: Int, channelId: Int): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = tokenManager.getAuthHeader()
                    ?: return@withContext Result.failure(Exception("No hay token de autenticación"))

                val response = channelsApiService.unsubscribeFromChannel(userId, channelId, authHeader)
                if (response.isSuccessful) {
                    Result.success(true)
                } else {
                    Result.failure(Exception("Error desuscribiéndose: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getSubscribedChannelEvents(userId: Int): Result<List<EventOrganization>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = tokenManager.getAuthHeader()
                    ?: return@withContext Result.failure(Exception("No hay token de autenticación"))

                val response = channelsApiService.getSubscribedChannelEvents(userId, authHeader)
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
}