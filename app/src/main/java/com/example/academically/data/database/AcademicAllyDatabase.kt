package com.example.academically.data.database



import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.academically.data.dao.EventDao
import com.example.academically.data.dao.ScheduleDao
import com.example.academically.data.entities.EventCategoryEntity
import com.example.academically.data.entities.EventEntity
import com.example.academically.data.entities.EventItemEntity
import com.example.academically.data.entities.EventNotificationEntity
import com.example.academically.data.entities.ScheduleEntity
import com.example.academically.data.entities.ScheduleTimeEntity

@Database(
    entities = [
        ScheduleEntity::class,
        ScheduleTimeEntity::class,
        EventEntity::class,
        EventCategoryEntity::class,
        EventItemEntity::class,
        EventNotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AcademicAllyDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
    abstract fun eventDao(): EventDao

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
                    .fallbackToDestructiveMigration(false) // Permitir migración destructiva
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}