package com.agendally.app.data.mappers

import com.agendally.app.data.api.Organization
import com.agendally.app.data.api.Channel
import com.agendally.app.data.entities.OrganizationEntity
import com.agendally.app.data.entities.SubscriptionEntity

/**
 * Convierte Organization (API) a OrganizationEntity (Room)
 */
fun Organization.toEntity(): OrganizationEntity {
    return OrganizationEntity(
        organizationID = this.organizationID,
        acronym = this.acronym,
        name = this.name,
        description = this.description,
        address = this.address,
        email = this.email,
        phone = this.phone,
        studentNumber = this.studentNumber,
        teacherNumber = this.teacherNumber,
        logoUrl = this.logoUrl,
        webSite = this.webSite,
        facebook = this.facebook,
        instagram = this.instagram,
        twitter = this.twitter,
        youtube = this.youtube,
        linkedin = this.linkedin,
        isActive = this.isActive,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * Convierte OrganizationEntity (Room) a Organization (API)
 */
fun OrganizationEntity.toApiModel(): Organization {
    return Organization(
        organizationID = this.organizationID,
        acronym = this.acronym,
        name = this.name,
        description = this.description,
        address = this.address,
        email = this.email,
        phone = this.phone,
        studentNumber = this.studentNumber,
        teacherNumber = this.teacherNumber,
        logoUrl = this.logoUrl,
        webSite = this.webSite,
        facebook = this.facebook,
        instagram = this.instagram,
        twitter = this.twitter,
        youtube = this.youtube,
        linkedin = this.linkedin,
        channels = emptyList(), // Se llenarán desde subscriptions si es necesario
        isActive = this.isActive,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * Convierte Channel (API) a SubscriptionEntity (Room)
 */
fun Channel.toSubscriptionEntity(organizationId: Int): SubscriptionEntity {
    return SubscriptionEntity(
        organizationId = organizationId,
        channelId = this.id,
        channelName = this.name,
        channelAcronym = this.acronym,
        isActive = true,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * Convierte una Organization con sus channels a entities
 */
fun Organization.toEntityWithSubscriptions(): Pair<OrganizationEntity, List<SubscriptionEntity>> {
    val organizationEntity = this.toEntity()
    val subscriptions = this.channels.map { channel ->
        channel.toSubscriptionEntity(this.organizationID)
    }
    return Pair(organizationEntity, subscriptions)
}