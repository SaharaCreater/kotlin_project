package com.app.dopp.data.repository

import com.app.dopp.data.local.PhysicsDao
import com.app.dopp.data.local.ProgressDao
import com.app.dopp.domain.ExperimentProgress
import com.app.dopp.domain.PhysicsExperiment
import com.app.dopp.physics.ExperimentType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhysicsRepository @Inject constructor(
    private val dao: PhysicsDao,
    private val progressDao: ProgressDao
) {
    fun getAllProgress(): Flow<List<ExperimentProgress>> = progressDao.getAllProgress()

    suspend fun seedLocalExperiments() {
        val experiments = ExperimentType.entries.map { type ->
            PhysicsExperiment(
                id = type.name,
                name = type.displayName,
                description = type.description,
                category = type.category.name
            )
        }
        dao.insertExperimentsIfAbsent(experiments)
    }

    suspend fun markCompleted(experimentId: String) {
        val current = progressDao.getProgress(experimentId)
        progressDao.upsertProgress(
            ExperimentProgress(
                experimentId = experimentId,
                isCompleted = true,
                openCount = (current?.openCount ?: 0) + 1,
                completedAt = System.currentTimeMillis()
            )
        )
    }
}
