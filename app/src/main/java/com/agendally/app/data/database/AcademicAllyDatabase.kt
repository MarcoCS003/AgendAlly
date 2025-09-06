package com.agendally.app.data.database



import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.agendally.app.data.dao.EventDao
import com.agendally.app.data.dao.OrganizationDao
import com.agendally.app.data.dao.ScheduleDao
import com.agendally.app.data.entities.EventCategoryEntity
import com.agendally.app.data.entities.EventEntity
import com.agendally.app.data.entities.EventItemEntity
import com.agendally.app.data.entities.EventNotificationEntity
import com.agendally.app.data.entities.OrganizationEntity
import com.agendally.app.data.entities.ScheduleEntity
import com.agendally.app.data.entities.ScheduleTimeEntity
import com.agendally.app.data.entities.SubscriptionEntity

@Database(
    entities = [
        // ===== CALENDARIO Y EVENTOS =====
        ScheduleEntity::class,
        ScheduleTimeEntity::class,
        EventEntity::class,
        EventCategoryEntity::class,
        EventItemEntity::class,
        EventNotificationEntity::class,
        // ===== ORGANIZACIONES =====
        OrganizationEntity::class,
        SubscriptionEntity::class
    ],
    version = 7, // ⚠️ INCREMENTAR A VERSIÓN 4
    exportSchema = false
)
abstract class AcademicAllyDatabase : RoomDatabase() {

    // ===== DAOs =====
    abstract fun scheduleDao(): ScheduleDao
    abstract fun eventDao(): EventDao
    abstract fun organizationDao(): OrganizationDao // ✅ NUEVO DAO

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
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}