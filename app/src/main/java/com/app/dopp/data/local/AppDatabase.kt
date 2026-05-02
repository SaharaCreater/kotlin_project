package com.app.dopp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.dopp.domain.PhysicsExperiment

@Database(entities = [PhysicsExperiment::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): PhysicsDao
}