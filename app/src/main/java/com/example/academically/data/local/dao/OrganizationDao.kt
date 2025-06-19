package com.example.academically.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.academically.data.local.entities.OrganizationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrganizationDao {

    @Query("SELECT * FROM organizations_cache WHERE is_active = 1 ORDER BY name ASC")
    fun getAllOrganizations(): Flow<List<OrganizationEntity>>

    @Query("SELECT * FROM organizations_cache WHERE id = :id")
    suspend fun getOrganizationById(id: Int): OrganizationEntity?

    @Query("SELECT * FROM organizations_cache WHERE is_active = 1 AND (name LIKE :query OR acronym LIKE :query) ORDER BY name ASC")
    fun searchOrganizations(query: String): Flow<List<OrganizationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrganizations(organizations: List<OrganizationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrganization(organization: OrganizationEntity)

    @Update
    suspend fun updateOrganization(organization: OrganizationEntity)

    @Query("DELETE FROM organizations_cache WHERE cached_at < :cutoffTime")
    suspend fun deleteOldCache(cutoffTime: String)

    @Query("SELECT COUNT(*) > 0 FROM organizations_cache WHERE cached_at > :cutoffTime")
    suspend fun hasRecentCache(cutoffTime: String): Boolean
}