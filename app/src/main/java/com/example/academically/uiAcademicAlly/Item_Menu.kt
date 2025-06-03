package com.example.academically.uiAcademicAlly

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItemContent(
    val icon: ImageVector,
    val text: String,
    val ruta: String
){
    data object Calendar: NavigationItemContent(icon = Icons.Default.CalendarMonth,"Calendar", "Calendar" )
    data object Schedule: NavigationItemContent(icon = Icons.Default.AccessTime,"Schedule", "Schedule" )
    data object Institute: NavigationItemContent(icon = Icons.Default.AccountBalance,"Institute", "Institute" )
    data object Settings: NavigationItemContent(icon = Icons.Default.Settings,"Settings", "Settings" )

    // Navegacion fuera de la Barra de navegacion

    data object AddEvent: NavigationItemContent(icon = Icons.Default.CalendarMonth,"AddEvent", "AddEvent" )
    data object AddEventSchedule: NavigationItemContent(icon = Icons.Default.Schedule,"AddEventSchedule", "AddEventSchedule")
    data object EditEventSchedule: NavigationItemContent(icon = Icons.Default.Schedule,"EditSchedule", "EditSchedule")
    data object EditEvent : NavigationItemContent(Icons.Default.CalendarMonth, "EditEvent", "EditEvent")
    data object AddOrganization : NavigationItemContent(Icons.Default.AccountBalance, "AddOrganization", "AddOrganization")
    data object BlogOrganization : NavigationItemContent(Icons.Default.AccountBalance, "BlogOrganization", "BlogOrganization")
}