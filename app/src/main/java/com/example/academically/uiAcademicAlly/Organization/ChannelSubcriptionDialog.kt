package com.example.academically.uiAcademicAlly.Organization

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.academically.ViewModel.OrganizationViewModel
import com.example.academically.data.api.Channel
import com.example.academically.data.api.ChannelType

@Composable
fun ChannelSubscriptionDialog(
    channels: List<Channel>,
    organizationId: Int, // ✅ NUEVO: ID específico de organización
    organizationViewModel: OrganizationViewModel,
    onDismiss: () -> Unit,
    onSubscriptionsChanged: (List<Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados de suscripciones desde Room
    var subscriptionsState by remember { mutableStateOf<Map<Int, Boolean>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(false) }

    // Inicializar suscripciones existentes de esta organización
    LaunchedEffect(channels, organizationId) {
        val subscriptions = mutableMapOf<Int, Boolean>()
        channels.forEach { channel ->
            // ✅ USAR AWAIT para función suspend
            val isSubscribed = organizationViewModel.isSubscribedToChannel(
                organizationId = organizationId,
                channelId = channel.id
            )
            subscriptions[channel.id] = isSubscribed
        }
        subscriptionsState = subscriptions
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gestionar Canales",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Selecciona los canales de los que quieres recibir eventos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ✅ LISTA SIMPLE SIN AGRUPACIÓN POR CATEGORÍAS
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Mostrar todos los canales directamente
                    items(channels) { channel ->
                        ChannelSubscriptionItem(
                            channel = channel,
                            isSubscribed = subscriptionsState[channel.id] ?: false,
                            onSubscriptionChanged = { isSubscribed ->
                                subscriptionsState = subscriptionsState.toMutableMap().apply {
                                    this[channel.id] = isSubscribed
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            isLoading = true
                            val subscribedChannelIds = subscriptionsState
                                .filter { it.value }
                                .keys
                                .toList()

                            // ✅ ACTUALIZAR SUSCRIPCIONES DE ESTA ORGANIZACIÓN
                            organizationViewModel.updateChannelSubscriptions(
                                organizationId = organizationId,
                                channelIds = subscribedChannelIds
                            )

                            onSubscriptionsChanged(subscribedChannelIds)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChannelTypeHeader(
    channelType: ChannelType,
    modifier: Modifier = Modifier
) {
    val (title, icon) = when (channelType) {
        ChannelType.CAREER -> "Carreras" to Icons.Default.School
        ChannelType.DEPARTMENT -> "Departamentos" to Icons.Default.Business
        ChannelType.ADMINISTRATIVE -> "Administrativo" to Icons.Default.AccountBox
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }

    HorizontalDivider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        thickness = 1.dp
    )
}

@Composable
fun ChannelSubscriptionItem(
    channel: Channel,
    isSubscribed: Boolean,
    onSubscriptionChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSubscribed) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
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
            // Información del canal
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = channel.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (channel.acronym.isNotEmpty()) {
                    Text(
                        text = channel.acronym,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Switch de suscripción
            Switch(
                checked = isSubscribed,
                onCheckedChange = onSubscriptionChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}