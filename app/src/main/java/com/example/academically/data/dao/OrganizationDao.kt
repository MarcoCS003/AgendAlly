package com.example.academically.data.dao


import androidx.room.*
import com.example.academically.data.entities.OrganizationEntity
import com.example.academically.data.entities.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrganizationDao {

    // ========== ORGANIZATIONS ==========

    @Query("SELECT * FROM organizations WHERE is_active = 1 ORDER BY name ASC")
    fun getAllOrganizations(): Flow<List<OrganizationEntity>>

    @Query("SELECT * FROM organizations WHERE organization_id = :organizationId")
    suspend fun getOrganizationById(organizationId: Int): OrganizationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrganization(organization: OrganizationEntity)

    @Update
    suspend fun updateOrganization(organization: OrganizationEntity)

    @Delete
    suspend fun deleteOrganization(organization: OrganizationEntity)

    @Query("DELETE FROM organizations WHERE organization_id = :organizationId")
    suspend fun deleteOrganizationById(organizationId: Int)

    @Query("SELECT COUNT(*) FROM organizations WHERE is_active = 1")
    suspend fun getOrganizationCount(): Int

    // ========== SUBSCRIPTIONS ==========

    @Query("SELECT * FROM subscriptions WHERE organization_id = :organizationId AND is_active = 1")
    fun getSubscriptionsByOrganization(organizationId: Int): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE is_active = 1")
    fun getAllActiveSubscriptions(): Flow<List<SubscriptionEntity>>

    @Query("DELETE FROM subscriptions WHERE organization_id = :organizationId")
    suspend fun deleteSubscriptionsByOrganization(organizationId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionEntity)

    @Update
    suspend fun updateSubscription(subscription: SubscriptionEntity)

    @Delete
    suspend fun deleteSubscription(subscription: SubscriptionEntity)

    @Query("DELETE FROM subscriptions WHERE organization_id = :organizationId AND channel_id = :channelId")
    suspend fun deleteSubscription(organizationId: Int, channelId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM subscriptions WHERE organization_id = :organizationId AND channel_id = :channelId AND is_active = 1)")
    suspend fun isSubscribedToChannel(organizationId: Int, channelId: Int): Boolean

    // ========== UTILIDADES ==========

    @Transaction
    suspend fun insertOrganizationWithSubscriptions(
        organization: OrganizationEntity,
        subscriptions: List<SubscriptionEntity>
    ) {
        insertOrganization(organization)
        subscriptions.forEach { subscription ->
            insertSubscription(subscription.copy(organizationId = organization.organizationID))
        }
    }
}