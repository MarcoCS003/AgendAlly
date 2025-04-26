package com.example.academically.uiAcademicAlly

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spoke
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItemContent(
    val icon: ImageVector,
    val text: String,
    val ruta: String
){
    object Calendar: NavigationItemContent(icon = Icons.Default.CalendarMonth,"Calendar", "Calendar" )
    object Schedule: NavigationItemContent(icon = Icons.Default.AccessTime,"Schedule", "Schedule" )
    object Institute: NavigationItemContent(icon = Icons.Default.AccountBalance,"Institute", "Institute" )
    object Next: NavigationItemContent(icon = Icons.Default.Settings,"Settings", "Settings" )

    // Navegacion fuera de la Barra de navegacion

    object AddEvent: NavigationItemContent(icon = Icons.Default.CalendarMonth,"AddEvent", "AddEvent" )
}