package com.example.academically.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.academically.data.model.PersonalEventItem
import com.example.academically.data.model.EventItemType

/**
 * Utilidad para convertir items y manejar eventos personales
 */
object EventItemHandler {

    /**
     * Crea un PersonalEventItem local directamente
     */
    fun createPersonalEventItem(
        id: Int = 0,
        personalEventId: Int = 0,
        iconName: String,
        text: String,
        value: String = "",
        isClickable: Boolean = false
    ): PersonalEventItem {
        return PersonalEventItem(
            id = id,
            personalEventId = personalEventId,
            iconName = iconName,
            text = text,
            value = value,
            isClickable = isClickable
        )
    }

    /**
     * Obtiene el ícono correspondiente para cada tipo de EventItemType
     */
    fun getIconForType(type: EventItemType): ImageVector {
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
            EventItemType.WHATSAPP -> Icons.Default.ChatBubble

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
     * Obtiene el nombre del ícono basado en el tipo
     */
    fun getIconNameForType(type: EventItemType): String {
        return when (type) {
            EventItemType.SCHEDULE -> "schedule"
            EventItemType.DEADLINE -> "alarm"
            EventItemType.DURATION -> "time"
            EventItemType.ATTACHMENT -> "attachment"
            EventItemType.WEBSITE -> "link"
            EventItemType.REGISTRATION_LINK -> "edit"
            EventItemType.LIVE_STREAM -> "video"
            EventItemType.RECORDING -> "video"
            EventItemType.FACEBOOK -> "share"
            EventItemType.INSTAGRAM -> "share"
            EventItemType.TWITTER -> "share"
            EventItemType.YOUTUBE -> "video"
            EventItemType.LINKEDIN -> "share"
            EventItemType.PHONE -> "call"
            EventItemType.EMAIL -> "email"
            EventItemType.WHATSAPP -> "chat"
            EventItemType.MAPS_LINK -> "location"
            EventItemType.ROOM_NUMBER -> "location"
            EventItemType.BUILDING -> "location"
            EventItemType.REQUIREMENTS -> "list"
            EventItemType.PRICE -> "payment"
            EventItemType.CAPACITY -> "group"
            EventItemType.ORGANIZER -> "person"
        }
    }

    /**
     * Crea items de ejemplo para eventos
     */
    fun createSampleEventItems(eventId: Int): List<PersonalEventItem> {
        return listOf(
            createPersonalEventItem(
                id = 1,
                personalEventId = eventId,
                iconName = "location",
                text = "Ubicación",
                value = "Edificio principal"
            ),
            createPersonalEventItem(
                id = 2,
                personalEventId = eventId,
                iconName = "time",
                text = "Hora",
                value = "09:00 AM"
            ),
            createPersonalEventItem(
                id = 3,
                personalEventId = eventId,
                iconName = "person",
                text = "Organizador",
                value = "Coordinación Académica"
            )
        )
    }
}