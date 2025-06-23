package com.example.academically.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.academically.data.local.entities.ChannelEntity
import com.example.academically.data.local.entities.ChannelWithOrganization
import kotlinx.coroutines.flow.Flow


@Dao
interface ChannelDao {

    @Query("SELECT * FROM channels_cache WHERE is_active = 1 ORDER BY type ASC, name ASC")
    fun getAllChannels(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels_cache WHERE id = :channelId AND is_active = 1")
    fun getChannelById(channelId: Int): Flow<ChannelEntity?>

    @Query("""
        SELECT * FROM channels_cache 
        WHERE organization_id = :organizationId AND is_active = 1 
        ORDER BY type ASC, name ASC
    """)
    fun getChannelsByOrganization(organizationId: Int): Flow<List<ChannelEntity>>

    @Query("""
        SELECT * FROM channels_cache 
        WHERE type = :type AND is_active = 1 
        ORDER BY name ASC
    """)
    fun getChannelsByType(type: String): Flow<List<ChannelEntity>>

    @Query("""
        SELECT * FROM channels_cache 
        WHERE organization_id = :organizationId AND type = :type AND is_active = 1 
        ORDER BY name ASC
    """)
    fun getChannelsByOrganizationAndType(organizationId: Int, type: String): Flow<List<ChannelEntity>>

    @Query("""
        SELECT * FROM channels_cache 
        WHERE (name LIKE :searchQuery OR acronym LIKE :searchQuery) 
        AND is_active = 1 
        ORDER BY name ASC
    """)
    fun searchChannels(searchQuery: String): Flow<List<ChannelEntity>>

    @Query("""
        SELECT * FROM channels_cache 
        WHERE organization_id = :organizationId 
        AND (name LIKE :searchQuery OR acronym LIKE :searchQuery) 
        AND is_active = 1 
        ORDER BY name ASC
    """)
    fun searchChannelsByOrganization(organizationId: Int, searchQuery: String): Flow<List<ChannelEntity>>

    @Transaction
    @Query("SELECT * FROM channels_cache WHERE is_active = 1 ORDER BY name ASC")
    fun getChannelsWithOrganization(): Flow<List<ChannelWithOrganization>>

    @Transaction
    @Query("SELECT * FROM channels_cache WHERE id = :channelId AND is_active = 1")
    fun getChannelWithOrganization(channelId: Int): Flow<ChannelWithOrganization?>

    @Transaction
    @Query("""
        SELECT * FROM channels_cache 
        WHERE organization_id = :organizationId AND is_active = 1 
        ORDER BY type ASC, name ASC
    """)
    fun getChannelsWithOrganizationByOrganization(organizationId: Int): Flow<List<ChannelWithOrganization>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: ChannelEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<ChannelEntity>)

    @Update
    suspend fun updateChannel(channel: ChannelEntity)

    @Query("DELETE FROM channels_cache WHERE id = :channelId")
    suspend fun deleteChannel(channelId: Int)

    @Query("DELETE FROM channels_cache WHERE organization_id = :organizationId")
    suspend fun deleteChannelsByOrganization(organizationId: Int)

    @Query("DELETE FROM channels_cache")
    suspend fun deleteAllChannels()

    @Query("SELECT COUNT(*) FROM channels_cache WHERE is_active = 1")
    suspend fun getChannelCount(): Int

    @Query("SELECT COUNT(*) FROM channels_cache WHERE organization_id = :organizationId AND is_active = 1")
    suspend fun getChannelCountByOrganization(organizationId: Int): Int

    @Query("UPDATE channels_cache SET cached_at = :cachedAt WHERE id = :channelId")
    suspend fun updateCacheTime(channelId: Int, cachedAt: String)
}