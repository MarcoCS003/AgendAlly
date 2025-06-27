package com.example.academically.data.api

import kotlinx.serialization.Serializable

// Enums para tipos de items (debe coincidir con el backend)

@Serializable
enum class ClientType {
    ANDROID_STUDENT,    // App móvil para estudiantes
    DESKTOP_ADMIN,      // App escritorio para administradores
    WEB_ADMIN,          // Web dashboard para administradores
    UNKNOWN             // Por defecto/desarrollo
}

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

@Serializable
enum class UserRole {
    SUPER_ADMIN,    // Administrador general del sistema
    ADMIN,          // Administrador de organización específica
    STUDENT         // Estudiante que consume eventos
}

@Serializable
enum class ChannelType {
    CAREER,         // Canal de carrera (TICS, Industrial, etc.)
    DEPARTMENT,     // Departamento (Biblioteca, Centro Cómputo)
    ADMINISTRATIVE  // Administrativo (Servicios Escolares, etc.)
}

@Serializable
enum class EventType {
    PERSONAL,       // Evento creado por el estudiante
    SUBSCRIBED,     // Evento de canal suscrito
    HIDDEN         // Evento suscrito pero oculto por el estudiante
}

// ============== MODELOS PRINCIPALES ==============

// ✅ ORGANIZATION (antes Institute)
@Serializable
data class Organization(
    val organizationID: Int,
    val acronym: String,
    val name: String,
    val description: String = "",
    val address: String,
    val email: String,
    val phone: String,
    val studentNumber: Int,
    val teacherNumber: Int,
    val logoUrl: String? = null,
    val webSite: String? = null,
    val facebook: String? = null,
    val instagram: String? = null,
    val twitter: String? = null,
    val youtube: String? = null,
    val linkedin: String? = null,
    val channels: List<Channel> = emptyList(),
    val isActive: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

// ✅ CHANNEL (antes Career) - CORREGIDO
@Serializable
data class Channel(
    val id: Int,
    val organizationId: Int,
    val organizationName: String,
    val name: String,
    val acronym: String,
    val description: String = "",
    val type: ChannelType, // ✅ CORREGIDO: era ClientType, ahora es ChannelType
    val email: String? = null,
    val phone: String? = null,
    val isActive: Boolean = true,
    val createdAt: String,
    val updatedAt: String? = null
)

// ✅ EVENTOS DEL BLOG
@Serializable
data class EventInstituteBlog(
    val id: Int,
    val title: String,
    val shortDescription: String = "",
    val longDescription: String = "",
    val location: String = "",
    val startDate: String? = null,
    val endDate: String? = null,
    val category: String = "INSTITUTIONAL",
    val imagePath: String = "",
    val organizationId: Int,
    val channelId: Int? = null,
    val items: List<EventItemBlog> = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val isActive: Boolean = true
)

// ✅ ITEM DE EVENTO
@Serializable
data class EventItemBlog(
    val id: Int,
    val type: EventItemType,
    val title: String,
    val value: String,
    val isClickable: Boolean = false,
    val iconName: String? = null
)

// ✅ USUARIO
@Serializable
data class User(
    val id: Int,
    val googleId: String,
    val email: String,
    val name: String,
    val profilePicture: String? = null,
    val role: UserRole = UserRole.STUDENT,
    val isActive: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val syncEnabled: Boolean = false,
    val createdAt: String,
    val lastLoginAt: String? = null,
    val lastSyncAt: String? = null
)

// ✅ SUSCRIPCIÓN DE USUARIO
@Serializable
data class UserSubscription(
    val id: Int,
    val userId: Int,
    val channelId: Int,
    val channelName: String,
    val channelType: ChannelType, // ✅ CORREGIDO: era ClientType, ahora es ChannelType
    val organizationName: String,
    val subscribedAt: String,
    val isActive: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val syncedAt: String? = null
)

// ============== REQUESTS ==============

// ✅ AUTENTICACIÓN CON FIREBASE
@Serializable
data class GoogleAuthRequest(
    val idToken: String,
    val clientType: ClientType = ClientType.UNKNOWN,
    val organizationId: Int? = null
)

@Serializable
data class AuthResponse(
    val success: Boolean,
    val user: User? = null,
    val token: String? = null,
    val expiresAt: String? = null,
    val message: String? = null,
    val assignedRole: String? = null,
    val requiresOrganization: Boolean = false
)

@Serializable
data class TokenValidationRequest(
    val token: String
)

@Serializable
data class AdminSetupRequest(
    val organizationId: Int
)

// ✅ REQUEST PARA CREAR EVENTO
@Serializable
data class CreateEventRequest(
    val title: String,
    val shortDescription: String = "",
    val longDescription: String = "",
    val location: String = "",
    val startDate: String? = null,
    val endDate: String? = null,
    val category: String = "INSTITUTIONAL",
    val imagePath: String = "",
    val organizationId: Int,
    val channelId: Int? = null,
    val items: List<EventItemBlog> = emptyList()
)

// ✅ REQUEST PARA AGREGAR ORGANIZACIÓN
@Serializable
data class AddOrganizationRequest(
    val organizationID: Int,
    val channelID: Int
)

// ✅ SUSCRIPCIONES
@Serializable
data class SubscribeToChannelRequest(
    val channelId: Int,
    val notificationsEnabled: Boolean = true
)

@Serializable
data class UpdateSubscriptionRequest(
    val notificationsEnabled: Boolean
)

// ============== RESPUESTAS DEL API ==============

// ✅ RESPUESTA DE ORGANIZACIONES
@Serializable
data class OrganizationSearchResponse(
    val organizations: List<Organization>,
    val total: Int
)

// ✅ RESPUESTA DE CANALES
@Serializable
data class ChannelsResponse(
    val channels: List<Channel>,
    val total: Int,
    val organizationId: Int? = null
)

// ✅ RESPUESTA DE EVENTOS DEL BLOG
@Serializable
data class BlogEventsResponse(
    val events: List<EventInstituteBlog>,
    val total: Int,
    val organizationInfo: Organization? = null
)

// ✅ RESPUESTA DE SUSCRIPCIONES
@Serializable
data class UserSubscriptionsResponse(
    val subscriptions: List<UserSubscription>,
    val total: Int,
    val userId: Int
)

// ============== RESPUESTAS GENÉRICAS ==============

@Serializable
data class EventsListResponse(
    val events: List<EventInstituteBlog>,
    val total: Int
)

@Serializable
data class EventSearchResponse(
    val events: List<EventInstituteBlog>,
    val total: Int,
    val query: String
)

@Serializable
data class EventsByCategoryResponse(
    val events: List<EventInstituteBlog>,
    val total: Int,
    val category: String
)

@Serializable
data class UpcomingEventsResponse(
    val events: List<EventInstituteBlog>,
    val total: Int,
    val description: String
)

@Serializable
data class EventStatsResponse(
    val totalEvents: Long,
    val eventsByCategory: Map<String, Long>,
    val lastUpdated: String
)

@Serializable
data class SuccessResponse(
    val success: Boolean,
    val message: String,
    val data: String? = null
)

@Serializable
data class ErrorResponse(
    val error: String
)

@Serializable
data class MessageResponse(
    val message: String
)