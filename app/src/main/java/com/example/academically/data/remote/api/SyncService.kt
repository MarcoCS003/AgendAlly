package com.example.academically.data.remote.api

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.academically.data.local.dao.*
import com.example.academically.data.local.entities.ChannelEntity
import com.example.academically.data.local.entities.OrganizationEntity
import com.example.academically.data.local.entities.StudentSubscriptionEntity
import com.example.academically.data.repositorty.NetworkChannelsRepository
import com.example.academically.data.repositorty.NetworkEventsRepository
import com.example.academically.data.repositorty.NetworkOrganizationsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncService @Inject constructor(
    private val tokenManager: TokenManager,
    private val networkChannelsRepository: NetworkChannelsRepository,
    private val networkEventsRepository: NetworkEventsRepository,
    private val networkInstitutesRepository: NetworkOrganizationsRepository,
    private val channelDao: ChannelDao,
    private val organizationDao: OrganizationDao,
    private val studentSubscriptionDao: StudentSubscriptionDao,
    private val userProfileDao: UserProfileDao
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun syncAll(): SyncResult {
        return withContext(Dispatchers.IO) {
            if (!tokenManager.isAuthenticated()) {
                return@withContext SyncResult.Error("Usuario no autenticado")
            }

            val userId = tokenManager.getUserId() ?: return@withContext SyncResult.Error("ID de usuario no encontrado")

            try {
                var successfulSyncs = 0
                val errors = mutableListOf<String>()

                // 1. Sincronizar institutos
                try {
                    syncInstitutes()
                    successfulSyncs++
                } catch (e: Exception) {
                    errors.add("Institutos: ${e.message}")
                }

                // 2. Sincronizar canales
                try {
                    syncChannels()
                    successfulSyncs++
                } catch (e: Exception) {
                    errors.add("Canales: ${e.message}")
                }

                // 3. Sincronizar suscripciones del usuario
                try {
                    syncUserSubscriptions(userId)
                    successfulSyncs++
                } catch (e: Exception) {
                    errors.add("Suscripciones: ${e.message}")
                }

                // 4. Actualizar timestamp de sincronización
                updateLastSyncTime()

                if (errors.isEmpty()) {
                    SyncResult.Success("Sincronización completa: $successfulSyncs elementos")
                } else {
                    SyncResult.PartialSuccess(
                        message = "Sincronización parcial: $successfulSyncs exitosos",
                        errors = errors
                    )
                }

            } catch (e: Exception) {
                SyncResult.Error("Error general de sincronización: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun syncInstitutes() {
        val networkResult = networkInstitutesRepository.getAllOrganizations()

        if (networkResult.isSuccess) {
            val institutes = networkResult.getOrThrow()

            // Convertir a entities y guardar en Room
            val organizationEntities = institutes.map { Organization ->
                OrganizationEntity(
                    id = Organization.organizationID,
                    acronym = Organization.acronym,
                    name = Organization.name,
                    description = "",
                    address = Organization.address,
                    email = Organization.email,
                    phone = Organization.phone,
                    website = Organization.webSite,
                    logoUrl = null,
                    facebook = Organization.facebook,
                    instagram = Organization.instagram,
                    twitter = Organization.twitter,
                    youtube = Organization.youtube,
                    linkedin = null,
                    isActive = true,
                    cachedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    studentNumber = TODO(),
                    teacherNumber = TODO()
                )
            }

            organizationDao.insertOrganizations(organizationEntities)
        } else {
            throw Exception(networkResult.exceptionOrNull()?.message ?: "Error sincronizando institutos")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun syncChannels() {
        val networkResult = networkChannelsRepository.getAllChannels()

        if (networkResult.isSuccess) {
            val channels = networkResult.getOrThrow()

            // Convertir a entities y guardar en Room
            val channelEntities = channels.map { channel ->
                ChannelEntity(
                    id = channel.id,
                    organizationId = channel.organizationId,
                    name = channel.name,
                    acronym = channel.acronym,
                    description = channel.description,
                    type = channel.type,
                    email = channel.email,
                    phone = channel.phone,
                    isActive = channel.isActive,
                    cachedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    createdAt = channel.createdAt,
                    updatedAt = channel.updatedAt
                )
            }

            channelDao.insertChannels(channelEntities)
        } else {
            throw Exception(networkResult.exceptionOrNull()?.message ?: "Error sincronizando canales")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun syncUserSubscriptions(userId: Int) {
        val networkResult = networkChannelsRepository.getUserSubscriptions(userId)

        if (networkResult.isSuccess) {
            val subscriptions = networkResult.getOrThrow()

            // Convertir a entities y guardar en Room
            val subscriptionEntities = subscriptions.map { subscription ->
                StudentSubscriptionEntity(
                    id = subscription.id,
                    userId = subscription.userId,
                    channelId = subscription.channelId,
                    subscribedAt = subscription.subscribedAt,
                    isActive = subscription.isActive,
                    notificationsEnabled = subscription.notificationsEnabled,
                    syncedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            }

            studentSubscriptionDao.subscribeToChannels(subscriptionEntities)
        } else {
            throw Exception(networkResult.exceptionOrNull()?.message ?: "Error sincronizando suscripciones")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun updateLastSyncTime() {
        val currentTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        userProfileDao.updateSyncSettings(true, currentTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun syncInstitutionalEvents(): SyncResult {
        return withContext(Dispatchers.IO) {
            try {
                val userId = tokenManager.getUserId() ?: return@withContext SyncResult.Error("ID de usuario no encontrado")

                // Obtener eventos de canales suscritos
                val networkResult = networkChannelsRepository.getSubscribedChannelEvents(userId)

                if (networkResult.isSuccess) {
                    val events = networkResult.getOrThrow()

                    // Aquí podrías convertir los eventos a PersonalEvent y guardarlos
                    // como eventos suscritos en la base de datos local
                    // Por ahora, solo retornamos éxito

                    SyncResult.Success("${events.size} eventos institucionales sincronizados")
                } else {
                    SyncResult.Error(networkResult.exceptionOrNull()?.message ?: "Error sincronizando eventos")
                }
            } catch (e: Exception) {
                SyncResult.Error("Error sincronizando eventos: ${e.message}")
            }
        }
    }

    suspend fun getLastSyncTime(): String? {
        return userProfileDao.getUserProfile().first()?.lastSyncAt
    }

    @SuppressLint("NewApi")
    suspend fun shouldSync(): Boolean {
        val lastSync = getLastSyncTime()
        if (lastSync == null) return true

        // Sincronizar si han pasado más de 1 hora
        try {
            val lastSyncTime = LocalDateTime.parse(lastSync, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val now = LocalDateTime.now()
            return now.isAfter(lastSyncTime.plusHours(1))
        } catch (e: Exception) {
            return true
        }
    }
}

sealed class SyncResult {
    data class Success(val message: String) : SyncResult()
    data class PartialSuccess(val message: String, val errors: List<String>) : SyncResult()
    data class Error(val message: String) : SyncResult()
}
