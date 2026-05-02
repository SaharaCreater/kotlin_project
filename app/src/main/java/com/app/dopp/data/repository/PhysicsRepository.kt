package com.app.dopp.data.repository

import com.app.dopp.data.local.PhysicsDao
import com.app.dopp.data.remote.PhysicsApi
import com.app.dopp.domain.PhysicsExperiment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhysicsRepository @Inject constructor(
    private val api: PhysicsApi,
    private val dao: PhysicsDao
) {
    // Получаем поток данных из БД (Requirement 4)
    fun getExperimentsFromDb(): Flow<List<PhysicsExperiment>> = dao.getAllExperiments()

    // Обновляем данные из сети и сохраняем в БД (Requirement 5)
    suspend fun refreshExperiments() {
        try {
            val remoteData = api.getExperiments()
            dao.insertExperiments(remoteData)
        } catch (e: Exception) {
            e.printStackTrace() // Здесь можно обработать отсутствие интернета
        }
    }
}