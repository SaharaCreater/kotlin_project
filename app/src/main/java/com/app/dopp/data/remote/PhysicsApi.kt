package com.app.dopp.data.remote

import com.app.dopp.domain.PhysicsExperiment
import retrofit2.http.*

interface PhysicsApi {

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @GET("api/auth/me")
    suspend fun getMe(): UserDto

    @PUT("api/auth/profile")
    suspend fun updateProfile(@Body body: UpdateProfileRequest): UserDto

    @GET("api/experiments")
    suspend fun getExperiments(): List<PhysicsExperiment>

    @GET("api/progress")
    suspend fun getProgress(): List<ProgressDto>

    @POST("api/progress/{expId}")
    suspend fun recordProgress(@Path("expId") expId: String): ProgressDto
}
