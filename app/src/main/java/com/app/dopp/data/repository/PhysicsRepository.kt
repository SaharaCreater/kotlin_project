package com.app.dopp.data.repository

import com.app.dopp.data.NetworkMonitor
import com.app.dopp.data.local.PhysicsDao
import com.app.dopp.data.remote.PhysicsApi
import com.app.dopp.domain.PhysicsExperiment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhysicsRepository @Inject constructor(
    private val api: PhysicsApi,
    private val dao: PhysicsDao,
    val networkMonitor: NetworkMonitor
) {
    fun getExperimentsFromDb(): Flow<List<PhysicsExperiment>> = dao.getAllExperiments()

    suspend fun refreshExperiments(): Boolean {
        return try {
            val remoteData = api.getExperiments()
            dao.insertExperiments(remoteData)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
