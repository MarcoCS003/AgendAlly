package com.example.academically.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.academically.data.dao.PersonalEventDao
import com.example.academically.data.dao.ScheduleDao
import com.example.academically.data.entities.*

@Database(
    entities = [
        // Entidades de horarios (mantener compatibilidad)
        ScheduleEntity::class,
        ScheduleTimeEntity::class,

        // Nuevas entidades según el modelo unificado
        PersonalEventEntity::class,
        EventItemEntity::class,
        PersonalEventNotificationEntity::class,
        StudentProfileEntity::class,
        StudentSubscriptionEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AcademicAllyDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
    abstract fun personalEventDao(): PersonalEventDao

    companion object {
        @Volatile
        private var INSTANCE: AcademicAllyDatabase? = null

        // Migración de versión 2 a 3
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Eliminar tablas obsoletas del modelo anterior
                database.execSQL("DROP TABLE IF EXISTS events")
                database.execSQL("DROP TABLE IF EXISTS event_categories")
                database.execSQL("DROP TABLE IF EXISTS event_items")
                database.execSQL("DROP TABLE IF EXISTS event_notifications")

                // Crear nuevas tablas según el modelo unificado

                // Tabla de eventos personales
                database.execSQL("""
                    CREATE TABLE personal_events (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        short_description TEXT NOT NULL DEFAULT '',
                        long_description TEXT NOT NULL DEFAULT '',
                        location TEXT NOT NULL DEFAULT '',
                        color_index INTEGER NOT NULL,
                        start_date TEXT NOT NULL,
                        end_date TEXT NOT NULL,
                        type TEXT NOT NULL,
                        priority TEXT NOT NULL DEFAULT 'MEDIUM',
                        institutional_event_id INTEGER,
                        image_path TEXT NOT NULL DEFAULT '',
                        tags TEXT NOT NULL DEFAULT '[]',
                        is_visible INTEGER NOT NULL DEFAULT 1,
                        is_completed INTEGER NOT NULL DEFAULT 0,
                        shape TEXT NOT NULL DEFAULT 'RoundedFull',
                        created_at TEXT NOT NULL,
                        updated_at TEXT
                    )
                """)

                // Tabla de items de eventos personales
                database.execSQL("""
                    CREATE TABLE personal_event_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        event_id INTEGER NOT NULL,
                        type TEXT NOT NULL,
                        title TEXT NOT NULL,
                        value TEXT NOT NULL,
                        description TEXT NOT NULL DEFAULT '',
                        is_clickable INTEGER NOT NULL DEFAULT 0,
                        icon_name TEXT,
                        order_index INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY(event_id) REFERENCES personal_events(id) ON DELETE CASCADE
                    )
                """)

                // Índice para event_id en items
                database.execSQL("CREATE INDEX index_personal_event_items_event_id ON personal_event_items(event_id)")

                // Tabla de notificaciones de eventos personales
                database.execSQL("""
                    CREATE TABLE personal_event_notifications (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        event_id INTEGER NOT NULL,
                        minutes_before INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        message TEXT NOT NULL,
                        is_enabled INTEGER NOT NULL DEFAULT 1,
                        notification_type TEXT NOT NULL DEFAULT 'LOCAL',
                        FOREIGN KEY(event_id) REFERENCES personal_events(id) ON DELETE CASCADE
                    )
                """)

                // Índice para event_id en notificaciones
                database.execSQL("CREATE INDEX index_personal_event_notifications_event_id ON personal_event_notifications(event_id)")

                // Tabla de perfil del estudiante
                database.execSQL("""
                    CREATE TABLE student_profile (
                        id INTEGER PRIMARY KEY NOT NULL DEFAULT 1,
                        google_user_id TEXT NOT NULL,
                        name TEXT NOT NULL,
                        email TEXT NOT NULL,
                        profile_picture TEXT,
                        notifications_enabled INTEGER NOT NULL DEFAULT 1,
                        sync_enabled INTEGER NOT NULL DEFAULT 0,
                        last_sync_at TEXT,
                        created_at TEXT NOT NULL,
                        updated_at TEXT
                    )
                """)

                // Tabla de suscripciones del estudiante
                database.execSQL("""
                    CREATE TABLE student_subscriptions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        student_id INTEGER NOT NULL DEFAULT 1,
                        channel_id INTEGER NOT NULL,
                        channel_name TEXT NOT NULL,
                        organization_name TEXT NOT NULL,
                        subscribed_at TEXT NOT NULL,
                        is_active INTEGER NOT NULL DEFAULT 1,
                        notifications_enabled INTEGER NOT NULL DEFAULT 1,
                        FOREIGN KEY(student_id) REFERENCES student_profile(id) ON DELETE CASCADE
                    )
                """)

                // Índice único para evitar suscripciones duplicadas
                database.execSQL("CREATE UNIQUE INDEX index_student_subscriptions_student_channel ON student_subscriptions(student_id, channel_id)")
            }
        }

        fun getDatabase(context: Context): AcademicAllyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AcademicAllyDatabase::class.java,
                    "academic_ally_database"
                )
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration(false) // Solo en desarrollo
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}