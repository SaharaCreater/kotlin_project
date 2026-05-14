package com.app.dopp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.dopp.domain.ExperimentProgress
import com.app.dopp.domain.PhysicsExperiment

@Database(
    entities = [PhysicsExperiment::class, ExperimentProgress::class],
    version = 4
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): PhysicsDao
    abstract fun progressDao(): ProgressDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE experiments_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        description TEXT NOT NULL,
                        category TEXT NOT NULL,
                        modelUrl TEXT,
                        title TEXT,
                        icon TEXT,
                        difficulty TEXT
                    )
                """.trimIndent())
                database.execSQL("""
                    INSERT INTO experiments_new (id, name, description, category, modelUrl, title)
                    SELECT id, name, description, category, modelUrl, title FROM experiments
                """.trimIndent())
                database.execSQL("DROP TABLE experiments")
                database.execSQL("ALTER TABLE experiments_new RENAME TO experiments")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE experiment_progress (
                        experimentId TEXT NOT NULL PRIMARY KEY,
                        isCompleted INTEGER NOT NULL DEFAULT 0,
                        openCount INTEGER NOT NULL DEFAULT 0,
                        completedAt INTEGER
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE experiment_progress ADD COLUMN pendingSyncNeeded INTEGER NOT NULL DEFAULT 0"
                )
            }
        }
    }
}
