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

    @Query("SELECT * FROM channels_cache WHERE is_active = 1 ORDER BY name ASC")
    fun getAllChannels(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels_cache WHERE organization_id = :organizationId AND is_active = 1 ORDER BY type ASC, name ASC")
    fun getChannelsByOrganization(organizationId: Int): Flow<List<ChannelEntity>>

    @Transaction
    @Query("SELECT * FROM channels_cache WHERE organization_id = :organizationId AND is_active = 1")
    fun getChannelsWithOrganization(organizationId: Int): Flow<List<ChannelWithOrganization>>

    @Query("SELECT * FROM channels_cache WHERE id = :id")
    suspend fun getChannelById(id: Int): ChannelEntity?

    @Query("SELECT * FROM channels_cache WHERE is_active = 1 AND (name LIKE :query OR acronym LIKE :query) ORDER BY name ASC")
    fun searchChannels(query: String): Flow<List<ChannelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<ChannelEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: ChannelEntity)

    @Update
    suspend fun updateChannel(channel: ChannelEntity)

    @Query("DELETE FROM channels_cache WHERE organization_id = :organizationId")
    suspend fun deleteChannelsByOrganization(organizationId: Int)
}