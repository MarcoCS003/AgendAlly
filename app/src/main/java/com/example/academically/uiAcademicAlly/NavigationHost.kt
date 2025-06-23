package com.example.academically.uiAcademicAlly


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.academically.ui.screens.auth.RegisterScreenWithViewModel
import com.example.academically.ViewModel.*
import com.example.academically.data.local.database.AcademicAllyDatabase
import com.example.academically.data.repositorty.AuthRepository
import com.example.academically.data.repository.PersonalEventRepository
import com.example.academically.data.repositorty.ScheduleRepository
import com.example.academically.uiAcademicAlly.calendar.AddEventScreenWithViewModel
import com.example.academically.uiAcademicAlly.calendar.CalendarScreenWithViewModel
import com.example.academically.uiAcademicAlly.calendar.EditEventScreenWithViewModel
import com.example.academically.uiAcademicAlly.Organization.EventBlogScreen
import com.example.academically.uiAcademicAlly.Organization.OrganizationScreen
import com.example.academically.uiAcademicAlly.Organization.OrganizationsScreen
import com.example.academically.uiAcademicAlly.schedule.AddScheduleActivityScreenWithViewModel
import com.example.academically.uiAcademicAlly.schedule.EditScheduleActivityScreen
import com.example.academically.uiAcademicAlly.schedule.ScheduleScreenWithViewModel
import com.example.academically.uiAcademicAlly.settings.ConfigurationScreen
import com.example.academically.uiAcademicAlly.settings.LoginScreenWithViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationHost(
    navController: NavHostController
) {
    val context = LocalContext.current

    // ========== REPOSITORIOS SIMPLIFICADOS ==========
    // Crear repositorios con Room directamente (sin dependencias complejas)
    val scheduleRepository = remember {
        ScheduleRepository(AcademicAllyDatabase.getDatabase(context).scheduleDao())
    }

    val eventRepository = remember {
        PersonalEventRepository(AcademicAllyDatabase.getDatabase(context).personalEventDao())
    }

    // ========== VIEW MODELS SIMPLIFICADOS ==========
    val scheduleViewModel: ScheduleViewModel = viewModel(
        factory = ScheduleViewModelFactory(scheduleRepository)
    )

    val eventViewModel: EventViewModel = viewModel(
        factory = EventViewModel.Factory(eventRepository)
    )

    // ViewModels mock para desarrollo (sin API)
    val organizationViewModel: OrganizationViewModel = viewModel()

    // BlogEventsViewModel con datos mock
    val blogEventsViewModel: BlogEventsViewModel = viewModel()

    // ========== NAVEGACIÓN SIMPLIFICADA (SIN AUTENTICACIÓN) ==========
    // Empezar directamente en Calendar para desarrollo

    NavHost(
        navController = navController,
        startDestination = NavigationItemContent.Calendar.ruta // ✅ Directamente al Calendar
    ) {

        // ========== PANTALLAS PRINCIPALES (SIN VERIFICACIÓN DE AUTH) ==========
        composable(NavigationItemContent.Calendar.ruta) {
            CalendarScreenWithViewModel(
                viewModel = eventViewModel,
                onAddEventClick = {
                    navController.navigate(NavigationItemContent.AddEvent.ruta)
                },
                onEditEventClick = { event ->
                    navController.navigate("${NavigationItemContent.EditEvent.ruta}/${event.id}")
                }
            )
        }

        composable(NavigationItemContent.AddEvent.ruta) {
            AddEventScreenWithViewModel(
                viewModel = eventViewModel,
                onBack = { navController.navigateUp() }
            )
        }

        composable("${NavigationItemContent.EditEvent.ruta}/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
            if (eventId != null) {
                EditEventScreenWithViewModel(
                    eventId = eventId,
                    viewModel = eventViewModel,
                    onBack = { navController.navigateUp() }
                )
            } else {
                navController.navigateUp()
            }
        }

        composable(NavigationItemContent.Settings.ruta) {
            ConfigurationScreen(
                OnClicKRes = {
                    // Por ahora solo mostrar mensaje
                    // TODO: Implementar logout real cuando tengas autenticación
                }
            )
        }

        composable(NavigationItemContent.Organization.ruta) {
            OrganizationsScreen(
                onOrganizationClick = {
                    navController.navigate(NavigationItemContent.BlogOrganization.ruta)
                },
                onAddOrganizationClick = {
                    navController.navigate(NavigationItemContent.AddOrganization.ruta)
                },
                viewModel = TODO()
            )
        }

        composable(NavigationItemContent.AddOrganization.ruta) {
            OrganizationScreen(
                viewModel = organizationViewModel,
                onOrganizationAndChannelSelected = { organization, channel ->
                    // TODO: Implementar suscripción cuando tengas API
                    // organizationViewModel.subscribeToChannel(channel.id)
                    navController.navigateUp()
                }
            )
        }

        composable(NavigationItemContent.BlogOrganization.ruta) {
            EventBlogScreen(
                blogEventsViewModel = blogEventsViewModel,
                eventViewModel = eventViewModel,
                organizationViewModel = organizationViewModel,
                onNavigateToSubscriptions = {
                    navController.navigate(NavigationItemContent.AddOrganization.ruta)
                }
            )
        }

        composable(NavigationItemContent.Schedule.ruta) {
            ScheduleScreenWithViewModel(
                viewModel = scheduleViewModel,
                onAddActivity = {
                    navController.navigate(NavigationItemContent.AddEventSchedule.ruta)
                },
                onEditActivity = { schedule ->
                    navController.navigate("${NavigationItemContent.EditEventSchedule.ruta}/${schedule.id}")
                }
            )
        }

        composable("${NavigationItemContent.EditEventSchedule.ruta}/{scheduleId}") { backStackEntry ->
            val scheduleId = backStackEntry.arguments?.getString("scheduleId")?.toIntOrNull()
            if (scheduleId != null) {
                EditScheduleActivityScreen(
                    scheduleId = scheduleId,
                    viewModel = scheduleViewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            } else {
                navController.navigateUp()
            }
        }

        composable(NavigationItemContent.AddEventSchedule.ruta) {
            AddScheduleActivityScreenWithViewModel(
                viewModel = scheduleViewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // ========== PANTALLAS DE AUTENTICACIÓN (OPCIONALES) ==========
        composable(NavigationItemContent.Login.ruta) {
            // Pantalla de login simplificada (opcional para desarrollo)
            LoginScreenWithViewModel(
                viewModel = viewModel(), // Mock ViewModel
                onLoginSuccess = { userRole ->
                    navController.navigate(NavigationItemContent.Calendar.ruta) {
                        popUpTo(NavigationItemContent.Login.ruta) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(NavigationItemContent.Register.ruta)
                }
            )
        }

        composable(NavigationItemContent.Register.ruta) {
            RegisterScreenWithViewModel(
                viewModel = viewModel(), // Mock ViewModel
                onRegisterSuccess = { userRole ->
                    navController.navigate(NavigationItemContent.Calendar.ruta) {
                        popUpTo(NavigationItemContent.Register.ruta) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigateUp()
                }
            )
        }
    }

    // ========== SIN OBSERVADOR DE AUTENTICACIÓN ==========
    // La app inicia directamente en Calendar sin verificaciones
}