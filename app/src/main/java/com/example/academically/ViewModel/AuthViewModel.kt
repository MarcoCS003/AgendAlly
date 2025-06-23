package com.example.academically.ViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.academically.data.remote.api.LoginUseCase
import com.example.academically.data.repositorty.AuthRepository
import com.example.academically.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Estados para la UI
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

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    // Estados principales
    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Unauthenticated)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    // Para compatibilidad con código existente
    val authState = authRepository.getAuthState()

    init {
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading

            try {
                val isLoggedIn = loginUseCase.isUserLoggedIn()
                if (isLoggedIn) {
                    // Obtener perfil del usuario desde el repositorio
                    authRepository.getCurrentUserProfile().collect { user ->
                        if (user != null) {
                            _authUiState.value = AuthUiState.Authenticated(user)
                        } else {
                            _authUiState.value = AuthUiState.Unauthenticated
                        }
                    }
                } else {
                    _authUiState.value = AuthUiState.Unauthenticated
                }
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    // ============== MÉTODOS DE LOGIN ==============

    @RequiresApi(Build.VERSION_CODES.O)
    fun loginWithGoogle(googleToken: String? = null) {
        viewModelScope.launch {
            _loginUiState.value = _loginUiState.value.copy(isLoading = true, errorMessage = null)
            _authUiState.value = AuthUiState.Loading

            val result = if (googleToken != null) {
                // Aquí se podría implementar login real con Google token
                loginUseCase.mockLogin() // Por ahora usar mock
            } else {
                loginUseCase.loginWithGoogle()
            }

            if (result.isSuccess) {
                val user = result.getOrThrow()
                _authUiState.value = AuthUiState.Authenticated(user)
                _loginUiState.value = _loginUiState.value.copy(
                    isLoading = false,
                    isLoginSuccessful = true,
                    errorMessage = null
                )
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Error en login con Google"
                _authUiState.value = AuthUiState.Error(errorMessage)
                _loginUiState.value = _loginUiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMessage,
                    isLoginSuccessful = false
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loginWithCredentials(email: String, password: String) {
        viewModelScope.launch {
            _loginUiState.value = _loginUiState.value.copy(isLoading = true, errorMessage = null)
            _authUiState.value = AuthUiState.Loading

            // Por ahora, simular login con credenciales usando mock login
            val result = loginUseCase.mockLogin(email)

            if (result.isSuccess) {
                val user = result.getOrThrow()
                _authUiState.value = AuthUiState.Authenticated(user)
                _loginUiState.value = _loginUiState.value.copy(
                    isLoading = false,
                    isLoginSuccessful = true,
                    errorMessage = null
                )
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Credenciales inválidas"
                _authUiState.value = AuthUiState.Error(errorMessage)
                _loginUiState.value = _loginUiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMessage,
                    isLoginSuccessful = false
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun mockLogin(email: String = "test@estudiante.tecnm.mx") {
        viewModelScope.launch {
            _loginUiState.value = _loginUiState.value.copy(isLoading = true, errorMessage = null)
            _authUiState.value = AuthUiState.Loading

            val result = loginUseCase.mockLogin(email)

            if (result.isSuccess) {
                val user = result.getOrThrow()
                _authUiState.value = AuthUiState.Authenticated(user)
                _loginUiState.value = _loginUiState.value.copy(
                    isLoading = false,
                    isLoginSuccessful = true,
                    errorMessage = null
                )
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Error en login mock"
                _authUiState.value = AuthUiState.Error(errorMessage)
                _loginUiState.value = _loginUiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMessage,
                    isLoginSuccessful = false
                )
            }
        }
    }

    // ============== MÉTODOS DE REGISTRO ==============

    @RequiresApi(Build.VERSION_CODES.O)
    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerUiState.value = _registerUiState.value.copy(isLoading = true, errorMessage = null)
            _authUiState.value = AuthUiState.Loading

            // Por ahora, simular registro exitoso con mock login
            val result = loginUseCase.mockLogin(email)

            if (result.isSuccess) {
                val user = result.getOrThrow()
                _authUiState.value = AuthUiState.Authenticated(user)
                _registerUiState.value = _registerUiState.value.copy(
                    isLoading = false,
                    isRegistrationSuccessful = true,
                    errorMessage = null
                )
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Error en registro"
                _authUiState.value = AuthUiState.Error(errorMessage)
                _registerUiState.value = _registerUiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMessage,
                    isRegistrationSuccessful = false
                )
            }
        }
    }

    // ============== MÉTODO DE LOGOUT ==============

    @RequiresApi(Build.VERSION_CODES.O)
    fun logout() {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading

            val result = loginUseCase.logout()

            _authUiState.value = AuthUiState.Unauthenticated
            _loginUiState.value = LoginUiState()
            _registerUiState.value = RegisterUiState()
        }
    }

    // ============== MÉTODOS DE LIMPIEZA DE ERRORES ==============

    fun clearError() {
        if (_authUiState.value is AuthUiState.Error) {
            _authUiState.value = AuthUiState.Unauthenticated
        }
    }

    fun clearLoginError() {
        _loginUiState.value = _loginUiState.value.copy(errorMessage = null)
    }

    fun clearRegisterError() {
        _registerUiState.value = _registerUiState.value.copy(errorMessage = null)
    }

    // ============== MÉTODOS DE COMPATIBILIDAD (DEPRECADOS) ==============

    @RequiresApi(Build.VERSION_CODES.O)
    fun loginWithGoogle() {
        loginWithGoogle(googleToken = null)
    }
}
