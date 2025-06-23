package com.example.academically.data.remote.api


import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {

    @POST("api/auth/google")
    suspend fun authenticateWithGoogle(
        @Body request: GoogleAuthRequest
    ): Response<AuthResponse>

    @GET("api/auth/profile")
    suspend fun getUserProfile(
        @Header("Authorization") authToken: String
    ): Response<ApiUser>

    @POST("api/auth/logout")
    suspend fun logout(
        @Header("Authorization") authToken: String
    ): Response<SuccessResponse>

    // Development endpoints
    @POST("api/dev/auth/mock-login")
    suspend fun mockLogin(
        @Query("email") email: String = "test@estudiante.tecnm.mx",
        @Query("name") name: String = "Usuario Test"
    ): Response<AuthResponse>
}