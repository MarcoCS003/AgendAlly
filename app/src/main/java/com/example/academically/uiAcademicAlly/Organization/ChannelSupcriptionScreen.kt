package com.example.academically.uiAcademicAlly.Organization


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Business
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
import com.example.academically.data.mappers.ChannelDomain
import com.example.academically.data.model.ChannelType

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChannelSubscriptionScreen(
    viewModel: OrganizationViewModel,
    organizationName: String,
    onBack: () -> Unit = {}
) {
    val channels = viewModel.channels
    val isLoading = viewModel.isLoading
    val error = viewModel.error

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.Remove, // O usar un ícono de flecha hacia atrás
                    contentDescription = "Volver"
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Canales",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = organizationName,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contenido principal
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                ErrorSection(
                    error = error,
                    onRetry = {
                        viewModel.clearError()
                        viewModel.selectedOrganizationId?.let { orgId ->
                            viewModel.loadChannelsForOrganization(orgId)
                        }
                    }
                )
            }

            channels.isEmpty() -> {
                EmptyChannelsSection()
            }

            else -> {
                ChannelsList(
                    channels = channels,
                    viewModel = viewModel
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChannelsList(
    channels: List<ChannelDomain>,
    viewModel: OrganizationViewModel
) {
    // Agrupar canales por tipo
    val groupedChannels = channels.groupBy { it.type }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groupedChannels.forEach { (channelType, channelsInType) ->
            item {
                ChannelTypeHeader(channelType = channelType)
            }

            items(
                items = channelsInType,
                key = { it.id }
            ) { channel ->
                ChannelSubscriptionCard(
                    channel = channel,
                    isSubscribed = viewModel.isSubscribedToChannel(channel.id),
                    onSubscriptionToggle = { isSubscribed ->
                        if (isSubscribed) {
                            viewModel.subscribeToChannel(channel.id)
                        } else {
                            viewModel.unsubscribeFromChannel(channel.id)
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ChannelTypeHeader(channelType: String) {
    Text(
        text = when (channelType) {
            "CAREER" -> "Carreras"
            "DEPARTMENT" -> "Departamentos"
            "ADMINISTRATIVE" -> "Administrativo"
            else -> channelType
        },
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun ChannelSubscriptionCard(
    channel: ChannelDomain,
    isSubscribed: Boolean,
    onSubscriptionToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSubscribed) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del canal
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(getChannelTypeColor(channel.type)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getChannelTypeIcon(channel.type),
                    contentDescription = channel.name,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del canal
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = channel.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = channel.acronym,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                if (channel.description.isNotBlank()) {
                    Text(
                        text = channel.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Botón de suscripción
            if (isSubscribed) {
                FilledTonalButton(
                    onClick = { onSubscriptionToggle(false) },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Desuscribirse",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Quitar")
                }
            } else {
                FilledTonalButton(
                    onClick = { onSubscriptionToggle(true) },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Suscribirse",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Agregar")
                }
            }
        }
    }
}

@Composable
fun EmptyChannelsSection() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.School,
            contentDescription = "Sin canales",
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No hay canales disponibles",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Esta organización aún no tiene canales configurados",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, start = 32.dp, end = 32.dp)
        )
    }
}

// Funciones de utilidad
fun getChannelTypeColor(type: String): Color {
    return when (type) {
        "CAREER" -> Color(0xFF4CAF50) // Verde
        "DEPARTMENT" -> Color(0xFF2196F3) // Azul
        "ADMINISTRATIVE" -> Color(0xFFFF9800) // Naranja
        else -> Color(0xFF9E9E9E) // Gris
    }
}

fun getChannelTypeIcon(type: String): ImageVector {
    return when (type) {
        "CAREER" -> Icons.Default.School
        "DEPARTMENT" -> Icons.Default.Business
        "ADMINISTRATIVE" -> Icons.Default.Work
        else -> Icons.Default.School
    }
}

@Preview(showBackground = true)
@Composable
fun ChannelSubscriptionScreenPreview() {
    MaterialTheme {
        // Preview sería con datos mock del viewModel
    }
}