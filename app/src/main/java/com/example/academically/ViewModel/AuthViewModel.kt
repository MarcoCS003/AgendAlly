package com.example.academically.ViewModel

import android.annotation.SuppressLint
import com.example.academically.data.repositorty.AuthRepository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.academically.data.model.User

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Estados para la UI (compatible con tu estructura)
sealed class AuthUiState {
    object Loading : AuthUiState()
    object Unauthenticated : AuthUiState()
    data class Authenticated(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false
)

data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistrationSuccessful: Boolean = false
)

class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // ✅ CORREGIDO: Tipo explícito para evitar problemas de inferencia
    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    init {
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            try {
                authRepository.getCurrentUserProfile().collect { user ->
                    _authUiState.value = if (user != null) {
                        AuthUiState.Authenticated(user)
                    } else {
                        AuthUiState.Unauthenticated
                    }
                }
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error("Error al verificar autenticación: ${e.message}")
            }
        }
    }

    // Iniciar sesión con credenciales
    @SuppressLint("NewApi")
    fun loginWithCredentials(email: String, password: String) {
        viewModelScope.launch {
            _loginUiState.value = _loginUiState.value.copy(isLoading = true, errorMessage = null)

            authRepository.loginWithCredentials(email, password)
                .onSuccess { user ->
                    _loginUiState.value = _loginUiState.value.copy(
                        isLoading = false,
                        isLoginSuccessful = true
                    )
                    _authUiState.value = AuthUiState.Authenticated(user)
                }
                .onFailure { exception ->
                    _loginUiState.value = _loginUiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al iniciar sesión: ${exception.message}"
                    )
                }
        }
    }

    // Registrar nuevo usuario
    @SuppressLint("NewApi")
    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerUiState.value = _registerUiState.value.copy(isLoading = true, errorMessage = null)

            authRepository.registerUser(name, email, password)
                .onSuccess { user ->
                    _registerUiState.value = _registerUiState.value.copy(
                        isLoading = false,
                        isRegistrationSuccessful = true
                    )
                    _authUiState.value = AuthUiState.Authenticated(user)
                }
                .onFailure { exception ->
                    _registerUiState.value = _registerUiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al registrar usuario: ${exception.message}"
                    )
                }
        }
    }

    // Iniciar sesión con Google
    @SuppressLint("NewApi")
    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _loginUiState.value = _loginUiState.value.copy(isLoading = true, errorMessage = null)

            authRepository.loginWithGoogle(idToken)
                .onSuccess { user ->
                    _loginUiState.value = _loginUiState.value.copy(
                        isLoading = false,
                        isLoginSuccessful = true
                    )
                    _authUiState.value = AuthUiState.Authenticated(user)
                }
                .onFailure { exception ->
                    _loginUiState.value = _loginUiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al iniciar sesión con Google: ${exception.message}"
                    )
                }
        }
    }

    // Cerrar sesión
    @SuppressLint("NewApi")
    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
                _authUiState.value = AuthUiState.Unauthenticated
                _loginUiState.value = LoginUiState()
                _registerUiState.value = RegisterUiState()
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error("Error al cerrar sesión: ${e.message}")
            }
        }
    }

    // Limpiar errores
    fun clearLoginError() {
        _loginUiState.value = _loginUiState.value.copy(errorMessage = null)
    }

    fun clearRegisterError() {
        _registerUiState.value = _registerUiState.value.copy(errorMessage = null)
    }

    // Actualizar configuraciones
    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            authRepository.updateNotificationsEnabled(enabled)
        }
    }

    @SuppressLint("NewApi")
    fun updateSyncSettings(enabled: Boolean) {
        viewModelScope.launch {
            authRepository.updateSyncSettings(enabled)
        }
    }
}

// Factory para crear el ViewModel sin Hilt
class AuthViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}