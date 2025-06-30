package com.example.academically.data.repositorty

import com.example.academically.data.dao.OrganizationDao
import com.example.academically.data.entities.OrganizationEntity
import com.example.academically.data.entities.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

class OrganizationRepository(
    private val organizationDao: OrganizationDao
) {

    // ========== ORGANIZATIONS ==========

    /**
     * Obtiene todas las organizaciones guardadas localmente
     */
    fun getAllOrganizations(): Flow<List<OrganizationEntity>> {
        return organizationDao.getAllOrganizations()
    }

    /**
     * Obtiene una organización por ID
     */
    suspend fun getOrganizationById(organizationId: Int): OrganizationEntity? {
        return organizationDao.getOrganizationById(organizationId)
    }

    /**
     * Guarda una nueva organización
     */
    suspend fun saveOrganization(organization: OrganizationEntity) {
        organizationDao.insertOrganization(organization)
    }

    /**
     * Actualiza una organización existente
     */
    suspend fun updateOrganization(organization: OrganizationEntity) {
        organizationDao.updateOrganization(organization)
    }

    /**
     * Elimina una organización
     */
    suspend fun deleteOrganization(organization: OrganizationEntity) {
        organizationDao.deleteOrganization(organization)
    }

    /**
     * Elimina una organización por ID
     */
    suspend fun deleteOrganizationById(organizationId: Int) {
        organizationDao.deleteOrganizationById(organizationId)
    }

    /**
     * Obtiene el número total de organizaciones guardadas
     */
    suspend fun getOrganizationCount(): Int {
        return organizationDao.getOrganizationCount()
    }

    // ========== SUBSCRIPTIONS ==========

    /**
     * Obtiene las suscripciones de una organización
     */
    fun getSubscriptionsByOrganization(organizationId: Int): Flow<List<SubscriptionEntity>> {
        return organizationDao.getSubscriptionsByOrganization(organizationId)
    }

    /**
     * Obtiene todas las suscripciones activas
     */
    fun getAllActiveSubscriptions(): Flow<List<SubscriptionEntity>> {
        return organizationDao.getAllActiveSubscriptions()
    }

    /**
     * Guarda una nueva suscripción
     */
    suspend fun saveSubscription(subscription: SubscriptionEntity) {
        organizationDao.insertSubscription(subscription)
    }

    /**
     * Actualiza una suscripción
     */
    suspend fun updateSubscription(subscription: SubscriptionEntity) {
        organizationDao.updateSubscription(subscription)
    }

    /**
     * Elimina una suscripción
     */
    suspend fun deleteSubscription(organizationId: Int, channelId: Int) {
        organizationDao.deleteSubscription(organizationId, channelId)
    }

    /**
     * Verifica si está suscrito a un canal
     */
    suspend fun isSubscribedToChannel(organizationId: Int, channelId: Int): Boolean {
        return organizationDao.isSubscribedToChannel(organizationId, channelId)
    }

    /**
     * Guarda una organización con sus suscripciones
     */
    suspend fun saveOrganizationWithSubscriptions(
        organization: OrganizationEntity,
        subscriptions: List<SubscriptionEntity>
    ) {
        organizationDao.insertOrganizationWithSubscriptions(organization, subscriptions)
    }

    suspend fun deleteSubscriptionsByOrganization(organizationId: Int) {
        organizationDao.deleteSubscriptionsByOrganization(organizationId)
    }
}
