package com.app.dopp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.dopp.domain.PhysicsExperiment

@Database(entities = [PhysicsExperiment::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): PhysicsDao

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
    }
}
