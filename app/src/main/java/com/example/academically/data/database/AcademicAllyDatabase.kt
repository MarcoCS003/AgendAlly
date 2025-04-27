package com.example.academically.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.academically.data.dao.ScheduleDao
import com.example.academically.data.entities.ScheduleEntity
import com.example.academically.data.entities.ScheduleTimeEntity

@Database(
    entities = [
        ScheduleEntity::class,
        ScheduleTimeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AcademicAllyDatabase : RoomDatabase() {

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
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}