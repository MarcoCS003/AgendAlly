package com.example.academically.data.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import com.example.academically.data.model.*
import com.example.academically.data.remote.api.*
import com.example.academically.data.local.entities.*
import com.example.academically.data.model.Organization
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// ============== API TO DOMAIN MAPPERS ==============

fun ApiUser.toDomainModel(): User {
    return User(
        id = this.id,
        googleId = this.googleId,
        email = this.email,
        name = this.name,
        profilePicture = this.profilePicture,
        role = UserRole.valueOf(this.role),
        isActive = this.isActive,
        createdAt = this.createdAt,
        lastLoginAt = this.lastLoginAt
    )
}

fun ApiOrganization.toDomainModel(): Organization {
    return Organization(
        organizationID = this.organizationID,
        acronym = this.acronym,
        name = this.name,
        address = this.address,
        email = this.email,
        phone = this.phone,
        studentNumber = this.studentNumber,
        teacherNumber = this.teacherNumber,
        logo = null,
        webSite = this.webSite,
        facebook = this.facebook,
        instagram = this.instagram,
        twitter = this.twitter,
        youtube = this.youtube,
        channels = this.channels.map { it.toDomainModel() }
    )
}

fun ApiOrganization.toUIOrganization(): Organization {
    return Organization(
        organizationID = this.organizationID,
        acronym = this.acronym,
        name = this.name,
        address = this.address,
        email = this.email,
        phone = this.phone,
        studentNumber = this.studentNumber,
        teacherNumber = this.teacherNumber,
        logo = null,
        webSite = this.webSite,
        facebook = this.facebook,
        instagram = this.instagram,
        twitter = this.twitter,
        youtube = this.youtube,
        channels = this.channels.map { it.toUIChannel() }
    )
}

fun ApiChannel.toDomainModel(): Channel {
    return Channel(
        channelID = this.id,
        organizationId = this.organizationId,
        name = this.name,
        acronym = this.acronym,
        description = this.description,
        type = when(this.type) {
            "CAREER" -> ChannelType.CAREER
            "DEPARTMENT" -> ChannelType.DEPARTMENT
            "ADMINISTRATIVE" -> ChannelType.ADMINISTRATIVE
            else -> ChannelType.CAREER
        },
        email = this.email,
        phone = this.phone,
        isActive = this.isActive
    )
}

fun ApiChannel.toUIChannel(): Channel {
    return Channel(
        channelID = this.id,
        organizationId = this.organizationId,
        name = this.name,
        acronym = this.acronym,
        description = this.description,
        type = when(this.type) {
            "CAREER" -> ChannelType.CAREER
            "DEPARTMENT" -> ChannelType.DEPARTMENT
            "ADMINISTRATIVE" -> ChannelType.ADMINISTRATIVE
            else -> ChannelType.CAREER
        },
        email = this.email,
        phone = this.phone,
        isActive = this.isActive
    )
}

fun ApiUserSubscription.toDomainModel(): UserSubscriptionDomain {
    return UserSubscriptionDomain(
        id = this.id,
        userId = this.userId,
        channelId = this.channelId,
        channelName = this.channelName,
        channelType = this.channelType,
        organizationName = this.organizationName,
        subscribedAt = this.subscribedAt,
        isActive = this.isActive,
        notificationsEnabled = this.notificationsEnabled,
        syncedAt = this.syncedAt
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun ApiEvent.toDomainModel(): EventOrganization {
    return EventOrganization(
        id = this.id,
        title = this.title,
        shortDescription = this.shortDescription,
        longDescription = this.longDescription,
        location = this.location,
        color = Color.Blue,
        startDate = this.startDate?.let { LocalDate.parse(it) },
        endDate = this.endDate?.let { LocalDate.parse(it) },
        category = when(this.category) {
            "INSTITUTIONAL" -> PersonalEventType.SUBSCRIBED
            "CAREER" -> PersonalEventType.SUBSCRIBED
            "DEPARTMENT" -> PersonalEventType.SUBSCRIBED
            else -> PersonalEventType.SUBSCRIBED
        },
        imagePath = this.imagePath,
        items = this.items.map { it.toDomainModel() },
        notification = null,
        mesID = null,
        shape = EventShape.RoundedFull,
        organizationId = this.id
    )
}

fun ApiEventItem.toDomainModel(): PersonalEventItem {
    return PersonalEventItem(
        id = this.id,
        personalEventId = 0,
        iconName = this.iconName ?: "info",
        text = this.title,
        value = this.value,
        isClickable = this.isClickable
    )
}

// ============== API TO ENTITY MAPPERS ==============

@RequiresApi(Build.VERSION_CODES.O)
fun ApiOrganization.toEntity(): OrganizationEntity {
    val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    return OrganizationEntity(
        id = this.organizationID,
        acronym = this.acronym,
        name = this.name,
        description = this.description,
        address = this.address,
        email = this.email,
        phone = this.phone,
        studentNumber = this.studentNumber,
        teacherNumber = this.teacherNumber,
        website = this.webSite,
        logoUrl = this.logoUrl,
        facebook = this.facebook,
        instagram = this.instagram,
        twitter = this.twitter,
        youtube = this.youtube,
        linkedin = this.linkedin,
        isActive = this.isActive,
        cachedAt = now,
        createdAt = this.createdAt ?: now,
        updatedAt = this.updatedAt
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun ApiChannel.toEntity(): ChannelEntity {
    val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    return ChannelEntity(
        id = this.id,
        organizationId = this.organizationId,
        name = this.name,
        acronym = this.acronym,
        description = this.description,
        type = this.type,
        email = this.email,
        phone = this.phone,
        isActive = this.isActive,
        cachedAt = now,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun ApiUserSubscription.toEntity(): StudentSubscriptionEntity {
    return StudentSubscriptionEntity(
        id = this.id,
        userId = this.userId,
        channelId = this.channelId,
        subscribedAt = this.subscribedAt,
        isActive = this.isActive,
        notificationsEnabled = this.notificationsEnabled,
        syncedAt = this.syncedAt
    )
}

// ============== ENTITY TO DOMAIN MAPPERS ==============

fun OrganizationEntity.toDomainModel(): Organization {
    return Organization(
        organizationID = this.id,
        acronym = this.acronym,
        name = this.name,
        address = this.address,
        email = this.email,
        phone = this.phone,
        studentNumber = this.studentNumber,
        teacherNumber = this.teacherNumber,
        logo = null,
        webSite = this.website,
        facebook = this.facebook,
        instagram = this.instagram,
        twitter = this.twitter,
        youtube = this.youtube,
        channels = emptyList() // Se carga por separado
    )
}

fun OrganizationEntity.toUIOrganization(): Organization {
    return Organization(
        organizationID = this.id,
        acronym = this.acronym,
        name = this.name,
        address = this.address,
        email = this.email,
        phone = this.phone,
        studentNumber = this.studentNumber,
        teacherNumber = this.teacherNumber,
        logo = null,
        webSite = this.website,
        facebook = this.facebook,
        instagram = this.instagram,
        twitter = this.twitter,
        youtube = this.youtube,
        channels = emptyList()
    )
}

fun ChannelEntity.toDomainModel(): Channel {
    return Channel(
        channelID = this.id,
        organizationId = this.organizationId,
        name = this.name,
        acronym = this.acronym,
        description = this.description,
        type = when(this.type) {
            "CAREER" -> ChannelType.CAREER
            "DEPARTMENT" -> ChannelType.DEPARTMENT
            "ADMINISTRATIVE" -> ChannelType.ADMINISTRATIVE
            else -> ChannelType.CAREER
        },
        email = this.email,
        phone = this.phone,
        isActive = this.isActive
    )
}

fun ChannelEntity.toUIChannel(): Channel {
    return Channel(
        channelID = this.id,
        organizationId = this.organizationId,
        name = this.name,
        acronym = this.acronym,
        description = this.description,
        type = when(this.type) {
            "CAREER" -> ChannelType.CAREER
            "DEPARTMENT" -> ChannelType.DEPARTMENT
            "ADMINISTRATIVE" -> ChannelType.ADMINISTRATIVE
            else -> ChannelType.CAREER
        },
        email = this.email,
        phone = this.phone,
        isActive = this.isActive
    )
}

fun StudentSubscriptionEntity.toDomainModel(): LocalStudentSubscription {
    return LocalStudentSubscription(
        id = this.id,
        studentId = this.userId,
        channelId = this.channelId,
        channelName = "Canal ${this.channelId}", // Se completará con join
        organizationName = "Organización", // Se completará con join
        subscribedAt = this.subscribedAt,
        isActive = this.isActive,
        notificationsEnabled = this.notificationsEnabled
    )
}

// ============== COMPLEX ENTITY MAPPERS ==============

fun OrganizationWithChannels.toDomainModel(): Organization {
    return Organization(
        organizationID = this.organization.id,
        acronym = this.organization.acronym,
        name = this.organization.name,
        address = this.organization.address,
        email = this.organization.email,
        phone = this.organization.phone,
        studentNumber = this.organization.studentNumber,
        teacherNumber = this.organization.teacherNumber,
        logo = null,
        webSite = this.organization.website,
        facebook = this.organization.facebook,
        instagram = this.organization.instagram,
        twitter = this.organization.twitter,
        youtube = this.organization.youtube,
        channels = this.channels.map { it.toDomainModel() }
    )
}

fun OrganizationWithChannels.toUIOrganization(): Organization {
    return Organization(
        organizationID = this.organization.id,
        acronym = this.organization.acronym,
        name = this.organization.name,
        address = this.organization.address,
        email = this.organization.email,
        phone = this.organization.phone,
        studentNumber = this.organization.studentNumber,
        teacherNumber = this.organization.teacherNumber,
        logo = null,
        webSite = this.organization.website,
        facebook = this.organization.facebook,
        instagram = this.organization.instagram,
        twitter = this.organization.twitter,
        youtube = this.organization.youtube,
        channels = this.channels.map { it.toUIChannel() }
    )
}

fun ChannelWithOrganization.toDomainModel(): ChannelDomain {
    return ChannelDomain(
        id = this.channel.id,
        organizationId = this.channel.organizationId,
        organizationName = this.organization.name,
        name = this.channel.name,
        acronym = this.channel.acronym,
        description = this.channel.description,
        type = this.channel.type,
        email = this.channel.email,
        phone = this.channel.phone,
        isActive = this.channel.isActive,
        createdAt = this.channel.createdAt,
        updatedAt = this.channel.updatedAt
    )
}

fun SubscriptionWithChannelAndOrganization.toDomainModel(): UserSubscriptionDomain {
    return UserSubscriptionDomain(
        id = this.subscription.id,
        userId = this.subscription.userId,
        channelId = this.subscription.channelId,
        channelName = this.channelWithOrganization.channel.name,
        channelType = this.channelWithOrganization.channel.type,
        organizationName = this.channelWithOrganization.organization.name,
        subscribedAt = this.subscription.subscribedAt,
        isActive = this.subscription.isActive,
        notificationsEnabled = this.subscription.notificationsEnabled,
        syncedAt = this.subscription.syncedAt
    )
}

// ============== DOMAIN MODELS ==============

data class ChannelDomain(
    val id: Int,
    val organizationId: Int,
    val organizationName: String,
    val name: String,
    val acronym: String,
    val description: String = "",
    val type: String,
    val email: String? = null,
    val phone: String? = null,
    val isActive: Boolean = true,
    val createdAt: String,
    val updatedAt: String? = null
)

data class UserSubscriptionDomain(
    val id: Int,
    val userId: Int,
    val channelId: Int,
    val channelName: String,
    val channelType: String,
    val organizationName: String,
    val subscribedAt: String,
    val isActive: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val syncedAt: String? = null
)

// ============== UTILITY EXTENSIONS ==============

