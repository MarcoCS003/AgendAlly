package com.example.academically.data.remote.api


import retrofit2.Response
import retrofit2.http.*

interface EventsApiService {

    @GET("api/events")
    suspend fun getAllEvents(): Response<EventsResponse>

    @GET("api/events/search")
    suspend fun searchEvents(
        @Query("q") query: String
    ): Response<EventsResponse>

    @GET("api/events/category/{category}")
    suspend fun getEventsByCategory(
        @Path("category") category: String
    ): Response<EventsResponse>

    @GET("api/events/upcoming")
    suspend fun getUpcomingEvents(): Response<EventsResponse>

    @GET("api/events/{id}")
    suspend fun getEventById(
        @Path("id") eventId: Int
    ): Response<ApiEvent>

    @GET("api/organizations/{organizationId}/events") // ✅ CAMBIO: institutes -> organizations
    suspend fun getEventsByOrganization(
        @Path("organizationId") organizationId: Int
    ): Response<EventsResponse>
}
