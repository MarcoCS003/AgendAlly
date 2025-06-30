package com.example.academically.uiAcademicAlly

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.academically.ViewModel.BlogEventsViewModel
import com.example.academically.ViewModel.EventViewModel
import com.example.academically.ViewModel.OrganizationViewModel
import com.example.academically.ViewModel.ScheduleViewModel
import com.example.academically.ViewModel.ScheduleViewModelFactory
import com.example.academically.data.api.ApiService
import com.example.academically.data.database.AcademicAllyDatabase
import com.example.academically.data.repositorty.OrganizationRepository
import com.example.academically.data.repository.EventRepository
import com.example.academically.data.repositorty.ScheduleRepository
import com.example.academically.uiAcademicAlly.Organization.OrganizationInfoScreen
import com.example.academically.uiAcademicAlly.Organization.OrganizationsScreen
import com.example.academically.uiAcademicAlly.calendar.AddEventScreenWithViewModel
import com.example.academically.uiAcademicAlly.calendar.CalendarScreenWithViewModel
import com.example.academically.uiAcademicAlly.calendar.EditEventScreenWithViewModel
import com.example.academically.uiAcademicAlly.institute.EventBlogScreenWithAPI
import com.example.academically.uiAcademicAlly.schedule.AddScheduleActivityScreenWithViewModel
import com.example.academically.uiAcademicAlly.schedule.EditScheduleActivityScreen
import com.example.academically.uiAcademicAlly.schedule.ScheduleScreenWithViewModel
import com.example.academically.uiAcademicAlly.settings.ConfigurationScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationHost(
    navController: NavHostController,
    isTablet: Boolean = false // Parámetro mantenido por compatibilidad
) {
    val context = LocalContext.current
    val database = AcademicAllyDatabase.getDatabase(context)

    // ========== API SERVICE ==========
    val apiService = ApiService()

    // ========== REPOSITORIOS ==========
    val scheduleRepository = ScheduleRepository(database.scheduleDao())
    val eventRepository = EventRepository(database.eventDao())
    val organizationRepository = OrganizationRepository(database.organizationDao())

    // ========== VIEW MODELS ==========
    val scheduleViewModel: ScheduleViewModel = viewModel(
        factory = ScheduleViewModelFactory(scheduleRepository)
    )

    val eventViewModel: EventViewModel = viewModel(
        factory = EventViewModel.Factory(eventRepository)
    )

    val blogEventsViewModel: BlogEventsViewModel = viewModel(
        factory = BlogEventsViewModel.Factory(apiService)
    )

    val organizationViewModel: OrganizationViewModel = viewModel(
        factory = OrganizationViewModel.Factory(organizationRepository)
    )

    NavHost(
        navController = navController,
        startDestination = NavigationItemContent.Calendar.ruta
    ) {

        // ========== CALENDARIO ==========
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
                onBack = {
                    navController.navigateUp()
                }
            )
        }

        composable("${NavigationItemContent.EditEvent.ruta}/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
            if (eventId != null) {
                EditEventScreenWithViewModel(
                    eventId = eventId,
                    viewModel = eventViewModel,
                    onBack = {
                        navController.navigateUp()
                    }
                )
            } else {
                navController.navigateUp()
            }
        }

        // ========== ORGANIZACIONES ==========
        composable(NavigationItemContent.Institute.ruta) {
            OrganizationsScreen(
                onOrganizationClick = { organization ->
                    // Navegar a eventos de la organización con ID
                    navController.navigate("${NavigationItemContent.BlogOrganization.ruta}/${organization.organizationID}")
                },
                onAddOrganizationClick = {
                    // Navegar a búsqueda de organizaciones
                    navController.navigate(NavigationItemContent.AddOrganization.ruta)
                }
            )
        }

        composable(NavigationItemContent.AddOrganization.ruta) {
            OrganizationInfoScreen(
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }

        // ========== EVENTOS DE ORGANIZACIÓN ==========
        composable(NavigationItemContent.BlogOrganization.ruta) {
            // Mostrar todos los eventos (sin filtro de organización)
            EventBlogScreenWithAPI(
                blogEventsViewModel = blogEventsViewModel,
                eventViewModel = eventViewModel,
                modifier = Modifier
            )
        }

        // NUEVA RUTA: Eventos filtrados por organización específica
        composable("${NavigationItemContent.BlogOrganization.ruta}/{organizationId}") { backStackEntry ->
            val organizationId = backStackEntry.arguments?.getString("organizationId")?.toIntOrNull()

            if (organizationId != null) {
                EventBlogScreenWithAPI(
                    blogEventsViewModel = blogEventsViewModel,
                    eventViewModel = eventViewModel,
                    organizationId = organizationId, // ✅ PASAR ID DE ORGANIZACIÓN
                    modifier = Modifier
                )

                // Filtrar eventos por organización al entrar
                LaunchedEffect(organizationId) {
                    blogEventsViewModel.filterEventsByOrganization(organizationId)
                }
            } else {
                // ID inválido, volver atrás
                navController.navigateUp()
            }
        }

        // ========== HORARIOS ==========
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

        composable(NavigationItemContent.AddEventSchedule.ruta) {
            AddScheduleActivityScreenWithViewModel(
                viewModel = scheduleViewModel,
                onNavigateBack = { navController.navigateUp() }
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

        // ========== CONFIGURACIÓN ==========
        composable(NavigationItemContent.Settings.ruta) {
            ConfigurationScreen()
        }
    }
}