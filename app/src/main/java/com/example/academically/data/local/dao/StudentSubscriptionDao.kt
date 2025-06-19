package com.example.academically.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.academically.data.local.entities.StudentSubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentSubscriptionDao {

    @Query("SELECT * FROM student_subscriptions WHERE user_id = :userId AND is_active = 1")
    fun getUserSubscriptions(userId: Int = 1): Flow<List<StudentSubscriptionEntity>>

    @Query("SELECT COUNT(*) > 0 FROM student_subscriptions WHERE user_id = :userId AND channel_id = :channelId AND is_active = 1")
    suspend fun isSubscribedToChannel(channelId: Int, userId: Int = 1): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun subscribeToChannel(subscription: StudentSubscriptionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun subscribeToChannels(subscriptions: List<StudentSubscriptionEntity>)

    @Query("UPDATE student_subscriptions SET is_active = 0 WHERE user_id = :userId AND channel_id = :channelId")
    suspend fun unsubscribeFromChannel(channelId: Int, userId: Int = 1)

    @Query("UPDATE student_subscriptions SET notifications_enabled = :enabled WHERE user_id = :userId AND channel_id = :channelId")
    suspend fun updateNotificationSettings(channelId: Int, enabled: Boolean, userId: Int = 1)

    @Query("SELECT channel_id FROM student_subscriptions WHERE user_id = :userId AND is_active = 1")
    suspend fun getSubscribedChannelIds(userId: Int = 1): List<Int>

    @Query("DELETE FROM student_subscriptions WHERE user_id = :userId")
    suspend fun deleteAllSubscriptions(userId: Int = 1)

    @Query("UPDATE student_subscriptions SET synced_at = :syncTime WHERE user_id = :userId")
    suspend fun markAsSynced(syncTime: String, userId: Int = 1)
}
