package com.example.academically.data.remote.api


import retrofit2.Response
import retrofit2.http.*
interface ChannelsApiService {

    @GET("api/channels")
    suspend fun getAllChannels(
        @Query("organizationId") organizationId: Int? = null,
        @Query("type") type: String? = null
    ): Response<ChannelsResponse>

    @GET("api/channels/search")
    suspend fun searchChannels(
        @Query("q") query: String,
        @Query("organizationId") organizationId: Int? = null
    ): Response<ChannelsResponse>

    @GET("api/channels/{channelId}")
    suspend fun getChannelById(
        @Path("channelId") channelId: Int
    ): Response<ApiChannel>

    @GET("api/channels/types/{type}")
    suspend fun getChannelsByType(
        @Path("type") type: String,
        @Query("organizationId") organizationId: Int? = null
    ): Response<ChannelsResponse>

    @GET("api/users/{userId}/subscriptions")
    suspend fun getUserSubscriptions(
        @Path("userId") userId: Int,
        @Header("Authorization") authToken: String
    ): Response<UserSubscriptionsResponse>

    @POST("api/users/{userId}/subscriptions")
    suspend fun subscribeToChannel(
        @Path("userId") userId: Int,
        @Header("Authorization") authToken: String,
        @Body request: SubscribeToChannelRequest
    ): Response<ApiUserSubscription>

    @DELETE("api/users/{userId}/subscriptions/{channelId}")
    suspend fun unsubscribeFromChannel(
        @Path("userId") userId: Int,
        @Path("channelId") channelId: Int,
        @Header("Authorization") authToken: String
    ): Response<SuccessResponse>

    @GET("api/users/{userId}/subscriptions/events")
    suspend fun getSubscribedChannelEvents(
        @Path("userId") userId: Int,
        @Header("Authorization") authToken: String
    ): Response<EventsResponse>
}