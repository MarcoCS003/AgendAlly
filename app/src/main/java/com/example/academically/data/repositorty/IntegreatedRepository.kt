package com.example.academically.data.repositorty

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.academically.data.local.dao.*
import com.example.academically.data.local.entities.ChannelEntity
import com.example.academically.data.local.entities.OrganizationEntity
import com.example.academically.data.local.entities.StudentSubscriptionEntity
import com.example.academically.data.mappers.*
import com.example.academically.data.model.*
import com.example.academically.data.remote.api.SyncService
import com.example.academically.data.remote.api.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntegratedRepository @Inject constructor(
    private val networkOrganizationsRepository: NetworkOrganizationsRepository,
    private val networkChannelsRepository: NetworkChannelsRepository,
    private val networkEventsRepository: NetworkEventsRepository,
    private val organizationDao: OrganizationDao,
    private val channelDao: ChannelDao,
    private val studentSubscriptionDao: StudentSubscriptionDao,
    private val syncService: SyncService,
    private val tokenManager: TokenManager
) {

    // =============== ORGANIZATIONS ===============

    fun getAllOrganizations(): Flow<List<com.example.academically.data.model.Organization>> {
        return organizationDao.getOrganizationsWithChannels()
            .map { organizationsWithChannels ->
                organizationsWithChannels.map { it.toDomainModel() }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun refreshOrganizations(): Result<List<Organization>> {
        return try {
            val networkResult = networkOrganizationsRepository.getAllOrganizations()
            if (networkResult.isSuccess) {
                // Actualizar cache local
                val organizations = networkResult.getOrThrow()
                val entities = organizations.mapIndexed { index, org ->
                    org.toEntity()
                }
                organizationDao.insertOrganizations(entities)
                networkResult
            } else {
                networkResult
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchOrganizations(query: String): Result<Organization> {
        return try {
            // Buscar en cache local primero
            val localResults = organizationDao.searchOrganizations("%$query%").first()

            if (localResults.isNotEmpty()) {
                val organizations = localResults.map { it.toDomainModel() }
                Result.success(organizations)
            } else {
                // Buscar en red
                networkOrganizationsRepository.searchOrganizations(query)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }as kotlin.Result<Organization>
    }

    // =============== CHANNELS ===============

    fun getChannelsByOrganization(organizationId: Int): Flow<List<ChannelDomain>> {
        return channelDao.getChannelsWithOrganizationByOrganization(organizationId)
            .map { channelsWithOrganization ->
                channelsWithOrganization.map { it.toDomainModel() }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun refreshChannelsByOrganization(organizationId: Int): Result<List<ChannelDomain>> {
        return try {
            val networkResult = networkOrganizationsRepository.getChannelsByOrganization(organizationId)
            if (networkResult.isSuccess) {
                // Actualizar cache local
                val channels = networkResult.getOrThrow()
                val entities = channels.map { it.toEntity() }
                channelDao.insertChannels(entities)
                networkResult
            } else {
                networkResult
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =============== SUBSCRIPTIONS ===============

    fun getUserSubscriptions(userId: Int): Flow<List<UserSubscriptionDomain>> {
        return studentSubscriptionDao.getUserSubscriptionsWithChannels(userId)
            .map { subscriptionsWithChannels ->
                subscriptionsWithChannels.map { it.toDomainModel() }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun refreshUserSubscriptions(userId: Int): Result<List<UserSubscriptionDomain>> {
        return try {
            val networkResult = networkChannelsRepository.getUserSubscriptions(userId)
            if (networkResult.isSuccess) {
                // Actualizar cache local
                val subscriptions = networkResult.getOrThrow()
                val entities = subscriptions.map { it.toEntity() }
                studentSubscriptionDao.subscribeToChannels(entities)
                networkResult
            } else {
                networkResult
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun subscribeToChannel(userId: Int, channelId: Int, notificationsEnabled: Boolean = true): Result<UserSubscriptionDomain> {
        return try {
            val networkResult = networkChannelsRepository.subscribeToChannel(userId, channelId, notificationsEnabled)
            if (networkResult.isSuccess) {
                // Actualizar cache local
                val subscription = networkResult.getOrThrow()
                val entity = subscription.toEntity()
                studentSubscriptionDao.subscribeToChannel(entity)
                networkResult
            } else {
                networkResult
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unsubscribeFromChannel(userId: Int, channelId: Int): Result<Boolean> {
        return try {
            val networkResult = networkChannelsRepository.unsubscribeFromChannel(userId, channelId)
            if (networkResult.isSuccess) {
                // Actualizar cache local
                studentSubscriptionDao.unsubscribeFromChannel(channelId, userId)
                networkResult
            } else {
                networkResult
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =============== EVENTS ===============

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getInstitutionalEvents(): Result<List<EventOrganization>> {
        return networkEventsRepository.getAllEvents()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun searchEvents(query: String): Result<List<EventOrganization>> {
        return networkEventsRepository.searchEvents(query)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getEventsByOrganization(organizationId: Int): Result<List<EventOrganization>> {
        return networkEventsRepository.getEventsByOrganization(organizationId)
    }
}

// ============== UTILITY EXTENSIONS ==============

@RequiresApi(Build.VERSION_CODES.O)
private fun com.example.academically.data.model.Organization.toEntity(): OrganizationEntity {
    val now = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    return OrganizationEntity(
        id = this.organizationID,
        acronym = this.acronym,
        name = this.name,
        description = "",
        address = this.address,
        email = this.email,
        phone = this.phone,
        studentNumber = this.studentNumber,
        teacherNumber = this.teacherNumber,
        website = this.webSite,
        logoUrl = null,
        facebook = this.facebook,
        instagram = this.instagram,
        twitter = this.twitter,
        youtube = this.youtube,
        linkedin = null,
        isActive = true,
        cachedAt = now,
        createdAt = now,
        updatedAt = null
    )
}

@RequiresApi(Build.VERSION_CODES.O)
private fun ChannelDomain.toEntity(): ChannelEntity {
    val now = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    return ChannelEntity(
        id = this.id,
        organizationId = this.organizationId,
        name = this.name,
        acronym = this.acronym,
        description = this.description,
        type = this.type,
        email = this.email,
        phone = this.phone,
        isActive = this.isActive,
        cachedAt = now,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

@RequiresApi(Build.VERSION_CODES.O)
private fun UserSubscriptionDomain.toEntity(): StudentSubscriptionEntity {
    return StudentSubscriptionEntity(
        id = this.id,
        userId = this.userId,
        channelId = this.channelId,
        subscribedAt = this.subscribedAt,
        isActive = this.isActive,
        notificationsEnabled = this.notificationsEnabled,
        syncedAt = this.syncedAt
    )
}