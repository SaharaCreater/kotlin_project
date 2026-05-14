package com.app.dopp.data.remote

import com.app.dopp.domain.PhysicsExperiment
import retrofit2.http.GET

interface PhysicsApi {
    @GET("api/experiments")
    suspend fun getExperiments(): List<PhysicsExperiment>
}
