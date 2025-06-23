package com.example.academically.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.academically.data.local.dao.ChannelDao
import com.example.academically.data.local.dao.OrganizationDao
import com.example.academically.data.local.dao.PersonalEventDao
import com.example.academically.data.local.dao.ScheduleDao
import com.example.academically.data.local.dao.StudentSubscriptionDao
import com.example.academically.data.local.dao.UserProfileDao
import com.example.academically.data.local.entities.ChannelEntity
import com.example.academically.data.local.entities.EventItemEntity
import com.example.academically.data.local.entities.OrganizationEntity
import com.example.academically.data.local.entities.PersonalEventEntity
import com.example.academically.data.local.entities.PersonalEventNotificationEntity
import com.example.academically.data.local.entities.ScheduleEntity
import com.example.academically.data.local.entities.ScheduleTimeEntity
import com.example.academically.data.local.entities.StudentSubscriptionEntity
import com.example.academically.data.local.entities.UserProfileEntity
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.Module
import dagger.Provides
@Database(
    entities = [
        // User and Profile
        UserProfileEntity::class,

        // Organizations and Channels
        OrganizationEntity::class,
        ChannelEntity::class,
        StudentSubscriptionEntity::class,

        // Personal Events
        PersonalEventEntity::class,
        EventItemEntity::class,
        PersonalEventNotificationEntity::class,

        // Schedule (AGREGADO)
        ScheduleEntity::class,
        ScheduleTimeEntity::class
    ],
    version = 3, // ✅ Incrementar versión por agregar Schedule
    exportSchema = false
)
abstract class AcademicAllyDatabase : RoomDatabase() {

    // User DAOs
    abstract fun userProfileDao(): UserProfileDao

    // Organization and Channel DAOs
    abstract fun organizationDao(): OrganizationDao
    abstract fun channelDao(): ChannelDao
    abstract fun studentSubscriptionDao(): StudentSubscriptionDao

    // Personal Event DAOs
    abstract fun personalEventDao(): PersonalEventDao

    // Schedule DAO (AGREGADO)
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: AcademicAllyDatabase? = null

        fun getDatabase(context: Context): AcademicAllyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AcademicAllyDatabase::class.java,
                    "academic_ally_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration() // Solo para desarrollo
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
// ============== MIGRATION ==============

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Migración de Institutes a Organizations
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS organizations_cache (
                id INTEGER PRIMARY KEY NOT NULL,
                acronym TEXT NOT NULL,
                name TEXT NOT NULL,
                description TEXT NOT NULL DEFAULT '',
                address TEXT NOT NULL DEFAULT '',
                email TEXT NOT NULL,
                phone TEXT NOT NULL,
                student_number INTEGER NOT NULL,
                teacher_number INTEGER NOT NULL,
                website TEXT,
                logo_url TEXT,
                facebook TEXT,
                instagram TEXT,
                twitter TEXT,
                youtube TEXT,
                linkedin TEXT,
                is_active INTEGER NOT NULL DEFAULT 1,
                cached_at TEXT NOT NULL,
                created_at TEXT NOT NULL,
                updated_at TEXT
            )
        """)

        database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_organizations_cache_acronym ON organizations_cache(acronym)")

        // Migración de Careers a Channels
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS channels_cache (
                id INTEGER PRIMARY KEY NOT NULL,
                organization_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                acronym TEXT NOT NULL,
                description TEXT NOT NULL DEFAULT '',
                type TEXT NOT NULL,
                email TEXT,
                phone TEXT,
                is_active INTEGER NOT NULL DEFAULT 1,
                cached_at TEXT NOT NULL,
                created_at TEXT NOT NULL,
                updated_at TEXT,
                FOREIGN KEY(organization_id) REFERENCES organizations_cache(id) ON DELETE CASCADE
            )
        """)

        database.execSQL("CREATE INDEX IF NOT EXISTS index_channels_cache_organization_id ON channels_cache(organization_id)")
        database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_channels_cache_organization_id_acronym ON channels_cache(organization_id, acronym)")

        // Actualizar tabla de suscripciones
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS student_subscriptions_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                user_id INTEGER NOT NULL,
                channel_id INTEGER NOT NULL,
                subscribed_at TEXT NOT NULL,
                is_active INTEGER NOT NULL DEFAULT 1,
                notifications_enabled INTEGER NOT NULL DEFAULT 1,
                synced_at TEXT,
                FOREIGN KEY(channel_id) REFERENCES channels_cache(id) ON DELETE CASCADE
            )
        """)

        database.execSQL("CREATE INDEX IF NOT EXISTS index_student_subscriptions_user_id ON student_subscriptions_new(user_id)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_student_subscriptions_channel_id ON student_subscriptions_new(channel_id)")
        database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_student_subscriptions_user_id_channel_id ON student_subscriptions_new(user_id, channel_id)")

        // Migrar datos existentes si existen
        try {
            // Copiar institutes a organizations si existe
            database.execSQL("""
                INSERT OR IGNORE INTO organizations_cache 
                (id, acronym, name, address, email, phone, student_number, teacher_number, website, facebook, instagram, twitter, youtube, is_active, cached_at, created_at)
                SELECT id, acronym, name, address, email, phone, studentNumber, teacherNumber, webSite, facebook, instagram, twitter, youtube, 1, datetime('now'), datetime('now')
                FROM institutes_cache
            """)

            // Copiar careers a channels si existe
            database.execSQL("""
                INSERT OR IGNORE INTO channels_cache 
                (id, organization_id, name, acronym, description, type, email, phone, is_active, cached_at, created_at)
                SELECT careerID, instituteId, name, acronym, '', 'CAREER', email, phone, 1, datetime('now'), datetime('now')
                FROM careers_cache
            """)

            // Migrar suscripciones existentes
            database.execSQL("""
                INSERT OR IGNORE INTO student_subscriptions_new 
                (user_id, channel_id, subscribed_at, is_active, notifications_enabled)
                SELECT user_id, career_id, subscribed_at, is_active, notifications_enabled
                FROM student_subscriptions
                WHERE career_id IN (SELECT id FROM channels_cache)
            """)

        } catch (e: Exception) {
            // Si no existen las tablas antiguas, continuar
        }

        // Eliminar tablas antiguas si existen
        try {
            database.execSQL("DROP TABLE IF EXISTS institutes_cache")
            database.execSQL("DROP TABLE IF EXISTS careers_cache")
            database.execSQL("DROP TABLE IF EXISTS student_subscriptions")
        } catch (e: Exception) {
            // Continuar si no existen
        }

        // Renombrar nueva tabla de suscripciones
        database.execSQL("ALTER TABLE student_subscriptions_new RENAME TO student_subscriptions")
    }
}

// ============== DAGGER MODULE ==============

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAcademicAllyDatabase(
        @ApplicationContext context: Context
    ): AcademicAllyDatabase {
        return Room.databaseBuilder(
            context,
            AcademicAllyDatabase::class.java,
            "academic_ally_database"
        )
            .addMigrations(MIGRATION_1_2 as Migration)
            .fallbackToDestructiveMigration() // Solo para desarrollo, remover en producción
            .build()
    }

    // ============== USER DAOS ==============

    @Provides
    fun provideUserProfileDao(database: AcademicAllyDatabase): UserProfileDao {
        return database.userProfileDao()
    }

    // ============== ORGANIZATION DAOS ==============

    @Provides
    fun provideOrganizationDao(database: AcademicAllyDatabase): OrganizationDao {
        return database.organizationDao()
    }

    @Provides
    fun provideChannelDao(database: AcademicAllyDatabase): ChannelDao {
        return database.channelDao()
    }

    @Provides
    fun provideStudentSubscriptionDao(database: AcademicAllyDatabase): StudentSubscriptionDao {
        return database.studentSubscriptionDao()
    }

    // ============== EVENT DAOS ==============

    @Provides
    fun providePersonalEventDao(database: AcademicAllyDatabase): PersonalEventDao {
        return database.personalEventDao()
    }
}