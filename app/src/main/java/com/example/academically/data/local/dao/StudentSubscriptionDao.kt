package com.example.academically.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.academically.data.local.entities.StudentSubscriptionEntity
import com.example.academically.data.local.entities.SubscriptionWithChannelAndOrganization
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentSubscriptionDao {

    @Query("SELECT * FROM student_subscriptions WHERE user_id = :userId AND is_active = 1")
    fun getUserSubscriptions(userId: Int): Flow<List<StudentSubscriptionEntity>>

    @Query("SELECT * FROM student_subscriptions WHERE id = :subscriptionId")
    fun getSubscriptionById(subscriptionId: Int): Flow<StudentSubscriptionEntity?>

    @Query("""
        SELECT * FROM student_subscriptions 
        WHERE user_id = :userId AND channel_id = :channelId
    """)
    fun getSubscription(userId: Int, channelId: Int): Flow<StudentSubscriptionEntity?>

    @Query("""
        SELECT * FROM student_subscriptions 
        WHERE user_id = :userId AND channel_id = :channelId AND is_active = 1
    """)
    fun getActiveSubscription(userId: Int, channelId: Int): Flow<StudentSubscriptionEntity?>

    @Transaction
    @Query("SELECT * FROM student_subscriptions WHERE user_id = :userId AND is_active = 1")
    fun getUserSubscriptionsWithChannels(userId: Int): Flow<List<SubscriptionWithChannelAndOrganization>>

    @Query("""
        SELECT channel_id FROM student_subscriptions 
        WHERE user_id = :userId AND is_active = 1
    """)
    fun getSubscribedChannelIds(userId: Int): Flow<List<Int>>

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM student_subscriptions 
            WHERE user_id = :userId AND channel_id = :channelId AND is_active = 1
        )
    """)
    suspend fun isSubscribedToChannel(userId: Int, channelId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun subscribeToChannel(subscription: StudentSubscriptionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun subscribeToChannels(subscriptions: List<StudentSubscriptionEntity>)

    @Query("""
        UPDATE student_subscriptions 
        SET is_active = 0 
        WHERE user_id = :userId AND channel_id = :channelId
    """)
    suspend fun unsubscribeFromChannel(channelId: Int, userId: Int)

    @Query("""
        UPDATE student_subscriptions 
        SET notifications_enabled = :enabled 
        WHERE user_id = :userId AND channel_id = :channelId
    """)
    suspend fun updateNotifications(userId: Int, channelId: Int, enabled: Boolean)

    @Query("""
        UPDATE student_subscriptions 
        SET synced_at = :syncedAt 
        WHERE user_id = :userId AND channel_id = :channelId
    """)
    suspend fun updateSyncTime(userId: Int, channelId: Int, syncedAt: String)

    @Update
    suspend fun updateSubscription(subscription: StudentSubscriptionEntity)

    @Query("DELETE FROM student_subscriptions WHERE user_id = :userId")
    suspend fun deleteUserSubscriptions(userId: Int)

    @Query("DELETE FROM student_subscriptions WHERE channel_id = :channelId")
    suspend fun deleteChannelSubscriptions(channelId: Int)

    @Query("DELETE FROM student_subscriptions")
    suspend fun deleteAllSubscriptions()

    @Query("SELECT COUNT(*) FROM student_subscriptions WHERE user_id = :userId AND is_active = 1")
    suspend fun getUserSubscriptionCount(userId: Int): Int
}