package com.example.academically.data.repositorty

import com.example.academically.data.mappers.ChannelDomain
import com.example.academically.data.mappers.toDomainModel
import com.example.academically.data.mappers.toUIOrganization
import com.example.academically.data.model.Organization
import com.example.academically.data.remote.api.OrganizationsApiService
import com.example.academically.data.remote.api.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkOrganizationsRepository @Inject constructor(
    private val organizationsApiService: OrganizationsApiService,
    private val tokenManager: TokenManager
) {

    suspend fun getAllOrganizations(): Result<List<Organization>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = organizationsApiService.getAllOrganizations()
                if (response.isSuccessful) {
                    val organizations = response.body()?.map { it.toDomainModel() } ?: emptyList()
                    Result.success(organizations)
                } else {
                    Result.failure(Exception("Error obteniendo organizaciones: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            } as Result<List<Organization>>
        }
    }

    suspend fun searchOrganizations(query: String): Result<List<Organization>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = organizationsApiService.searchOrganizations(query)
                if (response.isSuccessful) {
                    val organizations = response.body()?.organizations?.map { it.toDomainModel() } ?: emptyList()
                    Result.success(organizations)
                } else {
                    Result.failure(Exception("Error buscando organizaciones: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            } as Result<List<Organization>>
        }
    }

    suspend fun getOrganizationById(organizationId: Int): Result<Organization> {
        return withContext(Dispatchers.IO) {
            try {
                val response = organizationsApiService.getOrganizationById(organizationId)
                if (response.isSuccessful) {
                    val organization = response.body()?.toDomainModel()
                        ?: return@withContext Result.failure(Exception("Organización no encontrada"))
                    Result.success(organization)
                } else {
                    Result.failure(Exception("Error obteniendo organización: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            } as Result<Organization>
        }
    }

    suspend fun getChannelsByOrganization(organizationId: Int): Result<List<ChannelDomain>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = organizationsApiService.getChannelsByOrganization(organizationId)
                if (response.isSuccessful) {
                    val channels = response.body()?.channels?.map { apiChannel ->
                        ChannelDomain(
                            id = apiChannel.id,
                            organizationId = apiChannel.organizationId,
                            organizationName = apiChannel.organizationName,
                            name = apiChannel.name,
                            acronym = apiChannel.acronym,
                            description = apiChannel.description,
                            type = apiChannel.type,
                            email = apiChannel.email,
                            phone = apiChannel.phone,
                            isActive = apiChannel.isActive,
                            createdAt = apiChannel.createdAt,
                            updatedAt = apiChannel.updatedAt
                        )
                    } ?: emptyList()
                    Result.success(channels)
                } else {
                    Result.failure(Exception("Error obteniendo canales: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Para compatibilidad con UI existente
    suspend fun getAllOrganizationsForUI(): Result<List<Organization>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = organizationsApiService.getAllOrganizations()
                if (response.isSuccessful) {
                    val organizations = response.body()?.map { it.toUIOrganization() } ?: emptyList()
                    Result.success(organizations)
                } else {
                    Result.failure(Exception("Error obteniendo organizaciones: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            } as Result<List<Organization>>
        }
    }
}
