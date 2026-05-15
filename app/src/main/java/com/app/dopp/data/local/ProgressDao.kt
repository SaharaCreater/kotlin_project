package com.app.dopp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.dopp.domain.ExperimentProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Query("SELECT * FROM experiment_progress")
    fun getAllProgress(): Flow<List<ExperimentProgress>>

    @Query("SELECT * FROM experiment_progress WHERE experimentId = :id")
    suspend fun getProgress(id: String): ExperimentProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: ExperimentProgress)

    @Query("SELECT * FROM experiment_progress WHERE pendingSyncNeeded = 1")
    suspend fun getPendingSync(): List<ExperimentProgress>

    @Query("DELETE FROM experiment_progress")
    suspend fun clearAllProgress()
}
