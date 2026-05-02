package com.app.dopp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.dopp.domain.PhysicsExperiment
import kotlinx.coroutines.flow.Flow

@Dao
interface PhysicsDao {
    @Query("SELECT * FROM experiments")
    fun getAllExperiments(): Flow<List<PhysicsExperiment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExperiments(experiments: List<PhysicsExperiment>)

    @Query("SELECT * FROM experiments WHERE id = :experimentId")
    suspend fun getExperimentById(experimentId: String): PhysicsExperiment?
}