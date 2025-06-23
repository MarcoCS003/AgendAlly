package com.example.academically.uiAcademicAlly.Organization

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.academically.ViewModel.OrganizationViewModel
import com.example.academically.data.model.Organization
import com.example.academically.data.mappers.ChannelDomain

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrganizationScreen(
    viewModel: OrganizationViewModel,
    onOrganizationAndChannelSelected: (Organization, ChannelDomain) -> Unit
) {
    // Estados para controlar el flujo de selección
    var selectedOrganization by remember { mutableStateOf<Organization?>(null) }
    var showChannelSelection by remember { mutableStateOf(false) }

    // Pantalla principal
    OrganizationSearchScreen(
        viewModel = viewModel,
        onOrganizationSelected = { organization ->
            selectedOrganization = organization
            viewModel.selectOrganization(organization.organizationID)
            showChannelSelection = true
        }
    )

    // Mostrar diálogo de selección de canal si corresponde
    if (showChannelSelection && selectedOrganization != null) {
        ChannelSelectionDialog(
            organization = selectedOrganization!!,
            channels = viewModel.channels,
            isLoading = viewModel.isLoading,
            onDismiss = {
                showChannelSelection = false
                selectedOrganization = null
            },
            onChannelSelected = { channel ->
                onOrganizationAndChannelSelected(selectedOrganization!!, channel)
                showChannelSelection = false
                selectedOrganization = null
            }
        )
    }
}

@Composable
fun ChannelSelectionDialog(
    organization: Organization,
    channels: List<ChannelDomain>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onChannelSelected: (ChannelDomain) -> Unit
) {
    // Estado para el canal seleccionado
    var selectedChannel by remember { mutableStateOf<ChannelDomain?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Selecciona un canal",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = organization.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                when {
                    isLoading -> {
                        // Mostrar indicador de carga
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Cargando canales...")
                            }
                        }
                    }

                    channels.isEmpty() -> {
                        // Mostrar mensaje si no hay canales disponibles
                        NoChannelsAvailableMessage()
                    }

                    else -> {
                        // Componente de dropdown para canales
                        ChannelDropdown(
                            channels = channels,
                            selectedChannel = selectedChannel,
                            onChannelSelected = { selectedChannel = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            selectedChannel?.let { onChannelSelected(it) }
                        },
                        enabled = selectedChannel != null && !isLoading
                    ) {
                        Text("Seleccionar")
                    }
                }
            }
        }
    }
}

@Composable
fun ChannelDropdown(
    channels: List<ChannelDomain>,
    selectedChannel: ChannelDomain?,
    onChannelSelected: (ChannelDomain) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedChannel?.name ?: "",
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            label = { Text("Selecciona un canal") },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Contraer" else "Expandir",
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }
        )

        // Área invisible para hacer clic
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = !expanded }
        )

        // Popup con la lista de canales
        if (expanded && channels.isNotEmpty()) {
            Popup(
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .heightIn(max = 300.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    LazyColumn {
                        items(channels) { channel ->
                            ChannelDropdownItem(
                                channel = channel,
                                isSelected = channel.id == selectedChannel?.id,
                                onClick = {
                                    onChannelSelected(channel)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChannelDropdownItem(
    channel: ChannelDomain,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Column {
                Text(
                    text = channel.name,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "${getChannelTypeDisplayName(channel.type)} • ${channel.organizationName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        onClick = onClick,
        leadingIcon = {
            Icon(
                imageVector = getChannelTypeIcon(channel.type),
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        colors = MenuDefaults.itemColors(
            textColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun NoChannelsAvailableMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Advertencia",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "No hay canales disponibles para esta organización",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Por favor, selecciona otra organización o contacta al administrador",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.alpha(0.7f)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrganizationSearchScreen(
    viewModel: OrganizationViewModel,
    onOrganizationSelected: (Organization) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 28.dp, horizontal = 16.dp)
    ) {
        // Mostrar error si existe
        viewModel.error?.let { errorMessage ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚠️ $errorMessage",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("Cerrar")
                    }
                }
            }
        }

        // Barra de búsqueda
        OutlinedTextField(
            value = viewModel.searchQuery,
            onValueChange = { viewModel.searchOrganizations(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Buscar Organización o Universidad") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            shape = RoundedCornerShape(4.dp),
            singleLine = true
        )

        // Indicador de carga
        if (viewModel.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cargando organizaciones...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Grid de organizaciones
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(viewModel.organizations) { organization ->
                OrganizationCard(
                    organization = organization,
                    onClick = { onOrganizationSelected(organization) }
                )
            }
        }

        // Información y botón de recarga
        if (!viewModel.isLoading) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (viewModel.organizations.isEmpty() && viewModel.searchQuery.isNotBlank()) {
                    Text(
                        text = "No se encontraron organizaciones para '${viewModel.searchQuery}'",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                TextButton(
                    onClick = { viewModel.loadOrganizations() }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Recargar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Recargar desde servidor")
                }

                // Mostrar total de organizaciones
                Text(
                    text = "${viewModel.organizations.size} organizaciones disponibles",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun OrganizationCard(
    organization: Organization,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Acrónimo
            Text(
                text = organization.acronym,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Nombre completo (truncado)
            Text(
                text = organization.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Número de estudiantes
            Text(
                text = "${organization.studentNumber} estudiantes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 9.sp
            )

            // Número de canales disponibles
            Text(
                text = "${organization.channels.size} canales",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 8.sp
            )
        }
    }
}

// Funciones auxiliares para tipos de canal
fun getChannelTypeDisplayName(type: String): String {
    return when (type.uppercase()) {
        "CAREER" -> "Carrera"
        "DEPARTMENT" -> "Departamento"
        "ADMINISTRATIVE" -> "Administrativo"
        else -> "Canal"
    }
}
