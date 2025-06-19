package com.example.academically.data.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FormatListNumberedRtl
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.academically.data.local.entities.EventItemEntity
import com.example.academically.data.local.entities.PersonalEventEntity
import com.example.academically.data.local.entities.PersonalEventNotificationEntity
import com.example.academically.data.local.entities.PersonalEventWithDetails
import com.example.academically.data.local.entities.StudentSubscriptionEntity
import com.example.academically.data.local.entities.UserProfileEntity
import com.example.academically.data.model.PersonalEvent
import com.example.academically.data.model.PersonalEventItem
import com.example.academically.data.model.PersonalEventNotification
import com.example.academically.data.model.PersonalEventType
import com.example.academically.data.model.User
import com.example.academically.data.model.UserRole
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// ========== CONVERSIONES ENTITY -> DOMAIN MODEL ==========

@RequiresApi(Build.VERSION_CODES.O)
fun PersonalEventWithDetails.toDomainModel(): PersonalEvent {
    return PersonalEvent(
        id = this.event.id,
        title = this.event.title,
        shortDescription = this.event.shortDescription,
        longDescription = this.event.longDescription,
        location = this.event.location,
        colorIndex = this.event.colorIndex,
        startDate = LocalDate.parse(this.event.startDate),
        endDate = LocalDate.parse(this.event.endDate),
        type = parsePersonalEventType(this.event.type),
        institutionalEventId = this.event.institutionalEventId,
        imagePath = this.event.imagePath,
        items = this.items.map { it.toDomainModel() },
        notification = this.notification?.toDomainModel(),
        isVisible = this.event.isVisible,
        createdAt = this.event.createdAt,
        updatedAt = this.event.updatedAt
    )
}

private fun EventItemEntity.toDomainModel(): PersonalEventItem {
    return PersonalEventItem(
        id = this.id,
        personalEventId = this.eventId,
        iconName = this.iconName ?: "info",
        text = this.title,
        value = this.value,
        isClickable = this.isClickable
    )
}

private fun PersonalEventNotificationEntity.toDomainModel(): PersonalEventNotification {
    return PersonalEventNotification(
        id = this.id,
        personalEventId = this.eventId,
        time = this.minutesBefore.toLong() * 60 * 1000, // Convertir minutos a milisegundos
        title = this.title,
        message = this.message,
        isEnabled = this.isEnabled
    )
}

// ========== CONVERSIONES DOMAIN MODEL -> ENTITY ==========

@RequiresApi(Build.VERSION_CODES.O)
fun PersonalEvent.toEntity(): PersonalEventEntity {
    val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    return PersonalEventEntity(
        id = this.id,
        title = this.title,
        shortDescription = this.shortDescription,
        longDescription = this.longDescription,
        location = this.location,
        colorIndex = this.colorIndex,
        startDate = this.startDate.toString(),
        endDate = this.endDate.toString(),
        type = this.type.name,
        institutionalEventId = this.institutionalEventId,
        imagePath = this.imagePath,
        tags = Json.encodeToString(emptyList<String>()), // Tags vacíos por defecto
        isVisible = this.isVisible,
        shape = "RoundedFull", // Por defecto
        createdAt = this.createdAt.ifEmpty { now },
        updatedAt = this.updatedAt
    )
}

fun PersonalEventItem.toEntity(): EventItemEntity {
    return EventItemEntity(
        id = this.id,
        eventId = this.personalEventId,
        type = "NOTES", // Tipo por defecto
        title = this.text,
        value = this.value,
        description = "",
        isClickable = this.isClickable,
        iconName = this.iconName,
        orderIndex = 0
    )
}

fun PersonalEventNotification.toEntity(): PersonalEventNotificationEntity {
    return PersonalEventNotificationEntity(
        id = this.id,
        eventId = this.personalEventId,
        minutesBefore = (this.time / (60 * 1000)).toInt(), // Convertir milisegundos a minutos
        title = this.title,
        message = this.message,
        isEnabled = this.isEnabled,
        notificationType = "LOCAL"
    )
}

// ========== FUNCIONES DE MAPEO Y UTILIDADES ==========

private fun parsePersonalEventType(type: String): PersonalEventType {
    return try {
        PersonalEventType.valueOf(type.uppercase())
    } catch (e: IllegalArgumentException) {
        PersonalEventType.PERSONAL
    }
}

fun getIconByName(name: String): ImageVector {
    return when (name.lowercase()) {
        "person" -> Icons.Default.Person
        "call", "phone" -> Icons.Default.Call
        "email", "mail" -> Icons.Default.Email
        "location", "place" -> Icons.Default.Place
        "time", "schedule" -> Icons.Default.Schedule
        "link", "website" -> Icons.Default.Link
        "attachment", "file" -> Icons.Default.AttachFile
        "notification", "alarm" -> Icons.Default.Notifications
        "calendar", "event" -> Icons.Default.Event
        "school", "education" -> Icons.Default.School
        "work", "business" -> Icons.Default.Work
        "home" -> Icons.Default.Home
        "star", "favorite" -> Icons.Default.Star
        "warning" -> Icons.Default.Warning
        "check", "done" -> Icons.Default.Check
        "edit" -> Icons.Default.Edit
        "delete" -> Icons.Default.Delete
        "add" -> Icons.Default.Add
        "settings" -> Icons.Default.Settings
        "video" -> Icons.Default.PlayArrow
        "share" -> Icons.Default.Share
        "chat" -> Icons.Default.ChatBubble
        "list" -> Icons.Default.FormatListNumberedRtl
        "payment" -> Icons.Default.Payment
        "group" -> Icons.Default.Group
        else -> Icons.Default.Info
    }
}

private fun getIconName(icon: ImageVector): String {
    return when (icon) {
        Icons.Default.Person -> "person"
        Icons.Default.Call -> "call"
        Icons.Default.Email -> "email"
        Icons.Default.Place -> "location"
        Icons.Default.Schedule -> "time"
        Icons.Default.Link -> "link"
        Icons.Default.AttachFile -> "attachment"
        Icons.Default.Notifications -> "notification"
        Icons.Default.Event -> "calendar"
        Icons.Default.School -> "school"
        Icons.Default.Work -> "work"
        Icons.Default.Home -> "home"
        Icons.Default.Star -> "star"
        Icons.Default.Warning -> "warning"
        Icons.Default.Check -> "check"
        Icons.Default.Edit -> "edit"
        Icons.Default.Delete -> "delete"
        Icons.Default.Add -> "add"
        Icons.Default.Settings -> "settings"
        Icons.Default.PlayArrow -> "video"
        Icons.Default.Share -> "share"
        Icons.Default.ChatBubble -> "chat"
        Icons.Default.FormatListNumberedRtl -> "list"
        Icons.Default.Payment -> "payment"
        Icons.Default.Group -> "group"
        else -> "info"
    }
}

// ========== MAPPERS PARA PERFIL ESTUDIANTIL LOCAL ==========

// Modelo local simple para el perfil (sin dependencias del API)
data class LocalStudentProfile(
    val id: Int = 1,
    val googleUserId: String,
    val name: String,
    val email: String,
    val profilePicture: String? = null,
    val notificationsEnabled: Boolean = true,
    val syncEnabled: Boolean = false,
    val lastSyncAt: String? = null,
    val createdAt: String,
    val updatedAt: String? = null
)

// Modelo local simple para suscripciones (sin dependencias del API)
data class LocalStudentSubscription(
    val id: Int,
    val studentId: Int = 1,
    val channelId: Int,
    val channelName: String,
    val organizationName: String,
    val subscribedAt: String,
    val isActive: Boolean = true,
    val notificationsEnabled: Boolean = true
)



@RequiresApi(Build.VERSION_CODES.O)
fun PersonalEvent.toEntity(userId: Int = 1): PersonalEventEntity {
    val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    return PersonalEventEntity(
        id = this.id,
        title = this.title,
        shortDescription = this.shortDescription,
        longDescription = this.longDescription,
        location = this.location,
        colorIndex = this.colorIndex,
        startDate = this.startDate.toString(),
        endDate = this.endDate.toString(),
        type = this.type.name,
        institutionalEventId = this.institutionalEventId,
        imagePath = this.imagePath,
        tags = Json.encodeToString(emptyList<String>()),
        isVisible = this.isVisible,
        shape = "RoundedFull",
        createdAt = this.createdAt.ifEmpty { now },
        updatedAt = this.updatedAt
    )
}


// ========== HELPERS PARA CREACIÓN DE EVENTOS DE EJEMPLO ==========

@RequiresApi(Build.VERSION_CODES.O)
fun createSamplePersonalEvent(
    title: String,
    description: String,
    startDate: LocalDate,
    endDate: LocalDate = startDate,
    colorIndex: Int = 0,
    type: PersonalEventType = PersonalEventType.PERSONAL
): PersonalEvent {
    val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    return PersonalEvent(
        id = 0, // Se asignará automáticamente
        title = title,
        shortDescription = description,
        longDescription = "",
        location = "",
        colorIndex = colorIndex,
        startDate = startDate,
        endDate = endDate,
        type = type,
        institutionalEventId = null,
        imagePath = "",
        items = emptyList(),
        notification = null,
        isVisible = true,
        createdAt = now,
        updatedAt = null
    )
}


@RequiresApi(Build.VERSION_CODES.O)
fun PersonalEventEntity.toDomainModel(): PersonalEvent {
    return PersonalEvent(
        id = this.id,
        title = this.title,
        shortDescription = this.shortDescription,
        longDescription = this.longDescription,
        location = this.location,
        colorIndex = this.colorIndex,
        startDate = LocalDate.parse(this.startDate),
        endDate = LocalDate.parse(this.endDate),
        type = parsePersonalEventType(this.type),
        institutionalEventId = this.institutionalEventId,
        imagePath = this.imagePath,
        items = emptyList(), // Se carga por separado en PersonalEventWithDetails
        notification = null, // Se carga por separado en PersonalEventWithDetails
        isVisible = this.isVisible,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun LocalStudentSubscription.toEntity(): StudentSubscriptionEntity {
    return StudentSubscriptionEntity(
        id = this.id,
        userId = this.studentId,
        channelId = this.channelId,
        subscribedAt = this.subscribedAt,
        isActive = this.isActive,
        notificationsEnabled = this.notificationsEnabled,
        syncedAt = null
    )
}

fun StudentSubscriptionEntity.toLocalDomainModel(): LocalStudentSubscription {
    return LocalStudentSubscription(
        id = this.id,
        studentId = this.userId,
        channelId = this.channelId,
        channelName = "Canal ${this.channelId}", // Placeholder - luego conectar con ChannelDao
        organizationName = "Organización", // Placeholder - luego conectar con OrganizationDao
        subscribedAt = this.subscribedAt,
        isActive = this.isActive,
        notificationsEnabled = this.notificationsEnabled
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalStudentProfile.toEntity(): UserProfileEntity {
    return UserProfileEntity(
        id = this.id,
        googleId = this.googleUserId,
        email = this.email,
        name = this.name,
        profilePicture = this.profilePicture,
        role = UserRole.STUDENT.name,
        isActive = true,
        notificationsEnabled = this.notificationsEnabled,
        syncEnabled = this.syncEnabled,
        authToken = null,
        tokenExpiresAt = null,
        createdAt = this.createdAt,
        lastLoginAt = null,
        lastSyncAt = this.lastSyncAt,
        updatedAt = this.updatedAt
    )
}

fun UserProfileEntity.toLocalDomainModel(): LocalStudentProfile {
    return LocalStudentProfile(
        id = this.id,
        googleUserId = this.googleId,
        name = this.name,
        email = this.email,
        profilePicture = this.profilePicture,
        notificationsEnabled = this.notificationsEnabled,
        syncEnabled = this.syncEnabled,
        lastSyncAt = this.lastSyncAt,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun String.fromTagsJson(): List<String> {
    return try {
        Json.decodeFromString<List<String>>(this)
    } catch (e: Exception) {
        emptyList()
    }
}

fun List<String>.toTagsJson(): String {
    return try {
        Json.encodeToString(this)
    } catch (e: Exception) {
        "[]"
    }
}
