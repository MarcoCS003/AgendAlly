package com.example.academically.uiAcademicAlly.institute


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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


data class Organization(
    val id: String,
    val name: String,
    val shortName: String,
    val logoColor: Color = Color(0xFFFFD700), // Amarillo por defecto
    val icon: ImageVector = Icons.Default.School
)

@Composable
fun OrganizationsScreen(
    organizations: List<Organization> = getSampleOrganizations(),
    onOrganizationClick:  (Organization) -> Unit = {},
    onAddOrganizationClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 28.dp ,horizontal = 16.dp)
    ) {
        // Título
        Text(
            text = "Organizaciones",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = contentColorFor(
                if (isSystemInDarkTheme()) {
                    Color.White
                }else{
                    Color.White
                }
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Grid de organizaciones
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = organizations.chunked(2), // Agrupamos de 2 en 2 para el grid
                key = { chunk -> chunk.map { it.id }.joinToString() }
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
                            onClickAddOrganization =  onAddOrganizationClick,
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
                    .background(organization.logoColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = organization.icon,
                    contentDescription = organization.name,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nombre de la organización
            Text(
                text = organization.shortName,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
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
                    color = Color.Gray.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar organización",
                tint = Color.Gray,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

// Función para datos de ejemplo (en el futuro vendrán de la base de datos)
fun getSampleOrganizations(): List<Organization> {
    return listOf(
        Organization(
            id = "1",
            name = "Instituto Tecnológico de Puebla",
            shortName = "ITP",
            logoColor = Color(0xFFFFD700), // Amarillo
            icon = Icons.Default.School
        )
        // Agregar más organizaciones aquí cuando las tengas
    )
}

@Preview(showBackground = true)
@Composable
fun OrganizationsScreenPreview() {
    MaterialTheme {
        OrganizationsScreen(
            organizations = getSampleOrganizations(),
            onOrganizationClick = { org ->
                println("Clicked on ${org.name}")
            },
            onAddOrganizationClick = {
                println("Add organization clicked")
            }
        )
    }
}
