package com.example.academically.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.academically.data.local.entities.StudentSubscriptionEntity
import com.example.academically.data.local.entities.UserProfileEntity
import com.example.academically.data.local.entities.UserProfileWithSubscriptions
import kotlinx.coroutines.flow.Flow



@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Transaction
    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileWithSubscriptions(): UserProfileWithSubscriptions?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateUserProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET auth_token = :token, token_expires_at = :expiresAt WHERE id = 1")
    suspend fun updateAuthToken(token: String, expiresAt: String)

    @Query("UPDATE user_profile SET notifications_enabled = :enabled WHERE id = 1")
    suspend fun updateNotificationsEnabled(enabled: Boolean)

    @Query("UPDATE user_profile SET sync_enabled = :enabled, last_sync_at = :syncAt WHERE id = 1")
    suspend fun updateSyncSettings(enabled: Boolean, syncAt: String?)

    @Query("SELECT auth_token FROM user_profile WHERE id = 1 AND token_expires_at > :currentTime")
    suspend fun getValidAuthToken(currentTime: String): String?

    @Query("UPDATE user_profile SET auth_token = NULL, token_expires_at = NULL, last_login_at = :logoutTime WHERE id = 1")
    suspend fun logout(logoutTime: String)

    @Query("DELETE FROM user_profile")
    suspend fun deleteUserProfile()

    @Query("SELECT COUNT(*) > 0 FROM user_profile WHERE id = 1 AND auth_token IS NOT NULL")
    suspend fun isUserLoggedIn(): Boolean
}
