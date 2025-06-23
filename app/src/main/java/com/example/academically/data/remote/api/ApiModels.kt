package com.example.academically.data.remote.api

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

// Enums para tipos de items (debe coincidir con el backend)
@Serializable
enum class EventItemType {
    // Información temporal
    SCHEDULE, DEADLINE, DURATION,

    // Enlaces y archivos
    ATTACHMENT, WEBSITE, REGISTRATION_LINK, LIVE_STREAM, RECORDING,

    // Redes sociales
    FACEBOOK, INSTAGRAM, TWITTER, YOUTUBE, LINKEDIN,

    // Contacto
    PHONE, EMAIL, WHATSAPP,

    // Ubicación
    MAPS_LINK, ROOM_NUMBER, BUILDING,

    // Información adicional
    REQUIREMENTS, PRICE, CAPACITY, ORGANIZER
}

// ✅ CANAL (antes Career)
data class ApiChannel(
    @SerializedName("id") val id: Int,
    @SerializedName("organizationId") val organizationId: Int,
    @SerializedName("organizationName") val organizationName: String,
    @SerializedName("name") val name: String,
    @SerializedName("acronym") val acronym: String,
    @SerializedName("description") val description: String = "",
    @SerializedName("type") val type: String,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("isActive") val isActive: Boolean = true,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String? = null
)

data class ApiOrganization(
    @SerializedName("organizationID") val organizationID: Int,
    @SerializedName("acronym") val acronym: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String = "",
    @SerializedName("address") val address: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("studentNumber") val studentNumber: Int,
    @SerializedName("teacherNumber") val teacherNumber: Int,
    @SerializedName("logoUrl") val logoUrl: String? = null,
    @SerializedName("webSite") val webSite: String? = null,
    @SerializedName("facebook") val facebook: String? = null,
    @SerializedName("instagram") val instagram: String? = null,
    @SerializedName("twitter") val twitter: String? = null,
    @SerializedName("youtube") val youtube: String? = null,
    @SerializedName("linkedin") val linkedin: String? = null,
    @SerializedName("channels") val channels: List<ApiChannel> = emptyList(),
    @SerializedName("isActive") val isActive: Boolean = true,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
)



data class OrganizationsResponse(
    @SerializedName("organizations") val organizations: List<ApiOrganization>, // ✅ CAMBIO
    @SerializedName("total") val total: Int
)

data class ChannelsResponse(
    @SerializedName("channels") val channels: List<ApiChannel>,
    @SerializedName("total") val total: Int,
    @SerializedName("organizationId") val organizationId: Int? = null // ✅ CAMBIO
)

// ============== AUTENTICACIÓN ==============

data class GoogleAuthRequest(
    @SerializedName("idToken") val idToken: String,
    @SerializedName("clientType") val clientType: String = "ANDROID"
)

data class AuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("user") val user: ApiUser? = null,
    @SerializedName("token") val token: String? = null,
    @SerializedName("expiresAt") val expiresAt: String? = null,
    @SerializedName("message") val message: String? = null
)

data class ApiUser(
    @SerializedName("id") val id: Int,
    @SerializedName("googleId") val googleId: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("profilePicture") val profilePicture: String? = null,
    @SerializedName("role") val role: String = "STUDENT",
    @SerializedName("isActive") val isActive: Boolean = true,
    @SerializedName("notificationsEnabled") val notificationsEnabled: Boolean = true,
    @SerializedName("syncEnabled") val syncEnabled: Boolean = false,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("lastLoginAt") val lastLoginAt: String? = null
)

// ============== SUSCRIPCIONES ==============

data class ApiUserSubscription(
    @SerializedName("id") val id: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("channelId") val channelId: Int,
    @SerializedName("channelName") val channelName: String,
    @SerializedName("channelType") val channelType: String,
    @SerializedName("organizationName") val organizationName: String, // ✅ CAMBIO
    @SerializedName("subscribedAt") val subscribedAt: String,
    @SerializedName("isActive") val isActive: Boolean = true,
    @SerializedName("notificationsEnabled") val notificationsEnabled: Boolean = true,
    @SerializedName("syncedAt") val syncedAt: String? = null
)

data class UserSubscriptionsResponse(
    @SerializedName("subscriptions") val subscriptions: List<ApiUserSubscription>,
    @SerializedName("total") val total: Int,
    @SerializedName("userId") val userId: Int
)

data class SubscribeToChannelRequest(
    @SerializedName("channelId") val channelId: Int,
    @SerializedName("notificationsEnabled") val notificationsEnabled: Boolean = true
)








// Event Models
data class ApiEventItem(
    @SerializedName("id") val id: Int,
    @SerializedName("type") val type: String,
    @SerializedName("title") val title: String,
    @SerializedName("value") val value: String,
    @SerializedName("isClickable") val isClickable: Boolean = false,
    @SerializedName("iconName") val iconName: String? = null
)

data class ApiEvent(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("shortDescription") val shortDescription: String = "",
    @SerializedName("longDescription") val longDescription: String = "",
    @SerializedName("location") val location: String = "",
    @SerializedName("startDate") val startDate: String? = null,
    @SerializedName("endDate") val endDate: String? = null,
    @SerializedName("category") val category: String = "INSTITUTIONAL",
    @SerializedName("imagePath") val imagePath: String = "",
    @SerializedName("channelId") val channelId: Int? = null,
    @SerializedName("instituteId") val OrganizationId: Int,
    @SerializedName("items") val items: List<ApiEventItem> = emptyList(),
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    @SerializedName("isActive") val isActive: Boolean = true
)

data class EventsResponse(
    @SerializedName("events") val events: List<ApiEvent>,
    @SerializedName("total") val total: Int
)

data class SuccessResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: String? = null
)
