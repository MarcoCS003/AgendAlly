package com.example.academically.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.academically.data.EventItem
import com.example.academically.data.api.EventItemBlog
import com.example.academically.data.api.EventItemType

/**
 * Utilidad para convertir items de la API a formato local
 */
object EventItemHandler {

    /**
     * Convierte un EventItemBlog (de la API) a EventItem (local)
     */
    fun convertToLocalEventItem(apiItem: EventItemBlog ): EventItem {
        return EventItem(
            id = apiItem.id,
            type = apiItem.type,
            text = apiItem.title, // El título va en text
            value = apiItem.value, // El valor en value
            isClickable = apiItem.isClickable,
            icon = getIconForType(apiItem.type)
        )
    }

    /**
     * Obtiene el ícono correspondiente para cada tipo de EventItemType
     */
    private fun getIconForType(type: EventItemType): ImageVector {
        return when (type) {
            // Información temporal
            EventItemType.SCHEDULE -> Icons.Default.Schedule
            EventItemType.DEADLINE -> Icons.Default.Event
            EventItemType.DURATION -> Icons.Default.Timer

            // Enlaces y archivos
            EventItemType.ATTACHMENT -> Icons.Default.AttachFile
            EventItemType.WEBSITE -> Icons.Default.Language
            EventItemType.REGISTRATION_LINK -> Icons.Default.HowToReg
            EventItemType.LIVE_STREAM -> Icons.Default.LiveTv
            EventItemType.RECORDING -> Icons.Default.Videocam

            // Redes sociales
            EventItemType.FACEBOOK -> Icons.Default.Facebook
            EventItemType.INSTAGRAM -> Icons.Default.CameraAlt
            EventItemType.TWITTER -> Icons.Default.AlternateEmail
            EventItemType.YOUTUBE -> Icons.Default.PlayCircle
            EventItemType.LINKEDIN -> Icons.Default.Business

            // Contacto
            EventItemType.PHONE -> Icons.Default.Phone
            EventItemType.EMAIL -> Icons.Default.Email
            EventItemType.WHATSAPP -> Icons.Default.Chat

            // Ubicación
            EventItemType.MAPS_LINK -> Icons.Default.LocationOn
            EventItemType.ROOM_NUMBER -> Icons.Default.MeetingRoom
            EventItemType.BUILDING -> Icons.Default.Business

            // Información adicional
            EventItemType.REQUIREMENTS -> Icons.Default.Checklist
            EventItemType.PRICE -> Icons.Default.AttachMoney
            EventItemType.CAPACITY -> Icons.Default.People
            EventItemType.ORGANIZER -> Icons.Default.AccountCircle
        }
    }

    /**
     * Función auxiliar para crear EventItems locales directamente
     */
    fun createLocalEventItem(
        id: Int = 0,
        type: EventItemType,
        text: String,
        value: String,
        isClickable: Boolean = false
    ): EventItem {
        return EventItem(
            id = id,
            type = type,
            text = text,
            value = value,
            isClickable = isClickable,
            icon = getIconForType(type)
        )
    }
}