package com.app.dopp.data

import android.content.Context
import com.app.dopp.data.remote.UserDto
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("dopp_auth", Context.MODE_PRIVATE)

    var token: String?
        get() = prefs.getString("token", null)
        set(value) = prefs.edit().putString("token", value).apply()

    var userId: String?
        get() = prefs.getString("user_id", null)
        set(value) = prefs.edit().putString("user_id", value).apply()

    var userName: String?
        get() = prefs.getString("user_name", null)
        set(value) = prefs.edit().putString("user_name", value).apply()

    var userEmail: String?
        get() = prefs.getString("user_email", null)
        set(value) = prefs.edit().putString("user_email", value).apply()

    var userAvatarColor: String?
        get() = prefs.getString("user_avatar_color", "#6750A4")
        set(value) = prefs.edit().putString("user_avatar_color", value).apply()

    fun saveUser(user: UserDto) {
        userId = user.id
        userName = user.name
        userEmail = user.email
        userAvatarColor = user.avatar_color
    }

    fun getCachedUser(): UserDto? {
        val id = userId ?: return null
        return UserDto(
            id = id,
            name = userName ?: "",
            email = userEmail ?: "",
            avatar_color = userAvatarColor ?: "#6750A4"
        )
    }

    fun clear() = prefs.edit().clear().apply()
}
