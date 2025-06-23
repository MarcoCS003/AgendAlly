package com.example.academically.uiAcademicAlly.Organization

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.academically.ViewModel.OrganizationViewModel
import com.example.academically.data.model.Organization

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrganizationsScreen(
    viewModel: OrganizationViewModel,
    onOrganizationClick: (Organization) -> Unit = {},
    onAddOrganizationClick: () -> Unit = {}
) {
    val organizations = viewModel.organizations
    val isLoading = viewModel.isLoading
    val error = viewModel.error

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 28.dp, horizontal = 16.dp)
    ) {
        // Título
        Text(
            text = "Organizaciones",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Estado de carga
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Column
        }

        // Estado de error
        if (error != null) {
            ErrorSection(
                error = error,
                onRetry = {
                    viewModel.clearError()
                    viewModel.loadOrganizations()
                }
            )
            return@Column
        }

        // Lista de organizaciones
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = organizations.chunked(2),
                key = { chunk -> chunk.map { it.organizationID }.joinToString() }
            ) { organizationPair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Primera organización
                    OrganizationCard(
                        organization = organizationPair[0],
                        onClickOrganization = { onOrganizationClick(organizationPair[0]) },
                        modifier = Modifier.weight(1f)
                    )

                    // Segunda organización o botón de agregar
                    if (organizationPair.size > 1) {
                        OrganizationCard(
                            organization = organizationPair[1],
                            onClickOrganization = { onOrganizationClick(organizationPair[1]) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        AddOrganizationCard(
                            onClickAddOrganization = onAddOrganizationClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Si el número de organizaciones es par, agregamos el botón de agregar en una nueva fila
            if (organizations.size % 2 == 0) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AddOrganizationCard(
                            onClickAddOrganization = onAddOrganizationClick,
                            modifier = Modifier.weight(1f)
                        )
                        // Espacio vacío para mantener la alineación
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun OrganizationCard(
    organization: Organization,
    onClickOrganization: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1.2f)
            .clickable { onClickOrganization() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo/Ícono de la organización
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(getOrganizationColor(organization.organizationID)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getOrganizationIcon(organization.acronym),
                    contentDescription = organization.name,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Acrónimo de la organización
            Text(
                text = organization.acronym,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            // Nombre completo (más pequeño)
            Text(
                text = organization.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2
            )
        }
    }
}

@Composable
fun AddOrganizationCard(
    onClickAddOrganization: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1.2f)
            .clickable { onClickAddOrganization() },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar organización",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    text = "Agregar\nOrganización",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ErrorSection(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Error al cargar organizaciones",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )

        Text(
            text = error,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRetry
        ) {
            Text("Reintentar")
        }
    }
}

// Funciones de utilidad para colores e iconos
fun getOrganizationColor(id: Int): Color {
    return when (id % 5) {
        0 -> Color(0xFF2196F3) // Azul
        1 -> Color(0xFF4CAF50) // Verde
        2 -> Color(0xFFFF9800) // Naranja
        3 -> Color(0xFF9C27B0) // Púrpura
        4 -> Color(0xFFF44336) // Rojo
        else -> Color(0xFF607D8B) // Gris azul
    }
}

fun getOrganizationIcon(acronym: String): ImageVector {
    return when {
        acronym.contains("TECN", ignoreCase = true) -> Icons.Default.School
        else -> Icons.Default.School
    }
}

@Preview(showBackground = true)
@Composable
fun OrganizationsScreenPreview() {
    MaterialTheme {
        // Preview con datos simulados
        val sampleOrganizations = listOf(
            Organization(
                organizationID = 1,
                acronym = "ITP",
                name = "Instituto Tecnológico de Puebla",
                address = "Del Tecnológico 420, Puebla",
                email = "info@puebla.tecnm.mx",
                phone = "222 229 8810",
                studentNumber = 6284,
                teacherNumber = 298
            )
        )

        // Simular el viewModel para el preview
        // En el preview real, necesitarías crear un viewModel mock
    }
}