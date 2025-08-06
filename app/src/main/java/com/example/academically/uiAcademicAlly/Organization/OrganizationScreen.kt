package com.example.academically.uiAcademicAlly.Organization

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.modifier.ModifierLocalModifierNode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academically.ViewModel.OrganizationViewModel
import com.example.academically.data.api.Organization
import com.example.academically.data.database.AcademicAllyDatabase
import com.example.academically.data.repositorty.OrganizationRepository

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrganizationsScreen(
    onOrganizationClick: (Organization) -> Unit,
    onAddOrganizationClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrganizationViewModel
) {
    // ViewModel setup
    val context = LocalContext.current
    val database = AcademicAllyDatabase.getDatabase(context)
    val repository = OrganizationRepository(database.organizationDao())


    // Estados del ViewModel
    val organizations by viewModel.organizations.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header simple
        Text(
            text = "Mis Organizaciones",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Error message
        errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Content
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            organizations.isEmpty() -> {
                EmptyOrganizationsState(
                    onAddOrganizationClick = onAddOrganizationClick,
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                // ✅ VISTA SIMPLIFICADA CUANDO HAY ORGANIZACIONES
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Organizaciones simplificadas
                    items(organizations) { organization ->
                        SimpleOrganizationCard(
                            organization = organization,
                            onClick = { onOrganizationClick(organization) },
                            modifier = Modifier.fillMaxWidth(),
                            organizationViewModel = viewModel
                        )
                    }

                    // Botón para agregar nueva organización
                    item {
                        AddOrganizationButton(
                            onClick = onAddOrganizationClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

// ✅ CARD SIMPLIFICADA - SOLO LOGO Y ACCEDER A BLOG
// ✅ CARD MEJORADA CON BOTÓN DE ELIMINAR Y ALERTDIALOG
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SimpleOrganizationCard(
    organization: Organization,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    organizationViewModel: OrganizationViewModel
) {
    // Estado para controlar el AlertDialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCalendarDialog by remember { mutableStateOf(false) }
    var isDownloadingCalendar by remember { mutableStateOf(false) }
    Card(
        modifier = modifier
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Contenido principal de la card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo placeholder
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (organization.logoUrl != null) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Logo ${organization.acronym}",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        // Logo placeholder con acrónimo
                        Card(
                            modifier = Modifier.size(80.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = organization.acronym,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nombre de la organización
                Text(
                    text = organization.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Acrónimo
                Text(
                    text = organization.acronym,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onClick,
                        modifier = Modifier.fillMaxWidth(0.7f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Article,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Acceder a Blog",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { showCalendarDialog = true }, // ✅ CAMBIO: mostrar dialog
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        enabled = !isDownloadingCalendar // ✅ NUEVO: deshabilitar durante descarga
                    ) {
                        if (isDownloadingCalendar) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Botón de eliminar en la esquina superior derecha
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Eliminar organización",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    // AlertDialog de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
            },
            title = {
                Text(
                    text = "Eliminar Organización",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "¿Estás seguro de que deseas eliminar la organización:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = organization.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Esta acción no se puede deshacer. Perderás el acceso a sus eventos y contenido.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        organizationViewModel.deleteOrganization(organization.organizationID)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
    if (showCalendarDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isDownloadingCalendar) showCalendarDialog = false
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            },
            title = {
                Text(
                    text = "Descargar Calendario",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "¿Deseas agregar todos los eventos del calendario de:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = organization.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Esto agregará automáticamente todos los eventos programados de la organización a tu calendario personal.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isDownloadingCalendar = true
                        organizationViewModel.downloadOrganizationCalendar(
                            organizationId = organization.organizationID,
                            onSuccess = {
                                isDownloadingCalendar = false
                                showCalendarDialog = false
                            },
                            onError = { error ->
                                isDownloadingCalendar = false
                                // TODO: Mostrar mensaje de error
                            }
                        )
                    },
                    enabled = !isDownloadingCalendar,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isDownloadingCalendar) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Descargando...")
                    } else {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Descargar")
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showCalendarDialog = false },
                    enabled = !isDownloadingCalendar
                ) {
                    Text("Cancelar")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

}

// ✅ BOTÓN PARA AGREGAR ORGANIZACIÓN SIMPLIFICADO
@Composable
fun AddOrganizationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier
            .clickable { onClick() },
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 2.dp,
            brush = SolidColor(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar organización",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Agregar Nueva Organización",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Buscar y añadir más organizaciones",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ✅ ESTADO VACÍO (MANTENER IGUAL)
@Composable
fun EmptyOrganizationsState(
    onAddOrganizationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.School,
            contentDescription = "Sin organizaciones",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No tienes organizaciones guardadas",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Agrega tu primera organización para comenzar a recibir eventos y noticias",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAddOrganizationClick,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Agregar Organización")
        }
    }
}