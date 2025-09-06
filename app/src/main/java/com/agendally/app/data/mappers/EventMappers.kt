package com.agendally.app.data.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.agendally.app.data.Event
import com.agendally.app.data.EventItem
import com.agendally.app.data.EventNotification
import com.agendally.app.data.EventShape
import com.agendally.app.data.entities.EventEntity
import com.agendally.app.data.entities.EventItemEntity
import com.agendally.app.data.entities.EventNotificationEntity
import com.agendally.app.data.entities.EventWithDetails
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
        category = this.event.categoryId,
        imagePath = this.event.imagePath,
        items = this.items.map { it.toDomainModel() },
        notification = this.notification?.toDomainModel(),
        shape = parseEventShape(this.event.shape!!),
        organizationId = this.event.organizationId,
        isActive = this.event.isActive
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
        categoryId = this.category,
        imagePath = this.imagePath,
        shape = this.shape.javaClass.simpleName,
        organizationId = this.organizationId,
        isActive = this.isActive
    )
}

private fun EventItemEntity.toDomainModel(): EventItem {
    // Conversión del nombre del icono a ImageVector
    val iconVector = getIconByName(this.iconName)

    return EventItem(
        id = this.id,
        icon = iconVector,
        text = this.text,
        value = "",
        isClickable = true
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