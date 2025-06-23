package com.example.academically.data.remote.api

import retrofit2.Response
import retrofit2.http.*

interface OrganizationsApiService {

    @GET("api/organizations")
    suspend fun getAllOrganizations(): Response<List<ApiOrganization>>

    @GET("api/organizations/search")
    suspend fun searchOrganizations(
        @Query("q") query: String
    ): Response<OrganizationsResponse>

    @GET("api/organizations/{id}")
    suspend fun getOrganizationById(
        @Path("id") organizationId: Int
    ): Response<ApiOrganization>

    @GET("api/organizations/{id}/channels")
    suspend fun getChannelsByOrganization(
        @Path("id") organizationId: Int
    ): Response<ChannelsResponse>

    @GET("api/organizations/stats")
    suspend fun getOrganizationStats(): Response<Map<String, Int>>
}