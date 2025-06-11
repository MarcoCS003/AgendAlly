package com.example.academically.data.api

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

@Serializable
data class Career(
    val careerID: Int,
    val name: String,
    val acronym: String,
    val email: String? = null,
    val phone: String? = null
)

@Serializable
data class Institute(
    val instituteID: Int,
    val acronym: String,
    val name: String,
    val address: String,
    val email: String,
    val phone: String,
    val studentNumber: Int,
    val teacherNumber: Int,
    val webSite: String? = null,
    val facebook: String? = null,
    val instagram: String? = null,
    val twitter: String? = null,
    val youtube: String? = null,
    val listCareer: List<Career> = emptyList()
)

@Serializable
data class InstituteSearchResponse(
    val institutes: List<Institute>,
    val total: Int
)

// NUEVO: Modelo para items de eventos
@Serializable
data class EventInstituteBlogItem(
    val id: Int,
    val type: EventItemType,
    val title: String, // Título descriptivo
    val value: String, // Valor del item
    val isClickable: Boolean = false, // Si es clickeable
    val iconName: String? = null // Nombre del ícono
)

// MODELOS PARA EVENTOS DEL BLOG
@Serializable
data class EventInstituteBlog(
    val id: Int,
    val title: String,
    val shortDescription: String = "",
    val longDescription: String = "",
    val location: String = "",
    val startDate: String? = null, // Formato ISO: "2025-11-28"
    val endDate: String? = null,
    val category: String = "INSTITUTIONAL", // "INSTITUTIONAL", "CAREER", "PERSONAL"
    val imagePath: String = "",
    val instituteId: Int,
    val items: List<EventInstituteBlogItem> = emptyList(), // NUEVO: Lista de items
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val isActive: Boolean = true
)

@Serializable
data class BlogEventsResponse(
    val events: List<EventInstituteBlog>,
    val total: Int,
    val instituteInfo: Institute? = null
)

// RESPUESTAS DEL BACKEND
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
data class MessageResponse(
    val message: String
)

@Serializable
data class ErrorResponse(
    val error: String
)

// Request para crear evento
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
    val instituteId: Int,
    val items: List<EventInstituteBlogItem> = emptyList() // NUEVO: Items del evento
)

// Request para agregar instituto
@Serializable
data class AddInstituteRequest(
    val instituteID: Int,
    val careerID: Int
)