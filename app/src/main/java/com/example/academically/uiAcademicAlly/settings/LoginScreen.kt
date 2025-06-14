
@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.academically.uiAcademicAlly.settings

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.academically.data.UserRole
import com.example.academically.ui.theme.AcademicAllyTheme

@Composable
fun LoginScreen(
    onLoginSuccess: (userRole: UserRole) -> Unit = {},
    onNavigateToRegister: () -> Unit,
    onGoogleSignIn: () -> Unit = {},
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    // Validaciones
    fun validateEmail(): Boolean {
        return when {
            email.isBlank() -> {
                emailError = "El email es obligatorio"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailError = "Formato de email inválido"
                false
            }
            else -> {
                emailError = ""
                true
            }
        }
    }

    fun validatePassword(): Boolean {
        return when {
            password.isBlank() -> {
                passwordError = "La contraseña es obligatoria"
                false
            }
            password.length < 6 -> {
                passwordError = "La contraseña debe tener al menos 6 caracteres"
                false
            }
            else -> {
                passwordError = ""
                true
            }
        }
    }

    fun handleLogin() {
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()

        if (isEmailValid && isPasswordValid) {
            // Aquí iría la lógica de autenticación real
            // Por ahora simulamos un login exitoso
            onLoginSuccess(UserRole.STUDENT)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(60.dp))

            // Logo y título

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "AgendAlly",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Tu calendario académico centralizado",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Formulario de login
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Iniciar Sesión",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Campo de email
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (emailError.isNotEmpty()) emailError = ""
                        },
                        label = { Text("Email") },
                        placeholder = { Text("ejemplo@instituto.edu.mx") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = emailError.isNotEmpty(),
                        supportingText = if (emailError.isNotEmpty()) {
                            { Text(emailError, color = MaterialTheme.colorScheme.error) }
                        } else null,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (passwordError.isNotEmpty()) passwordError = ""
                        },
                        label = { Text("Contraseña") },
                        placeholder = { Text("Ingresa tu contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { isPasswordVisible = !isPasswordVisible }
                            ) {
                                Icon(
                                    imageVector = if (isPasswordVisible) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                    contentDescription = if (isPasswordVisible) {
                                        "Ocultar contraseña"
                                    } else {
                                        "Mostrar contraseña"
                                    }
                                )
                            }
                        },
                        isError = passwordError.isNotEmpty(),
                        supportingText = if (passwordError.isNotEmpty()) {
                            { Text(passwordError, color = MaterialTheme.colorScheme.error) }
                        } else null,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Mensaje de error general
                    errorMessage?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón de login
                    Button(
                        onClick = { handleLogin() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Iniciar Sesión",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f))
                        Text(
                            text = "  o  ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón de Google Sign In
                    OutlinedButton(
                        onClick = { onGoogleSignIn() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Aquí puedes agregar el ícono de Google si lo tienes
                            // Icon(...)
                            Text(
                                text = "Continuar con Google",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Link para registro
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "¿No tienes cuenta? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        TextButton(
                            onClick = { onNavigateToRegister() },
                            enabled = !isLoading
                        ) {
                            Text(
                                text = "Regístrate",
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer
            Text(
                text = "Al iniciar sesión, aceptas nuestros términos y condiciones",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    AcademicAllyTheme {
        LoginScreen(
            onLoginSuccess = TODO(),
            onNavigateToRegister = TODO(),
            onGoogleSignIn = TODO(),
            isLoading = TODO(),
            errorMessage = TODO()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenLoadingPreview() {
    AcademicAllyTheme {
        LoginScreen(
            isLoading = true,
            onLoginSuccess = TODO(),
            onNavigateToRegister = TODO(),
            onGoogleSignIn = TODO(),
            errorMessage = TODO()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenErrorPreview() {
    AcademicAllyTheme {
        LoginScreen(
            errorMessage = "Credenciales incorrectas. Verifica tu email y contraseña.",
            onLoginSuccess = TODO(),
            onNavigateToRegister = TODO(),
            onGoogleSignIn = TODO(),
            isLoading = TODO()
        )
    }
}