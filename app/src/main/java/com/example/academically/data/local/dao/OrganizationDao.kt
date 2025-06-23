package com.example.academically.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.academically.data.local.entities.OrganizationEntity
import com.example.academically.data.local.entities.OrganizationWithChannels
import kotlinx.coroutines.flow.Flow

@Dao
interface OrganizationDao {

    @Query("SELECT * FROM organizations_cache WHERE is_active = 1 ORDER BY name ASC")
    fun getAllOrganizations(): Flow<List<OrganizationEntity>>

    @Query("SELECT * FROM organizations_cache WHERE id = :organizationId AND is_active = 1")
    fun getOrganizationById(organizationId: Int): Flow<OrganizationEntity?>

    @Query("SELECT * FROM organizations_cache WHERE acronym = :acronym AND is_active = 1")
    fun getOrganizationByAcronym(acronym: String): Flow<OrganizationEntity?>

    @Query("""
        SELECT * FROM organizations_cache 
        WHERE (name LIKE :searchQuery OR acronym LIKE :searchQuery) 
        AND is_active = 1 
        ORDER BY name ASC
    """)
    fun searchOrganizations(searchQuery: String): Flow<List<OrganizationEntity>>

    @Transaction
    @Query("SELECT * FROM organizations_cache WHERE is_active = 1 ORDER BY name ASC")
    fun getOrganizationsWithChannels(): Flow<List<OrganizationWithChannels>>

    @Transaction
    @Query("SELECT * FROM organizations_cache WHERE id = :organizationId AND is_active = 1")
    fun getOrganizationWithChannels(organizationId: Int): Flow<OrganizationWithChannels?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrganization(organization: OrganizationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrganizations(organizations: List<OrganizationEntity>)

    @Update
    suspend fun updateOrganization(organization: OrganizationEntity)

    @Query("DELETE FROM organizations_cache WHERE id = :organizationId")
    suspend fun deleteOrganization(organizationId: Int)

    @Query("DELETE FROM organizations_cache")
    suspend fun deleteAllOrganizations()

    @Query("SELECT COUNT(*) FROM organizations_cache WHERE is_active = 1")
    suspend fun getOrganizationCount(): Int

    @Query("UPDATE organizations_cache SET cached_at = :cachedAt WHERE id = :organizationId")
    suspend fun updateCacheTime(organizationId: Int, cachedAt: String)
}