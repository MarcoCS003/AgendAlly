@file:OptIn(ExperimentalMaterial3Api::class)

package com.academically.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academically.ViewModel.AuthViewModel
import com.example.academically.ViewModel.AuthUiState
import com.example.academically.data.model.UserRole
import com.example.academically.ui.theme.AcademicAllyTheme

@Composable
fun RegisterScreenWithViewModel(
    viewModel: AuthViewModel = viewModel(),
    onRegisterSuccess: (userRole: UserRole) -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onGoogleSignIn: () -> Unit = {}
) {
    // Estados del ViewModel
    val authUiState by viewModel.authUiState.collectAsState()
    val registerUiState by viewModel.registerUiState.collectAsState()

    // Estados locales para los campos
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var acceptTerms by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    // Estados de error
    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var termsError by remember { mutableStateOf("") }

    // Efectos para manejar el estado de autenticación
    LaunchedEffect(authUiState) {
        when (authUiState) {
            is AuthUiState.Authenticated -> {
                onRegisterSuccess((authUiState as AuthUiState.Authenticated).user.role)
            }
            else -> { /* No hacer nada */ }
        }
    }

    // Validaciones
    fun validateName(): Boolean {
        return when {
            name.isBlank() -> {
                nameError = "El nombre es obligatorio"
                false
            }
            name.length < 2 -> {
                nameError = "El nombre debe tener al menos 2 caracteres"
                false
            }
            else -> {
                nameError = ""
                true
            }
        }
    }

    fun validateEmail(): Boolean {
        return when {
            email.isBlank() -> {
                emailError = "El email es obligatorio"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
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

    fun validateConfirmPassword(): Boolean {
        return when {
            confirmPassword.isBlank() -> {
                confirmPasswordError = "Confirma tu contraseña"
                false
            }
            confirmPassword != password -> {
                confirmPasswordError = "Las contraseñas no coinciden"
                false
            }
            else -> {
                confirmPasswordError = ""
                true
            }
        }
    }

    fun validateTerms(): Boolean {
        return if (!acceptTerms) {
            termsError = "Debes aceptar los términos y condiciones"
            false
        } else {
            termsError = ""
            true
        }
    }

    fun handleRegister() {
        val isNameValid = validateName()
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()
        val isConfirmPasswordValid = validateConfirmPassword()
        val isTermsValid = validateTerms()

        if (isNameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid && isTermsValid) {
            // Limpiar errores previos
            viewModel.clearRegisterError()
            // Llamar al ViewModel para hacer registro
            viewModel.registerUser(name, email, password)
        }
    }

    fun handleGoogleSignIn() {
        // Por ahora simulamos un token
        val mockGoogleToken = "mock_google_token_${System.currentTimeMillis()}"
        viewModel.loginWithGoogle(mockGoogleToken)
    }

    // Limpiar errores cuando el usuario escribe
    LaunchedEffect(name) {
        if (nameError.isNotEmpty()) nameError = ""
        if (registerUiState.errorMessage != null) viewModel.clearRegisterError()
    }

    LaunchedEffect(email) {
        if (emailError.isNotEmpty()) emailError = ""
        if (registerUiState.errorMessage != null) viewModel.clearRegisterError()
    }

    LaunchedEffect(password) {
        if (passwordError.isNotEmpty()) passwordError = ""
        if (registerUiState.errorMessage != null) viewModel.clearRegisterError()
    }

    LaunchedEffect(confirmPassword) {
        if (confirmPasswordError.isNotEmpty()) confirmPasswordError = ""
    }

    LaunchedEffect(acceptTerms) {
        if (termsError.isNotEmpty()) termsError = ""
    }

    RegisterScreenContent(
        name = name,
        onNameChange = { name = it },
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        confirmPassword = confirmPassword,
        onConfirmPasswordChange = { confirmPassword = it },
        acceptTerms = acceptTerms,
        onAcceptTermsChange = { acceptTerms = it },
        isPasswordVisible = isPasswordVisible,
        onPasswordVisibilityChange = { isPasswordVisible = it },
        isConfirmPasswordVisible = isConfirmPasswordVisible,
        onConfirmPasswordVisibilityChange = { isConfirmPasswordVisible = it },
        nameError = nameError,
        emailError = emailError,
        passwordError = passwordError,
        confirmPasswordError = confirmPasswordError,
        termsError = termsError,
        isLoading = registerUiState.isLoading,
        errorMessage = registerUiState.errorMessage,
        onRegisterClick = { handleRegister() },
        onGoogleSignInClick = { handleGoogleSignIn() },
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: (userRole: UserRole) -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onGoogleSignIn: () -> Unit = {},
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var acceptTerms by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var termsError by remember { mutableStateOf("") }

    fun validateName(): Boolean {
        return when {
            name.isBlank() -> {
                nameError = "El nombre es obligatorio"
                false
            }
            name.length < 2 -> {
                nameError = "El nombre debe tener al menos 2 caracteres"
                false
            }
            else -> {
                nameError = ""
                true
            }
        }
    }

    fun validateEmail(): Boolean {
        return when {
            email.isBlank() -> {
                emailError = "El email es obligatorio"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
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

    fun validateConfirmPassword(): Boolean {
        return when {
            confirmPassword.isBlank() -> {
                confirmPasswordError = "Confirma tu contraseña"
                false
            }
            confirmPassword != password -> {
                confirmPasswordError = "Las contraseñas no coinciden"
                false
            }
            else -> {
                confirmPasswordError = ""
                true
            }
        }
    }

    fun validateTerms(): Boolean {
        return if (!acceptTerms) {
            termsError = "Debes aceptar los términos y condiciones"
            false
        } else {
            termsError = ""
            true
        }
    }

    fun handleRegister() {
        val isNameValid = validateName()
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()
        val isConfirmPasswordValid = validateConfirmPassword()
        val isTermsValid = validateTerms()

        if (isNameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid && isTermsValid) {
            onRegisterSuccess(UserRole.STUDENT)
        }
    }

    RegisterScreenContent(
        name = name,
        onNameChange = {
            name = it
            if (nameError.isNotEmpty()) nameError = ""
        },
        email = email,
        onEmailChange = {
            email = it
            if (emailError.isNotEmpty()) emailError = ""
        },
        password = password,
        onPasswordChange = {
            password = it
            if (passwordError.isNotEmpty()) passwordError = ""
        },
        confirmPassword = confirmPassword,
        onConfirmPasswordChange = {
            confirmPassword = it
            if (confirmPasswordError.isNotEmpty()) confirmPasswordError = ""
        },
        acceptTerms = acceptTerms,
        onAcceptTermsChange = {
            acceptTerms = it
            if (termsError.isNotEmpty()) termsError = ""
        },
        isPasswordVisible = isPasswordVisible,
        onPasswordVisibilityChange = { isPasswordVisible = it },
        isConfirmPasswordVisible = isConfirmPasswordVisible,
        onConfirmPasswordVisibilityChange = { isConfirmPasswordVisible = it },
        nameError = nameError,
        emailError = emailError,
        passwordError = passwordError,
        confirmPasswordError = confirmPasswordError,
        termsError = termsError,
        isLoading = isLoading,
        errorMessage = errorMessage,
        onRegisterClick = { handleRegister() },
        onGoogleSignInClick = onGoogleSignIn,
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
private fun RegisterScreenContent(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    acceptTerms: Boolean,
    onAcceptTermsChange: (Boolean) -> Unit,
    isPasswordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    isConfirmPasswordVisible: Boolean,
    onConfirmPasswordVisibilityChange: (Boolean) -> Unit,
    nameError: String,
    emailError: String,
    passwordError: String,
    confirmPasswordError: String,
    termsError: String,
    isLoading: Boolean,
    errorMessage: String?,
    onRegisterClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
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

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Únete a AgendAlly",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Crea tu cuenta para organizar tu vida académica",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

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
                        text = "Crear Cuenta",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Campo de nombre
                    OutlinedTextField(
                        value = name,
                        onValueChange = onNameChange,
                        label = { Text("Nombre completo") },
                        placeholder = { Text("Ej: Juan Pérez López") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = nameError.isNotEmpty(),
                        supportingText = if (nameError.isNotEmpty()) {
                            { Text(nameError, color = MaterialTheme.colorScheme.error) }
                        } else null,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de email
                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = { Text("Email") },
                        placeholder = { Text("ejemplo@correo.com") },
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
                        onValueChange = onPasswordChange,
                        label = { Text("Contraseña") },
                        placeholder = { Text("Mínimo 6 caracteres") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { onPasswordVisibilityChange(!isPasswordVisible) }
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de confirmar contraseña
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        label = { Text("Confirmar contraseña") },
                        placeholder = { Text("Repite tu contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (isConfirmPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { onConfirmPasswordVisibilityChange(!isConfirmPasswordVisible) }
                            ) {
                                Icon(
                                    imageVector = if (isConfirmPasswordVisible) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                    contentDescription = if (isConfirmPasswordVisible) {
                                        "Ocultar contraseña"
                                    } else {
                                        "Mostrar contraseña"
                                    }
                                )
                            }
                        },
                        isError = confirmPasswordError.isNotEmpty(),
                        supportingText = if (confirmPasswordError.isNotEmpty()) {
                            { Text(confirmPasswordError, color = MaterialTheme.colorScheme.error) }
                        } else null,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Checkbox de términos
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = acceptTerms,
                            onCheckedChange = onAcceptTermsChange
                        )
                        Text(
                            text = "Acepto los términos y condiciones",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (termsError.isNotEmpty()) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    if (termsError.isNotEmpty()) {
                        Text(
                            text = termsError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 40.dp, top = 4.dp)
                        )
                    }

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

                    // Botón de registro
                    Button(
                        onClick = onRegisterClick,
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
                                text = "Crear Cuenta",
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
                        onClick = onGoogleSignInClick,
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
                            Text(
                                text = "Registrarse con Google",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Link para login
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "¿Ya tienes cuenta? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        TextButton(
                            onClick = onNavigateToLogin,
                            enabled = !isLoading
                        ) {
                            Text(
                                text = "Inicia sesión",
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer
            Text(
                text = "Al registrarte, aceptas nuestros términos y condiciones",
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
private fun RegisterScreenPreview() {
    AcademicAllyTheme {
        RegisterScreen()
    }
}