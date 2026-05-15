package com.app.dopp.ui_project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.dopp.data.AuthPreferences
import com.app.dopp.data.remote.LoginRequest
import com.app.dopp.data.remote.PhysicsApi
import com.app.dopp.data.remote.RegisterRequest
import com.app.dopp.data.remote.UpdateProfileRequest
import com.app.dopp.data.remote.UserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val user: UserDto) : AuthState()
    object Unauthenticated : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val api: PhysicsApi,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            val token = authPreferences.token
            if (token == null) {
                _authState.value = AuthState.Unauthenticated
                return@launch
            }
            // Сразу показываем кэшированного пользователя — не ждём сеть
            val cached = authPreferences.getCachedUser()
            if (cached != null) {
                _authState.value = AuthState.Authenticated(cached)
            }
            // Пытаемся обновить данные с сервера в фоне
            try {
                val user = api.getMe()
                authPreferences.saveUser(user)
                _authState.value = AuthState.Authenticated(user)
            } catch (_: Exception) {
                // Сервер недоступен — если есть кэш, остаёмся залогиненными
                // Если кэша нет и токен невалиден — разлогиниваем
                if (cached == null) {
                    authPreferences.clear()
                    _authState.value = AuthState.Unauthenticated
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = api.login(LoginRequest(email.trim(), password))
                authPreferences.token = response.token
                authPreferences.saveUser(response.user)
                _authState.value = AuthState.Authenticated(response.user)
            } catch (e: retrofit2.HttpException) {
                _error.value = when (e.code()) {
                    401 -> "Неверный email или пароль"
                    else -> "Ошибка входа. Попробуйте снова"
                }
            } catch (_: Exception) {
                _error.value = "Нет соединения с сервером"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = api.register(RegisterRequest(name.trim(), email.trim(), password))
                authPreferences.token = response.token
                authPreferences.saveUser(response.user)
                _authState.value = AuthState.Authenticated(response.user)
            } catch (e: retrofit2.HttpException) {
                _error.value = when (e.code()) {
                    409 -> "Этот email уже зарегистрирован"
                    400 -> "Заполните все поля"
                    else -> "Ошибка регистрации. Попробуйте снова"
                }
            } catch (_: Exception) {
                _error.value = "Нет соединения с сервером"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        authPreferences.clear()
        _authState.value = AuthState.Unauthenticated
    }

    fun updateName(newName: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val user = api.updateProfile(UpdateProfileRequest(newName.trim()))
                authPreferences.saveUser(user)
                _authState.value = AuthState.Authenticated(user)
                onSuccess()
            } catch (_: Exception) {
                _error.value = "Не удалось обновить имя"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun currentUser(): UserDto? = (_authState.value as? AuthState.Authenticated)?.user

    fun getServerUrl(): String = authPreferences.serverUrl ?: ""

    fun saveServerUrl(url: String) {
        authPreferences.serverUrl = url.trim()
    }
}
