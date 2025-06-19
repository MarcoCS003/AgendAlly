package com.example.academically.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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

@Database(
    entities = [
        UserProfileEntity::class,
        OrganizationEntity::class,
        ChannelEntity::class,
        StudentSubscriptionEntity::class,
        PersonalEventEntity::class,
        EventItemEntity::class,
        PersonalEventNotificationEntity::class,
        ScheduleEntity::class,
        ScheduleTimeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AcademicAllyDatabase : RoomDatabase() {

    // DAOs
    abstract fun userProfileDao(): UserProfileDao
    abstract fun organizationDao(): OrganizationDao
    abstract fun channelDao(): ChannelDao
    abstract fun studentSubscriptionDao(): StudentSubscriptionDao
    abstract fun personalEventDao(): PersonalEventDao
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
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration(false) // Solo en desarrollo
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Base de datos creada exitosamente
            }
        }
    }
}
