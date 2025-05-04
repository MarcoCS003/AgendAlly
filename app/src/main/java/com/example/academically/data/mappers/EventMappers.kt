package com.example.academically.data.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.academically.data.Event
import com.example.academically.data.EventCategory
import com.example.academically.data.EventItem
import com.example.academically.data.EventNotification
import com.example.academically.data.EventShape
import com.example.academically.data.entities.EventEntity
import com.example.academically.data.entities.EventItemEntity
import com.example.academically.data.entities.EventNotificationEntity
import com.example.academically.data.entities.EventWithDetails
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
fun EventWithDetails.toDomainModel(): Event {
    return Event(
        id = this.event.id,
        title = this.event.title,
        shortDescription = this.event.shortDescription,
        longDescription = this.event.longDescription,
        location = this.event.location,
        colorIndex = this.event.colorIndex,
        startDate = if (this.event.startDate.isNotEmpty()) LocalDate.parse(this.event.startDate) else null,
        endDate = if (this.event.endDate.isNotEmpty()) LocalDate.parse(this.event.endDate) else null,
        category = when (this.event.categoryId) {
            1 -> EventCategory.INSTITUTIONAL
            2 -> EventCategory.CAREER
            else -> EventCategory.PERSONAL
        },
        imagePath = this.event.imagePath,
        items = this.items.map { it.toDomainModel() },
        notification = this.notification?.toDomainModel(),
        shape = parseEventShape(this.event.shape)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun Event.toEntity(): EventEntity {
    return EventEntity(
        id = this.id,
        title = this.title,
        shortDescription = this.shortDescription,
        longDescription = this.longDescription,
        location = this.location,
        colorIndex = this.colorIndex,
        startDate = this.startDate?.toString() ?: "",
        endDate = this.endDate?.toString() ?: "",
        categoryId = this.category.id,
        imagePath = this.imagePath,
        shape = this.shape.javaClass.simpleName
    )
}

private fun EventItemEntity.toDomainModel(): EventItem {
    // Conversión del nombre del icono a ImageVector
    val iconVector = getIconByName(this.iconName)

    return EventItem(
        id = this.id,
        icon = iconVector,
        text = this.text
    )
}

fun EventItem.toEntity(eventId: Int): EventItemEntity {
    return EventItemEntity(
        id = this.id,
        eventId = eventId,
        iconName = getIconName(this.icon),
        text = this.text
    )
}

private fun EventNotificationEntity.toDomainModel(): EventNotification {
    return EventNotification(
        id = this.id,
        time = this.time,
        title = this.title,
        message = this.message,
        isEnabled = this.isEnabled
    )
}

fun EventNotification.toEntity(eventId: Int): EventNotificationEntity {
    return EventNotificationEntity(
        id = this.id,
        eventId = eventId,
        time = this.time,
        title = this.title,
        message = this.message,
        isEnabled = this.isEnabled
    )
}

// Funciones de ayuda para manejar tipos complejos
private fun parseEventShape(shapeName: String): EventShape {
    return when (shapeName) {
        "Circle" -> EventShape.Circle
        "RoundedStart" -> EventShape.RoundedStart
        "RoundedMiddle" -> EventShape.RoundedMiddle
        "RoundedEnd" -> EventShape.RoundedEnd
        else -> EventShape.RoundedFull
    }
}

private fun getIconByName(name: String): ImageVector {
    // Lógica para convertir nombre de icono a ImageVector
    return when (name) {
        "Person" -> Icons.Default.Person
        "Call" -> Icons.Default.Call
        // Añadir más casos según sea necesario
        else -> Icons.Default.Info
    }
}

private fun getIconName(icon: ImageVector): String {
    // Lógica para obtener nombre de icono desde ImageVector
    return when (icon) {
        Icons.Default.Person -> "Person"
        Icons.Default.Call -> "Call"
        // Añadir más casos según sea necesario
        else -> "Info"
    }
}