package com.example.academically.data.model

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.academically.ui.theme.DarkThemeScheduleColors
import com.example.academically.ui.theme.LightThemeScheduleColors
import java.time.LocalDate

// ========== ENUMS SEGÚN EL NUEVO MODELO ==========
enum class PersonalEventType {
    PERSONAL,       // Evento creado por el estudiante
    SUBSCRIBED,     // Evento de canal suscrito
    HIDDEN,         // Evento suscrito pero oculto por el estudiante
    ACADEMIC        // Eventos académicos (clases, exámenes, tareas) - ELIMINADO, usar PERSONAL
}

enum class EventPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

// ========== TIPOS DE ITEMS SEGÚN EL NUEVO MODELO ==========
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

// ========== ELEMENTOS DE EVENTOS ==========
data class EventItem(
    val id: Int,
    val type: EventItemType,
    val title: String,              // "Horario", "Registro", "Contacto"
    val value: String,              // "8:30-15:30", "https://...", "222 229 8810"
    val isClickable: Boolean = false,
    val iconName: String? = null
)

// ========== NOTIFICACIONES ==========
data class EventNotification(
    val id: Int,
    val time: Long, // Milisegundos antes del evento
    val title: String,
    val message: String,
    val isEnabled: Boolean = true
)

// ========== FORMAS DE EVENTOS ==========
sealed class EventShape {
    object Circle : EventShape()
    object RoundedStart : EventShape()
    object RoundedMiddle : EventShape()
    object RoundedEnd : EventShape()
    object RoundedFull : EventShape()

    fun toShape(): Shape {
        return when (this) {
            is Circle -> CircleShape
            is RoundedStart -> RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp)
            is RoundedMiddle -> RoundedCornerShape(0.dp)
            is RoundedEnd -> RoundedCornerShape(topEnd = 14.dp, bottomEnd = 14.dp)
            is RoundedFull -> RoundedCornerShape(14.dp)
        }
    }
}

// ========== EVENTO PERSONAL (SEGÚN NUEVO MODELO) ==========
data class PersonalEvent(
    val id: Int,
    val title: String,
    val shortDescription: String = "",
    val longDescription: String = "",
    val location: String = "",
    val colorIndex: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val type: PersonalEventType,
    val institutionalEventId: Int? = null,  // Si viene de evento institucional
    val imagePath: String = "",
    val items: List<PersonalEventItem> = emptyList(),
    val notification: PersonalEventNotification? = null,
    val isVisible: Boolean = true,
    val createdAt: String,
    val updatedAt: String? = null
) {
    // Propiedad computada para obtener color según el tema
    val color: Color
        @Composable
        get() {
            val colors = if (isSystemInDarkTheme()) DarkThemeScheduleColors else LightThemeScheduleColors
            return colors[colorIndex % colors.size]
        }

    // ========== MÉTODOS DE FECHAS ==========
    @SuppressLint("NewApi")
    fun occursOn(date: LocalDate): Boolean {
        return (date.isEqual(startDate) || date.isAfter(startDate)) &&
                (date.isEqual(endDate) || date.isBefore(endDate))
    }

    @SuppressLint("NewApi")
    fun getShapeForDate(date: LocalDate): EventShape {
        // Si es un evento de un solo día, usar forma completa
        if (startDate.isEqual(endDate)) {
            return EventShape.RoundedFull
        }

        // Para eventos de múltiples días, calcular la forma según la posición
        return when {
            date.isEqual(startDate) -> EventShape.RoundedStart
            date.isEqual(endDate) -> EventShape.RoundedEnd
            else -> EventShape.RoundedMiddle
        }
    }
}

// ========== ITEM DE EVENTO PERSONAL ==========
data class PersonalEventItem(
    val id: Int,
    val personalEventId: Int,
    val iconName: String,
    val text: String,
    val value: String = "",
    val isClickable: Boolean = false
)

// ========== NOTIFICACIÓN DE EVENTO PERSONAL ==========
data class PersonalEventNotification(
    val id: Int,
    val personalEventId: Int,
    val time: Long,                 // Milisegundos antes del evento
    val title: String,
    val message: String,
    val isEnabled: Boolean = true
)