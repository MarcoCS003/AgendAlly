package com.agendally.app.uiAcademicAlly

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
import com.agendally.app.ViewModel.BlogEventsViewModel
import com.agendally.app.ViewModel.EventViewModel
import com.agendally.app.ViewModel.OrganizationViewModel
import com.agendally.app.ViewModel.ScheduleViewModel
import com.agendally.app.ViewModel.ScheduleViewModelFactory
import com.agendally.app.data.api.ApiService
import com.agendally.app.data.database.AcademicAllyDatabase
import com.agendally.app.data.repositorty.OrganizationRepository
import com.agendally.app.data.repository.EventRepository
import com.agendally.app.data.repositorty.ScheduleRepository
import com.agendally.app.uiAcademicAlly.Organization.OrganizationInfoScreen
import com.agendally.app.uiAcademicAlly.Organization.OrganizationsScreen
import com.agendally.app.uiAcademicAlly.calendar.AddEventScreenWithViewModel
import com.agendally.app.uiAcademicAlly.calendar.CalendarScreenWithViewModel
import com.agendally.app.uiAcademicAlly.calendar.EditEventScreenWithViewModel
import com.agendally.app.uiAcademicAlly.institute.EventBlogScreenWithAPI
import com.agendally.app.uiAcademicAlly.schedule.AddScheduleActivityScreenWithViewModel
import com.agendally.app.uiAcademicAlly.schedule.EditScheduleActivityScreen
import com.agendally.app.uiAcademicAlly.schedule.ScheduleScreenWithViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationHost(
    navController: NavHostController,
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
        factory = OrganizationViewModel.Factory(organizationRepository, eventRepository)
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
                onBack = { navController.navigateUp() }
            )
        }

        composable("${NavigationItemContent.EditEvent.ruta}/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
            if (eventId != null) {
                EditEventScreenWithViewModel(
                    eventId = eventId,
                    viewModel = eventViewModel,
                    onNavigateBack = {
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
                },
                viewModel = organizationViewModel
            )
        }

        composable(NavigationItemContent.AddOrganization.ruta) {
            OrganizationInfoScreen(
                onBackPressed = {
                    navController.navigateUp()
                },
                blogEventsViewModel = blogEventsViewModel
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

    }
}