package com.example.academically.data.remote.api

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.academically.data.model.User
import com.example.academically.data.repositorty.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loginWithGoogle(): Result<User> {
        return authRepository.loginWithGoogle()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun mockLogin(email: String = "test@estudiante.tecnm.mx"): Result<User> {
        return authRepository.mockLogin(email)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun logout(): Result<Boolean> {
        return authRepository.logout()
    }

    suspend fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }
}