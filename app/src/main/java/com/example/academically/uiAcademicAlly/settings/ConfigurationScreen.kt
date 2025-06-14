package com.example.academically.uiAcademicAlly.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConfigurationScreen(
    OnClicKRes: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 28.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            // Título principal
            Text(
                text = "Configuración",
                color = contentColorFor(
                    if (isSystemInDarkTheme()){
                        Color.White
                    }else{
                        Color.Black
                    }
                ),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            // Sección Cuenta
            ConfigurationSection(
                title = "Cuenta",
                items = listOf(
                    ConfigItem("Perfil", Icons.Default.Person),
                    ConfigItem("Notificaciones", Icons.Default.Notifications),
                    ConfigItem("Organizaciones", Icons.Default.Business)
                ),
                onClicKRes = OnClicKRes
            )
        }
        /* Se integrara en fituras vesiones
        item {
            // Sección Soporte
            ConfigurationSection(
                title = "Soporte",
                items = listOf(
                    ConfigItem("Centro de ayuda", Icons.Default.Help),
                    ConfigItem("Sugerencias", Icons.Default.Lightbulb),
                    ConfigItem("Política de privacidad", Icons.Default.Security)
                )
            )
        }
        */
        item {
            // Términos y Agradecimientos
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TÉRMINOS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "AGRADECIMIENTOS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun ConfigurationSection(
    title: String,
    items: List<ConfigItem>,
    onClicKRes: () -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    ConfigurationButton(
                        text = item.title,
                        icon = item.icon,
                        onClick = onClicKRes
                    )

                    // Agregar divider entre items (excepto el último)
                    if (index < items.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = Color.Gray.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConfigurationButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (isSystemInDarkTheme())
                Color.White
            else
                Color.Black
        ),
        contentPadding = PaddingValues(16.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Clase de datos para los items de configuración
data class ConfigItem(
    val title: String,
    val icon: ImageVector
)

@Preview(showBackground = true)
@Composable
fun ConfigurationScreenPreview() {
    MaterialTheme {
        ConfigurationScreen(
            OnClicKRes = TODO()
        )
    }
}