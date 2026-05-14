package com.app.dopp.data.remote

data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(val name: String, val email: String, val password: String)

data class UpdateProfileRequest(val name: String)

data class UserDto(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val avatar_color: String = "#6750A4"
)

data class AuthResponse(val token: String, val user: UserDto)

data class ProgressDto(
    val id: String = "",
    val user_id: String = "",
    val experiment_id: String = "",
    val run_count: Int = 0,
    val completed: Boolean = false,
    val last_run_at: Long? = null
)
