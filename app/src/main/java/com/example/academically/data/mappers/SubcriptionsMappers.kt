package com.example.academically.data.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.academically.data.local.entities.ChannelEntity
import com.example.academically.data.local.entities.StudentSubscriptionEntity
import com.example.academically.data.model.PersonalEventType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class SubscriptionDisplayModel(
    val id: Int,
    val channelId: Int,
    val channelName: String,
    val organizationName: String,
    val isActive: Boolean,
    val notificationsEnabled: Boolean,
    val subscribedDate: String
)

fun StudentSubscriptionEntity.toDisplayModel(): SubscriptionDisplayModel {
    return SubscriptionDisplayModel(
        id = this.id,
        channelId = this.channelId,
        channelName = "Canal ${this.channelId}", // Placeholder - luego conectar con ChannelDao
        organizationName = "Organización", // Placeholder - luego conectar con OrganizationDao
        isActive = this.isActive,
        notificationsEnabled = this.notificationsEnabled,
        subscribedDate = formatSubscriptionDate(this.subscribedAt)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun createSampleChannelEntity(
    id: Int,
    organizationId: Int,
    name: String,
    acronym: String,
    type: String
): ChannelEntity {
    val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    return ChannelEntity(
        id = id,
        organizationId = organizationId,
        name = name,
        acronym = acronym,
        description = "",
        type = type,
        email = null,
        phone = null,
        isActive = true,
        cachedAt = now,
        createdAt = now,
        updatedAt = null
    )
}


@RequiresApi(Build.VERSION_CODES.O)
fun createSubscriptionEntity(
    channelId: Int,
    userId: Int = 1,
    notificationsEnabled: Boolean = true
): StudentSubscriptionEntity {
    val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    return StudentSubscriptionEntity(
        id = 0,
        userId = userId,
        channelId = channelId,
        subscribedAt = now,
        isActive = true,
        notificationsEnabled = notificationsEnabled,
        syncedAt = null
    )
}

private fun formatSubscriptionDate(dateString: String): String {
    return try {
        dateString.substring(0, 10) // YYYY-MM-DD
    } catch (e: Exception) {
        dateString
    }
}


private fun parsePersonalEventType(type: String): PersonalEventType {
    return try {
        PersonalEventType.valueOf(type.uppercase())
    } catch (e: IllegalArgumentException) {
        PersonalEventType.PERSONAL
    }
}

