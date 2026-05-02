package com.app.dopp.data.remote

import com.app.dopp.domain.PhysicsExperiment
import retrofit2.http.GET

interface PhysicsApi {
    // Здесь указываешь путь к твоему JSON файлу на сервере
    @GET("experiments.json")
    suspend fun getExperiments(): List<PhysicsExperiment>
}